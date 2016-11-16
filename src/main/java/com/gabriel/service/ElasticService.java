package com.gabriel.service;

import com.gabriel.domain.Job;
import com.gabriel.repository.JobRepository;
import com.gabriel.repository.search.JobSearchRepository;
import com.gabriel.web.rest.DTO.StateDTO;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * Created by liuyufei on 14/11/16.
 */

@Service
@Transactional
public class ElasticService {

    private final Logger log = LoggerFactory.getLogger(ElasticService.class);

//    @Inject
//    JobService jobService;

    @Inject
    ElasticsearchTemplate elasticsearchTemplate;

    @Inject
    JobRepository jobRepository;

    @Inject
    JobSearchRepository jobSearchRepository;


    @Async
    public void updateSynonyms(){

//        https://gist.github.com/flying3615/19d3c585e8c9c87461d4865d4eaf67bb
//        cannot update dynamic settings exception, only have to delete all and re-create

        Client client =  elasticsearchTemplate.getClient();

        //delete all
        client.admin().indices().delete(new DeleteIndexRequest("job")).actionGet();

        log.info("after deleting all");

        //update settings

        String settings =  "{  \n" +
            "      \"analysis\":{  \n" +
            "         \"filter\":{  \n" +
            "            \"my_synonym_filter\":{  \n" +
            "               \"type\":\"synonym\",\n" +
            "               \"synonyms\":" + getSynonyms() +
            "            }\n" +
            "         },\n" +
            "         \"analyzer\":{  \n" +
            "            \"my_synonyms\":{  \n" +
            "               \"tokenizer\":\"standard\",\n" +
            "               \"filter\":[  \n" +
            "                  \"lowercase\",\n" +
            "                  \"my_synonym_filter\"\n" +
            "               ]\n" +
            "            }\n" +
            "         }\n" +
            "      }\n" +
            "   }";


        client.admin().indices().prepareCreate("job").setSettings(Settings.builder().loadFromSource(settings)).get();


        log.info("after creating and set {}",settings);


        client.admin().indices().preparePutMapping("job")
            .setType("job")
            .setSource("{\n" +
                " \"job\":{\n" +
                "  \"properties\":{\n" +
                "   \"description\": {\n" +
                "            \"type\": \"string\",\n" +
                "            \"analyzer\":\"my_synonyms\"\n" +
                "          }\n" +
                "  }\n" +
                " }\n" +
                "}").get();

        log.info("after updating mapping");


        log.info("after porting back the data");


    }

    @Async
    public void synchDBtoES(){
        log.info("synchDBtoES");
        jobRepository.findAll().forEach(jobSearchRepository::save);
    }


//TODO user input words, and build synonym dictionary...
    private String getSynonyms(){
        List<String> synonyms = new ArrayList<>();

        String angular = Arrays.asList("angular","angularjs").stream().collect(Collectors.joining(","));
        synonyms.add("\""+angular+"\"");

        String react = Arrays.asList("react","react.js","reactjs").stream().collect(Collectors.joining(","));
        synonyms.add("\""+react+"\"");

        String nodejs = Arrays.asList("nodejs","node.js").stream().collect(Collectors.joining(","));
        synonyms.add("\""+nodejs+"\"");

        String linux = Arrays.asList("linux","unix","ubuntu","centos").stream().collect(Collectors.joining(","));
        synonyms.add("\""+linux+"\"");

        String git = Arrays.asList("git","github").stream().collect(Collectors.joining(","));
        synonyms.add("\""+git+"\"");

        StringJoiner sj = new StringJoiner(",","[","]");

        synonyms.forEach(sj::add);
        return sj.toString();
    }


    // TODO: 16/11/16 user search job engine like pluralsight
    @Transactional(readOnly = true)
    public Page<Job> searchSuitableJob(List<String> techWords, String searchWord, String percentage) {
        //multi match....
//        {
//            "query":{
//            "bool":{
//                "must":{"match":{"searchWord":"Java"}},
//                "should":[
//                {"match":{"description":"Elasticsearch"}},
//                {"match":{"description":"Spring"}},
//                {"match":{"description":"Groovy"}},
//                {"match":{"description":"Angular"}},
//                {"match":{"description":"Jenkins"}},
//                {"match":{"description":"Git"}},
//                {"match":{"description":"DevOps"}},
//                {"match":{"description":"Linux"}},
//                {"match":{"description":"Docker"}}
//         ],
//                "minimum_should_match": "60%"
//            }
//        }
//        }

        if (techWords.isEmpty()) {
            log.info("searchSuitableJob by no tech words....");
        }

        //find the valid jobs... join job_log search
//        https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-joining-queries.html
        BoolQueryBuilder boolQueryBuilder = boolQuery().must(matchQuery("searchWord", searchWord));
        techWords.forEach(word -> boolQueryBuilder.should(matchQuery("description", word)));
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(boolQueryBuilder
                .minimumShouldMatch(percentage))
            .build();

//        log.info("searchJobAgg by ES query = {}", searchQuery.getQuery());

        Page<Job> jobPage = elasticsearchTemplate.queryForPage(searchQuery, Job.class);
        return jobPage;
    }

    @Transactional(readOnly = true)
    public Optional<StateDTO> searchJobAgg(String techWord, String searchWord, String groupByField) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(boolQuery()
                .must(matchQuery("description", techWord))
                .must(matchQuery("searchWord", searchWord))
            ).addAggregation(AggregationBuilders.terms("by_" + groupByField).field(groupByField))
            .build();

        log.info("searchJobAgg by ES query = {}", searchQuery.getQuery());
        Optional<StateDTO> result = elasticsearchTemplate.query(searchQuery, response -> {
            List<Long> jobIDList = new ArrayList<>();
            for (SearchHit searchHit : response.getHits()) {
                if (response.getHits().getHits().length <= 0) {
                    return Optional.ofNullable(null);
                }

                long id = Long.parseLong(searchHit.getId());
                jobIDList.add(id);
                String title = (String) searchHit.getSource().get("title");
                float score = searchHit.getScore();

                log.info("find {} in for {} id={} title={} score={} ", techWord, searchWord, id, title, score);

            }

            Terms agg = response.getAggregations().get("by_" + groupByField);
            Map<String, String> bucketMap = new HashMap<>();
            for (Terms.Bucket entry : agg.getBuckets()) {
                bucketMap.put(entry.getKey().toString(), String.valueOf(entry.getDocCount()));
            }

            StateDTO stateDTO = new StateDTO(techWord, jobIDList, response.getHits().totalHits(), bucketMap);
            return Optional.of(stateDTO);
        });
        return result;
    }
}
