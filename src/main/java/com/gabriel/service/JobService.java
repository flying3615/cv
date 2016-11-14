package com.gabriel.service;

import com.gabriel.domain.Job;
import com.gabriel.domain.JobCount;
import com.gabriel.domain.JobLog;
import com.gabriel.domain.SearchWord;
import com.gabriel.domain.enumeration.JobLogType;
import com.gabriel.repository.JobCountRepository;
import com.gabriel.repository.JobLogRepository;
import com.gabriel.repository.JobRepository;
import com.gabriel.repository.SearchWordRepository;
import com.gabriel.repository.search.JobLogSearchRepository;
import com.gabriel.repository.search.JobSearchRepository;
import com.gabriel.web.rest.DTO.GoogleLocation;
import com.gabriel.web.rest.DTO.JobTrendDTO;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Job.
 */
@Service
@Transactional
public class JobService {

    private final Logger log = LoggerFactory.getLogger(JobService.class);

    @Inject
    private JobRepository jobRepository;

    @Inject
    private JobSearchRepository jobSearchRepository;

    @Inject
    private JobLogSearchRepository jobLogSearchRepository;

    @Inject
    private JobLogRepository jobLogRepository;

    @Inject
    private JobCountRepository jobCountRepository;

    @Inject
    private SearchWordRepository searchWordRepository;


    /**
     * Save a job.
     *
     * @param job the entity to save
     * @return the persisted entity
     */
    public Job save(Job job) {
        log.debug("Request to save Job : {}", job);
        Job result = jobRepository.save(job);
        jobSearchRepository.save(result);
        return result;
    }


    public JobLog saveJobLog(Job job) {
        log.debug("Request to save JobLog : {}", job);
        JobLog jobLog = jobLogRepository.save(new JobLog(JobLogType.ADD, LocalDate.now(), job));
        log.debug("Request to save JobLog to elasticsearch: {}", jobLog);
        jobLogSearchRepository.save(jobLog);
        return jobLog;

    }

    /**
     * Get all the jobs.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Job> findAll(Pageable pageable) {
        log.debug("Request to get all Jobs");
        Page<Job> result = jobRepository.findAll(pageable);
        return result;
    }


    @Transactional(readOnly = true)
    public Long countByWordCurrent(String word) {
        List<Job> jobs = jobRepository.countBySearchWord(word);
        return (long) jobs.size();
    }

    /**
     * Get one job by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Job findOne(Long id) {
        log.debug("Request to get Job : {}", id);
        Job job = jobRepository.findOne(id);
        return job;
    }

    /**
     * Delete the  job by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Job : {}", id);
        jobRepository.delete(id);
        jobSearchRepository.delete(id);
    }

    @Inject
    ElasticsearchTemplate elasticsearchTemplate;

    /**
     * Search for the job corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Job> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Jobs for query {}", query);
        Page<Job> result = jobSearchRepository.search(queryStringQuery(query), pageable);

        return result;
    }

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
    public void bubbleData() {

        List<String> possibility = Arrays.asList("20%", "40%", "60%", "80%", "100%");
        List<String> searchWord = Arrays.asList("Java", ".Net", "JavaScript", "Ruby", "PHP", "Python");

        for (String word : searchWord) {
            for (String p : possibility) {
                Page<Job> jobPage = this.searchSuitableJob(Arrays.asList("Spring", "GitHub", "Angular", "Jenkins", "Docker"), "Java", p);
                log.info("{} {} = {}", word, p, jobPage.getTotalElements());
            }
        }

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


    @Transactional(readOnly = true)
    public Set<Job> findBySearchWordAndFromSite(String keyword, String from_site) {
        log.debug("Request to find jobs by search word {} and from site {}", keyword, from_site);
        return jobRepository.findBySearchWordAndFromSite(keyword, from_site);
    }


    public void saveVanishedJob(Job job) {
        log.debug("Request to save VanishedJob job externalID: {}", job.getExternalID());
//        jobLogSearchRepository.save(new JobLog(JobLogType.REMOVE, LocalDate.now(), job));
        jobLogRepository.save(new JobLog(JobLogType.REMOVE, LocalDate.now(), job));
    }


    public void findJobsNotBelongs2HR() {
//        TODO
    }

    public JobTrendDTO getJobTrendByWord(String keyword) {

        log.debug("Request to get {} job trend", keyword);
        Optional<SearchWord> searchWord = searchWordRepository.findByWordName(keyword);
        List<JobCount> result = jobCountRepository.findBySearchWord(searchWord.orElseThrow(IllegalArgumentException::new));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM");
        List<String> dates = new ArrayList<>();
        List<Long> jobNum = new ArrayList<>();

        for (JobCount o : result) {
            dates.add(o.getLogDate().format(dateTimeFormatter));
            jobNum.add(o.getJobNumber());
        }
        JobTrendDTO jobTrendDTO = new JobTrendDTO(keyword, dates, jobNum);
        return jobTrendDTO;
    }


    public List<GoogleLocation> getMapDataByWord(String keyword) {
        List<GoogleLocation> convertedResult = new ArrayList<>();
        Object[] result;
        if ("All".equals(keyword)) {
            result = jobRepository.getMapDataAll();

        } else {
            result = jobRepository.getMapDataByWord(keyword);
        }

        for (Object o : result) {
            Object[] item = (Object[]) o;
            String location = (String) item[0];
            BigInteger count = (BigInteger) item[1];
            convertedResult.add(new GoogleLocation(location, keyword, count.longValue()));

        }
        return convertedResult;

    }


    public void recordTodayJobNumber(String searchKeyword) {
        JobCount jobCount = new JobCount();
        jobCount.setJobNumber(countByWordCurrent(searchKeyword));
        jobCount.setLogDate(LocalDate.now());
        Optional<SearchWord> searchWord = searchWordRepository.findByWordName(searchKeyword);
        jobCount.setSearchWord(searchWord.orElseThrow(IllegalArgumentException::new));
        jobCountRepository.save(jobCount);
    }

    //just for dev
    public void synchData() {
        jobRepository.findAll().forEach(jobSearchRepository::save);
    }


    public void updateSynonyms(){
        Client client =  elasticsearchTemplate.getClient();

        //delete all
        client.admin().indices().delete(new DeleteIndexRequest("job")).actionGet();

        log.info("after deleting all");

        //update settings
        client.admin().indices().prepareCreate("job").setSettings(Settings.builder().loadFromSource(
            "{  \n" +
            "      \"analysis\":{  \n" +
            "         \"filter\":{  \n" +
            "            \"my_synonym_filter\":{  \n" +
            "               \"type\":\"synonym\",\n" +
            "               \"synonyms\":[  \n" +
            "                  \"angular,angularjs\",\n" +
            "                  \"react,reactjs\",\n" +
            "                  \"whh,nodejs,node.js\"\n" +
            "               ]\n" +
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
            "   }")).get();


        log.info("after creating and set");


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


        this.synchData();
        log.info("after porting back the data");


    }


    public static void main(String[] args) {

    }




}
