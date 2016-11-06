package com.gabriel.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.gabriel.domain.Job;
import com.gabriel.service.JobService;
import com.gabriel.service.task.ScheduledCrawlTask;
import com.gabriel.web.rest.DTO.GoogleLocation;
import com.gabriel.web.rest.DTO.JobCountDTO;
import com.gabriel.web.rest.DTO.JobTrendDTO;
import com.gabriel.web.rest.util.HeaderUtil;
import com.gabriel.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * REST controller for managing Job.
 */
@RestController
@RequestMapping("/api")
public class JobResource {

    private final Logger log = LoggerFactory.getLogger(JobResource.class);

    @Inject
    private JobService jobService;

    /**
     * POST  /jobs : Create a new job.
     *
     * @param job the job to create
     * @return the ResponseEntity with status 201 (Created) and with body the new job, or with status 400 (Bad Request) if the job has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/jobs")
    @Timed
    public ResponseEntity<Job> createJob(@Valid @RequestBody Job job) throws URISyntaxException {
        log.debug("REST request to save Job : {}", job);
        if (job.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("job", "idexists", "A new job cannot already have an ID")).body(null);
        }
        Job result = jobService.save(job);
        return ResponseEntity.created(new URI("/api/jobs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("job", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /jobs : Updates an existing job.
     *
     * @param job the job to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated job,
     * or with status 400 (Bad Request) if the job is not valid,
     * or with status 500 (Internal Server Error) if the job couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/jobs")
    @Timed
    public ResponseEntity<Job> updateJob(@Valid @RequestBody Job job) throws URISyntaxException {
        log.debug("REST request to update Job : {}", job);
        if (job.getId() == null) {
            return createJob(job);
        }
        Job result = jobService.save(job);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("job", job.getId().toString()))
            .body(result);
    }

    /**
     * GET  /jobs : get all the jobs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of jobs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/jobs")
    @Timed
    public ResponseEntity<List<Job>> getAllJobs(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Jobs");
        Page<Job> page = jobService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/jobs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


    @GetMapping("/jobs_count_word")
    @Timed
    public ResponseEntity<List<JobCountDTO>> getJobsByWord(Pageable pageable)
        throws URISyntaxException {

        List<JobCountDTO> jobCountList = new ArrayList<>();
        //TODO by date, need to join log and job table
        //find jobs which status is not remove in joblog table
        jobCountList.add(new JobCountDTO("Java", jobService.countByWordCurrent("Java", pageable)));
        jobCountList.add(new JobCountDTO(".Net", jobService.countByWordCurrent(".Net", pageable)));
        jobCountList.add(new JobCountDTO("Python", jobService.countByWordCurrent("Python", pageable)));
        jobCountList.add(new JobCountDTO("Ruby", jobService.countByWordCurrent("Ruby", pageable)));
        jobCountList.add(new JobCountDTO("JavaScript", jobService.countByWordCurrent("JavaScript", pageable)));
        jobCountList.add(new JobCountDTO("PHP", jobService.countByWordCurrent("PHP", pageable)));

        return new ResponseEntity<>(jobCountList, HttpStatus.OK);
    }

    @GetMapping("/jobs_map/{searchword}")
    @Timed
    public ResponseEntity<List<GoogleLocation>> getJobsMapByWord(@PathVariable String searchword)
        throws URISyntaxException {
        log.debug("REST request to getJobsMapByWord searchword : {}", searchword);

        //fucking stupid solution!!!!
        //springMVC will drop params with special char
        if ("Net".equals(searchword)) {
            searchword = ".Net";
        }

        List<GoogleLocation> jobCountList = jobService.getMapDataByWord(searchword);

        return new ResponseEntity<>(jobCountList, HttpStatus.OK);
    }


    @GetMapping("/jobs_trend")
    @Timed
    public ResponseEntity<Map<String, JobTrendDTO>> getJobsTrendByWord(Pageable pageable)
        throws URISyntaxException {

        Map<String, JobTrendDTO> jobTrendList = new HashMap<>();
//        //TODO get jobs by all date, need to join log and job table

        jobTrendList.put("Java", jobService.getJobTrendByWord("Java", pageable));
//        jobCountList.add(new JobCountDTO(".Net",jobService.countByWordCurrent(".Net",pageable)));
//        jobCountList.add(new JobCountDTO("Python",jobService.countByWordCurrent("Python",pageable)));
//        jobCountList.add(new JobCountDTO("Ruby",jobService.countByWordCurrent("Ruby",pageable)));
//        jobCountList.add(new JobCountDTO("JavaScript",jobService.countByWordCurrent("JavaScript",pageable)));

        return new ResponseEntity<>(jobTrendList, HttpStatus.OK);
    }

    /**
     * GET  /jobs/:id : get the "id" job.
     *
     * @param id the id of the job to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the job, or with status 404 (Not Found)
     */
    @GetMapping("/jobs/{id}")
    @Timed
    public ResponseEntity<Job> getJob(@PathVariable Long id) {
        log.debug("REST request to get Job : {}", id);
        Job job = jobService.findOne(id);
        return Optional.ofNullable(job)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /jobs/:id : delete the "id" job.
     *
     * @param id the id of the job to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/jobs/{id}")
    @Timed
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        log.debug("REST request to delete Job : {}", id);
        jobService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("job", id.toString())).build();
    }

    /**
     * SEARCH  /_search/jobs?query=:query : search for the job corresponding
     * to the query.
     *
     * @param query    the query of the job search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/jobs")
    @Timed
    public ResponseEntity<List<Job>> searchJobs(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Jobs for query {}", query);
        Page<Job> page = jobService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/jobs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


    @Inject
    ScheduledCrawlTask scheduledCrawlTask;

    @GetMapping("/schedule/jobs")
    @Timed
    public ResponseEntity<Void> schedule()
        throws URISyntaxException {
        List<String> keywords = new ArrayList<>();

        keywords.add("Java");
        keywords.add(".Net");
        keywords.add("Python");
        keywords.add("Ruby");
        keywords.add("JavaScript");
        keywords.add("PHP");

        keywords.forEach(scheduledCrawlTask::crawlByWord);

        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("job", "")).build();
    }

}
