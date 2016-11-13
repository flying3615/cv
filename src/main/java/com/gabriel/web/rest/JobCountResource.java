package com.gabriel.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.gabriel.domain.JobCount;

import com.gabriel.repository.JobCountRepository;
import com.gabriel.repository.search.JobCountSearchRepository;
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
 * REST controller for managing JobCount.
 */
@RestController
@RequestMapping("/api")
public class JobCountResource {

    private final Logger log = LoggerFactory.getLogger(JobCountResource.class);
        
    @Inject
    private JobCountRepository jobCountRepository;

    @Inject
    private JobCountSearchRepository jobCountSearchRepository;

    /**
     * POST  /job-counts : Create a new jobCount.
     *
     * @param jobCount the jobCount to create
     * @return the ResponseEntity with status 201 (Created) and with body the new jobCount, or with status 400 (Bad Request) if the jobCount has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/job-counts")
    @Timed
    public ResponseEntity<JobCount> createJobCount(@RequestBody JobCount jobCount) throws URISyntaxException {
        log.debug("REST request to save JobCount : {}", jobCount);
        if (jobCount.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("jobCount", "idexists", "A new jobCount cannot already have an ID")).body(null);
        }
        JobCount result = jobCountRepository.save(jobCount);
        jobCountSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/job-counts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("jobCount", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /job-counts : Updates an existing jobCount.
     *
     * @param jobCount the jobCount to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated jobCount,
     * or with status 400 (Bad Request) if the jobCount is not valid,
     * or with status 500 (Internal Server Error) if the jobCount couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/job-counts")
    @Timed
    public ResponseEntity<JobCount> updateJobCount(@RequestBody JobCount jobCount) throws URISyntaxException {
        log.debug("REST request to update JobCount : {}", jobCount);
        if (jobCount.getId() == null) {
            return createJobCount(jobCount);
        }
        JobCount result = jobCountRepository.save(jobCount);
        jobCountSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("jobCount", jobCount.getId().toString()))
            .body(result);
    }

    /**
     * GET  /job-counts : get all the jobCounts.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of jobCounts in body
     */
    @GetMapping("/job-counts")
    @Timed
    public List<JobCount> getAllJobCounts() {
        log.debug("REST request to get all JobCounts");
        List<JobCount> jobCounts = jobCountRepository.findAll();
        return jobCounts;
    }

    /**
     * GET  /job-counts/:id : get the "id" jobCount.
     *
     * @param id the id of the jobCount to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the jobCount, or with status 404 (Not Found)
     */
    @GetMapping("/job-counts/{id}")
    @Timed
    public ResponseEntity<JobCount> getJobCount(@PathVariable Long id) {
        log.debug("REST request to get JobCount : {}", id);
        JobCount jobCount = jobCountRepository.findOne(id);
        return Optional.ofNullable(jobCount)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /job-counts/:id : delete the "id" jobCount.
     *
     * @param id the id of the jobCount to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/job-counts/{id}")
    @Timed
    public ResponseEntity<Void> deleteJobCount(@PathVariable Long id) {
        log.debug("REST request to delete JobCount : {}", id);
        jobCountRepository.delete(id);
        jobCountSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("jobCount", id.toString())).build();
    }

    /**
     * SEARCH  /_search/job-counts?query=:query : search for the jobCount corresponding
     * to the query.
     *
     * @param query the query of the jobCount search 
     * @return the result of the search
     */
    @GetMapping("/_search/job-counts")
    @Timed
    public List<JobCount> searchJobCounts(@RequestParam String query) {
        log.debug("REST request to search JobCounts for query {}", query);
        return StreamSupport
            .stream(jobCountSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
