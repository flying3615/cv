package com.gabriel.web.rest;

import com.gabriel.CvApp;

import com.gabriel.domain.JobCount;
import com.gabriel.repository.JobCountRepository;
import com.gabriel.repository.search.JobCountSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the JobCountResource REST controller.
 *
 * @see JobCountResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CvApp.class)
public class JobCountResourceIntTest {

    private static final LocalDate DEFAULT_LOG_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LOG_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_JOB_NUMBER = 1L;
    private static final Long UPDATED_JOB_NUMBER = 2L;

    @Inject
    private JobCountRepository jobCountRepository;

    @Inject
    private JobCountSearchRepository jobCountSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restJobCountMockMvc;

    private JobCount jobCount;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        JobCountResource jobCountResource = new JobCountResource();
        ReflectionTestUtils.setField(jobCountResource, "jobCountSearchRepository", jobCountSearchRepository);
        ReflectionTestUtils.setField(jobCountResource, "jobCountRepository", jobCountRepository);
        this.restJobCountMockMvc = MockMvcBuilders.standaloneSetup(jobCountResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JobCount createEntity(EntityManager em) {
        JobCount jobCount = new JobCount()
                .logDate(DEFAULT_LOG_DATE)
                .jobNumber(DEFAULT_JOB_NUMBER);
        return jobCount;
    }

    @Before
    public void initTest() {
        jobCountSearchRepository.deleteAll();
        jobCount = createEntity(em);
    }

    @Test
    @Transactional
    public void createJobCount() throws Exception {
        int databaseSizeBeforeCreate = jobCountRepository.findAll().size();

        // Create the JobCount

        restJobCountMockMvc.perform(post("/api/job-counts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(jobCount)))
                .andExpect(status().isCreated());

        // Validate the JobCount in the database
        List<JobCount> jobCounts = jobCountRepository.findAll();
        assertThat(jobCounts).hasSize(databaseSizeBeforeCreate + 1);
        JobCount testJobCount = jobCounts.get(jobCounts.size() - 1);
        assertThat(testJobCount.getLogDate()).isEqualTo(DEFAULT_LOG_DATE);
        assertThat(testJobCount.getJobNumber()).isEqualTo(DEFAULT_JOB_NUMBER);

        // Validate the JobCount in ElasticSearch
        JobCount jobCountEs = jobCountSearchRepository.findOne(testJobCount.getId());
        assertThat(jobCountEs).isEqualToComparingFieldByField(testJobCount);
    }

    @Test
    @Transactional
    public void getAllJobCounts() throws Exception {
        // Initialize the database
        jobCountRepository.saveAndFlush(jobCount);

        // Get all the jobCounts
        restJobCountMockMvc.perform(get("/api/job-counts?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(jobCount.getId().intValue())))
                .andExpect(jsonPath("$.[*].logDate").value(hasItem(DEFAULT_LOG_DATE.toString())))
                .andExpect(jsonPath("$.[*].jobNumber").value(hasItem(DEFAULT_JOB_NUMBER.intValue())));
    }

    @Test
    @Transactional
    public void getJobCount() throws Exception {
        // Initialize the database
        jobCountRepository.saveAndFlush(jobCount);

        // Get the jobCount
        restJobCountMockMvc.perform(get("/api/job-counts/{id}", jobCount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jobCount.getId().intValue()))
            .andExpect(jsonPath("$.logDate").value(DEFAULT_LOG_DATE.toString()))
            .andExpect(jsonPath("$.jobNumber").value(DEFAULT_JOB_NUMBER.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingJobCount() throws Exception {
        // Get the jobCount
        restJobCountMockMvc.perform(get("/api/job-counts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJobCount() throws Exception {
        // Initialize the database
        jobCountRepository.saveAndFlush(jobCount);
        jobCountSearchRepository.save(jobCount);
        int databaseSizeBeforeUpdate = jobCountRepository.findAll().size();

        // Update the jobCount
        JobCount updatedJobCount = jobCountRepository.findOne(jobCount.getId());
        updatedJobCount
                .logDate(UPDATED_LOG_DATE)
                .jobNumber(UPDATED_JOB_NUMBER);

        restJobCountMockMvc.perform(put("/api/job-counts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedJobCount)))
                .andExpect(status().isOk());

        // Validate the JobCount in the database
        List<JobCount> jobCounts = jobCountRepository.findAll();
        assertThat(jobCounts).hasSize(databaseSizeBeforeUpdate);
        JobCount testJobCount = jobCounts.get(jobCounts.size() - 1);
        assertThat(testJobCount.getLogDate()).isEqualTo(UPDATED_LOG_DATE);
        assertThat(testJobCount.getJobNumber()).isEqualTo(UPDATED_JOB_NUMBER);

        // Validate the JobCount in ElasticSearch
        JobCount jobCountEs = jobCountSearchRepository.findOne(testJobCount.getId());
        assertThat(jobCountEs).isEqualToComparingFieldByField(testJobCount);
    }

    @Test
    @Transactional
    public void deleteJobCount() throws Exception {
        // Initialize the database
        jobCountRepository.saveAndFlush(jobCount);
        jobCountSearchRepository.save(jobCount);
        int databaseSizeBeforeDelete = jobCountRepository.findAll().size();

        // Get the jobCount
        restJobCountMockMvc.perform(delete("/api/job-counts/{id}", jobCount.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean jobCountExistsInEs = jobCountSearchRepository.exists(jobCount.getId());
        assertThat(jobCountExistsInEs).isFalse();

        // Validate the database is empty
        List<JobCount> jobCounts = jobCountRepository.findAll();
        assertThat(jobCounts).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchJobCount() throws Exception {
        // Initialize the database
        jobCountRepository.saveAndFlush(jobCount);
        jobCountSearchRepository.save(jobCount);

        // Search the jobCount
        restJobCountMockMvc.perform(get("/api/_search/job-counts?query=id:" + jobCount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobCount.getId().intValue())))
            .andExpect(jsonPath("$.[*].logDate").value(hasItem(DEFAULT_LOG_DATE.toString())))
            .andExpect(jsonPath("$.[*].jobNumber").value(hasItem(DEFAULT_JOB_NUMBER.intValue())));
    }
}
