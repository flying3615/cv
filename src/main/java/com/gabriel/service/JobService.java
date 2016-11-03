package com.gabriel.service;

import com.gabriel.domain.Job;
import com.gabriel.domain.JobLog;
import com.gabriel.domain.enumeration.JobLogType;
import com.gabriel.repository.JobLogRepository;
import com.gabriel.repository.JobRepository;
import com.gabriel.repository.search.JobLogSearchRepository;
import com.gabriel.repository.search.JobSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    public Long countByWord(String word) {
        return jobRepository.countBySearchWord(word);
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
        Page<Job> result = jobSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }

    @Transactional(readOnly = true)
    public Set<Job> findBySearchWordAndFromSite(String keyword, String from_site) {
        log.debug("Request to find jobs by search word {} and from site {}", keyword, from_site);
        return jobRepository.findBySearchWordAndFromSite(keyword, from_site);
    }


    public void saveVanishedJob(Job job) {
        log.debug("Request to save VanishedJob job : {}", job);
        jobLogSearchRepository.save(new JobLog(JobLogType.REMOVE, LocalDate.now(), job));
    }


    public void findJobsNotBelongs2HR(){

    }
}
