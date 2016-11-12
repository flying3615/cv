package com.gabriel.service;

import com.gabriel.domain.Job;
import com.gabriel.domain.JobLog;
import com.gabriel.domain.enumeration.JobLogType;
import com.gabriel.repository.JobLogRepository;
import com.gabriel.repository.JobRepository;
import com.gabriel.repository.search.JobLogSearchRepository;
import com.gabriel.repository.search.JobSearchRepository;
import com.gabriel.web.rest.DTO.GoogleLocation;
import com.gabriel.web.rest.DTO.JobTrendDTO;
import com.gabriel.web.rest.DTO.StateDTO;
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
import java.util.*;

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
    public Long countByWordCurrent(String word, Pageable pageable) {
        Page<Job> jobs = jobRepository.countBySearchWord(word, pageable);
        return jobs.getTotalElements();
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
    public void searchSuitableJob(){
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
    }


    @Transactional(readOnly = true)
    public Optional<StateDTO> searchJobAgg(String techWord, String searchWord, String groupByField) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(boolQuery()
                .must(matchQuery("description",techWord))
                .must(matchQuery("searchWord",searchWord))
            ).addAggregation(AggregationBuilders.terms("by_"+groupByField).field(groupByField))
            .build();

        log.info("searchJobAgg by ES query = {}",searchQuery.getQuery());
        Optional<StateDTO> result = elasticsearchTemplate.query(searchQuery, response -> {
            List<Long> jobIDList = new ArrayList<>();
            for(SearchHit searchHit : response.getHits()){
                if(response.getHits().getHits().length <= 0) {
                    return Optional.ofNullable(null);
                }

                long id = Long.parseLong(searchHit.getId());
                jobIDList.add(id);
                String title = (String) searchHit.getSource().get("title");
                float score = searchHit.getScore();

                log.info("find {} in for {} id={} title={} score={} ",techWord,searchWord, id,title,score);

            }

            Terms agg = response.getAggregations().get("by_"+groupByField);
            Map<String,String> bucketMap = new HashMap<>();
            for(Terms.Bucket entry:agg.getBuckets()){
                bucketMap.put(entry.getKey().toString(),String.valueOf(entry.getDocCount()));
            }

            StateDTO stateDTO = new StateDTO(techWord,jobIDList,response.getHits().totalHits(),bucketMap);
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

        Object[] result = jobRepository.getJobTrend(keyword);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

        List<String> dates = new ArrayList<>();
        List<Long> jobNum = new ArrayList<>();

        Map<String, Long> trendMap = new HashMap<>();
        for (Object o : result) {
            Object[] item = (Object[]) o;
            Date date = (Date) item[0];
            BigInteger count = (BigInteger) item[2];
            dates.add(sdf.format(date));
            jobNum.add(count.longValue());
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


}
