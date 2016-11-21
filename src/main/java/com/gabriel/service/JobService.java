package com.gabriel.service;

import com.gabriel.domain.Job;
import com.gabriel.domain.JobCount;
import com.gabriel.domain.JobLog;
import com.gabriel.domain.SearchWord;
import com.gabriel.domain.enumeration.JobLogType;
import com.gabriel.domain.specification.JobSpecification;
import com.gabriel.repository.JobCountRepository;
import com.gabriel.repository.JobLogRepository;
import com.gabriel.repository.JobRepository;
import com.gabriel.repository.SearchWordRepository;
import com.gabriel.repository.search.JobLogSearchRepository;
import com.gabriel.repository.search.JobSearchRepository;
import com.gabriel.web.rest.DTO.GoogleLocation;
import com.gabriel.web.rest.DTO.JobTrendDTO;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.gabriel.service.util.StringUtil.splitQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

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


    /**
     * Search for the job corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Job> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Jobs for query {}", query);
        QueryStringQueryBuilder qsq = queryStringQuery(query);
        log.info(qsq.toString());
        Page<Job> result = jobSearchRepository.search(qsq, pageable);

        return result;
    }


    @Transactional(readOnly = true)
    public void bubbleData() {

        List<String> possibility = Arrays.asList("20%", "40%", "60%", "80%", "100%");
        List<String> searchWord = Arrays.asList("Java", ".Net", "JavaScript", "Ruby", "PHP", "Python");

//        for (String word : searchWord) {
//            for (String p : possibility) {
//                Page<Job> jobPage = this.s(Arrays.asList("Spring", "GitHub", "Angular", "Jenkins", "Docker"), "Java", p);
//                log.info("{} {} = {}", word, p, jobPage.getTotalElements());
//            }
//        }

    }


    @Transactional(readOnly = true)
    public Set<Job> findBySearchWordAndFromSite(String keyword, String from_site) {
        log.debug("Request to find jobs by search word {} and from site {}", keyword, from_site);


        return jobRepository.findBySearchWordAndFromSite(keyword, from_site);
    }


    public void saveVanishedJob(Job job) {
        log.debug("Request to save VanishedJob job externalID: {}", job.getExternalID());
        job.setIsremoved(true);
        jobSearchRepository.save(job);
        jobRepository.save(job);
    }


    public void findExprenceJobs(){
        // TODO: 16/11/16
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

    @Async
    public void updateDuplicateJobsBySettingKeywords() {

        Map<String, String> dupJobMap = new HashMap<>();
        //find duplicate ID
        Object[] id_word = jobRepository.getDuplicateJobs();
        for (Object o : id_word) {
            Object[] item = (Object[]) o;
            String external_id = (String) item[0];
            String search_word = (String) item[1];
            dupJobMap.compute(external_id, (key, oldvalue) -> {
                if (oldvalue == null) return search_word;
                else return oldvalue + "," + search_word;
            });
        }


        //update its keywords to all search_word,include SQL&ES
        dupJobMap.keySet().forEach(externalID ->
            jobRepository.findByExternalID(externalID).forEach(job -> {
                job.setKeywords(dupJobMap.get(externalID));
                jobRepository.save(job);
                jobSearchRepository.save(job);
            }));


        //update single keywords when seach_word finding single job
        List<Job> singleWordJobs = jobRepository.findByKeywordsIsNull();
        singleWordJobs.forEach(job -> {
            job.setKeywords(job.getSearchWord());
            jobRepository.save(job);
            jobSearchRepository.save(job);
        });


    }


    public void recordTodayJobNumber(String searchKeyword) {
        JobCount jobCount = new JobCount();
        jobCount.setJobNumber(countByWordCurrent(searchKeyword));
        jobCount.setLogDate(LocalDate.now());
        Optional<SearchWord> searchWord = searchWordRepository.findByWordName(searchKeyword);
        jobCount.setSearchWord(searchWord.orElseThrow(IllegalArgumentException::new));
        log.info("write into job count table for {} {}",jobCount.getSearchWord(),jobCount.getJobNumber());
        jobCountRepository.save(jobCount);
    }

    public Page<Job> findAllByQuery(String query, Pageable pageable) {
        Map<String,String> queryMap = splitQuery(query);
        log.info("getAllJobs query={}",splitQuery(query));

        String searchWord;
        String location;

        Job jobCriteria = new Job();

        if((searchWord=queryMap.get("searchWord"))!=null&&!searchWord.equals("All")){
            jobCriteria.setSearchWord(searchWord);
        }

        if((location=queryMap.get("location"))!=null){
            jobCriteria.setLocation(location);
        }

        //TODO more!!!

        return jobRepository.findAll(Specifications
            .where(JobSpecification.findByCriteria(jobCriteria)),pageable);

    }




    //just for dev




}
