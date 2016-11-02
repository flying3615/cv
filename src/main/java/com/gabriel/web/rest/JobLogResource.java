package com.gabriel.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.gabriel.domain.JobLog;

import com.gabriel.repository.JobLogRepository;
import com.gabriel.repository.search.JobLogSearchRepository;
import com.gabriel.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing JobLog.
 */
@RestController
@RequestMapping("/api")
public class JobLogResource {

    private final Logger log = LoggerFactory.getLogger(JobLogResource.class);
        
    @Inject
    private JobLogRepository jobLogRepository;

    @Inject
    private JobLogSearchRepository jobLogSearchRepository;

    /**
     * POST  /job-logs : Create a new jobLog.
     *
     * @param jobLog the jobLog to create
     * @return the ResponseEntity with status 201 (Created) and with body the new jobLog, or with status 400 (Bad Request) if the jobLog has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/job-logs")
    @Timed
    public ResponseEntity<JobLog> createJobLog(@RequestBody JobLog jobLog) throws URISyntaxException {
        log.debug("REST request to save JobLog : {}", jobLog);
        if (jobLog.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("jobLog", "idexists", "A new jobLog cannot already have an ID")).body(null);
        }
        JobLog result = jobLogRepository.save(jobLog);
        jobLogSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/job-logs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("jobLog", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /job-logs : Updates an existing jobLog.
     *
     * @param jobLog the jobLog to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated jobLog,
     * or with status 400 (Bad Request) if the jobLog is not valid,
     * or with status 500 (Internal Server Error) if the jobLog couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/job-logs")
    @Timed
    public ResponseEntity<JobLog> updateJobLog(@RequestBody JobLog jobLog) throws URISyntaxException {
        log.debug("REST request to update JobLog : {}", jobLog);
        if (jobLog.getId() == null) {
            return createJobLog(jobLog);
        }
        JobLog result = jobLogRepository.save(jobLog);
        jobLogSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("jobLog", jobLog.getId().toString()))
            .body(result);
    }

    /**
     * GET  /job-logs : get all the jobLogs.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of jobLogs in body
     */
    @GetMapping("/job-logs")
    @Timed
    public List<JobLog> getAllJobLogs() {
        log.debug("REST request to get all JobLogs");
        List<JobLog> jobLogs = jobLogRepository.findAll();
        return jobLogs;
    }

    /**
     * GET  /job-logs/:id : get the "id" jobLog.
     *
     * @param id the id of the jobLog to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the jobLog, or with status 404 (Not Found)
     */
    @GetMapping("/job-logs/{id}")
    @Timed
    public ResponseEntity<JobLog> getJobLog(@PathVariable Long id) {
        log.debug("REST request to get JobLog : {}", id);
        JobLog jobLog = jobLogRepository.findOne(id);
        return Optional.ofNullable(jobLog)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /job-logs/:id : delete the "id" jobLog.
     *
     * @param id the id of the jobLog to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/job-logs/{id}")
    @Timed
    public ResponseEntity<Void> deleteJobLog(@PathVariable Long id) {
        log.debug("REST request to delete JobLog : {}", id);
        jobLogRepository.delete(id);
        jobLogSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("jobLog", id.toString())).build();
    }

    /**
     * SEARCH  /_search/job-logs?query=:query : search for the jobLog corresponding
     * to the query.
     *
     * @param query the query of the jobLog search 
     * @return the result of the search
     */
    @GetMapping("/_search/job-logs")
    @Timed
    public List<JobLog> searchJobLogs(@RequestParam String query) {
        log.debug("REST request to search JobLogs for query {}", query);
        return StreamSupport
            .stream(jobLogSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
