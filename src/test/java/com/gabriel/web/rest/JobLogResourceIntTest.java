package com.gabriel.web.rest;

import com.gabriel.CvApp;

import com.gabriel.domain.JobLog;
import com.gabriel.repository.JobLogRepository;
import com.gabriel.repository.search.JobLogSearchRepository;

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

import com.gabriel.domain.enumeration.JobLogType;
/**
 * Test class for the JobLogResource REST controller.
 *
 * @see JobLogResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CvApp.class)
public class JobLogResourceIntTest {

    private static final JobLogType DEFAULT_TYPE = JobLogType.ADD;
    private static final JobLogType UPDATED_TYPE = JobLogType.REMOVE;

    private static final LocalDate DEFAULT_LOG_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LOG_DATE = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private JobLogRepository jobLogRepository;

    @Inject
    private JobLogSearchRepository jobLogSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restJobLogMockMvc;

    private JobLog jobLog;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        JobLogResource jobLogResource = new JobLogResource();
        ReflectionTestUtils.setField(jobLogResource, "jobLogSearchRepository", jobLogSearchRepository);
        ReflectionTestUtils.setField(jobLogResource, "jobLogRepository", jobLogRepository);
        this.restJobLogMockMvc = MockMvcBuilders.standaloneSetup(jobLogResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JobLog createEntity(EntityManager em) {
        JobLog jobLog = new JobLog()
                .type(DEFAULT_TYPE)
                .logDate(DEFAULT_LOG_DATE);
        return jobLog;
    }

    @Before
    public void initTest() {
        jobLogSearchRepository.deleteAll();
        jobLog = createEntity(em);
    }

    @Test
    @Transactional
    public void createJobLog() throws Exception {
        int databaseSizeBeforeCreate = jobLogRepository.findAll().size();

        // Create the JobLog

        restJobLogMockMvc.perform(post("/api/job-logs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(jobLog)))
                .andExpect(status().isCreated());

        // Validate the JobLog in the database
        List<JobLog> jobLogs = jobLogRepository.findAll();
        assertThat(jobLogs).hasSize(databaseSizeBeforeCreate + 1);
        JobLog testJobLog = jobLogs.get(jobLogs.size() - 1);
        assertThat(testJobLog.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testJobLog.getLogDate()).isEqualTo(DEFAULT_LOG_DATE);

        // Validate the JobLog in ElasticSearch
        JobLog jobLogEs = jobLogSearchRepository.findOne(testJobLog.getId());
        assertThat(jobLogEs).isEqualToComparingFieldByField(testJobLog);
    }

    @Test
    @Transactional
    public void getAllJobLogs() throws Exception {
        // Initialize the database
        jobLogRepository.saveAndFlush(jobLog);

        // Get all the jobLogs
        restJobLogMockMvc.perform(get("/api/job-logs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(jobLog.getId().intValue())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].logDate").value(hasItem(DEFAULT_LOG_DATE.toString())));
    }

    @Test
    @Transactional
    public void getJobLog() throws Exception {
        // Initialize the database
        jobLogRepository.saveAndFlush(jobLog);

        // Get the jobLog
        restJobLogMockMvc.perform(get("/api/job-logs/{id}", jobLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jobLog.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.logDate").value(DEFAULT_LOG_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingJobLog() throws Exception {
        // Get the jobLog
        restJobLogMockMvc.perform(get("/api/job-logs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJobLog() throws Exception {
        // Initialize the database
        jobLogRepository.saveAndFlush(jobLog);
        jobLogSearchRepository.save(jobLog);
        int databaseSizeBeforeUpdate = jobLogRepository.findAll().size();

        // Update the jobLog
        JobLog updatedJobLog = jobLogRepository.findOne(jobLog.getId());
        updatedJobLog
                .type(UPDATED_TYPE)
                .logDate(UPDATED_LOG_DATE);

        restJobLogMockMvc.perform(put("/api/job-logs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedJobLog)))
                .andExpect(status().isOk());

        // Validate the JobLog in the database
        List<JobLog> jobLogs = jobLogRepository.findAll();
        assertThat(jobLogs).hasSize(databaseSizeBeforeUpdate);
        JobLog testJobLog = jobLogs.get(jobLogs.size() - 1);
        assertThat(testJobLog.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testJobLog.getLogDate()).isEqualTo(UPDATED_LOG_DATE);

        // Validate the JobLog in ElasticSearch
        JobLog jobLogEs = jobLogSearchRepository.findOne(testJobLog.getId());
        assertThat(jobLogEs).isEqualToComparingFieldByField(testJobLog);
    }

    @Test
    @Transactional
    public void deleteJobLog() throws Exception {
        // Initialize the database
        jobLogRepository.saveAndFlush(jobLog);
        jobLogSearchRepository.save(jobLog);
        int databaseSizeBeforeDelete = jobLogRepository.findAll().size();

        // Get the jobLog
        restJobLogMockMvc.perform(delete("/api/job-logs/{id}", jobLog.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean jobLogExistsInEs = jobLogSearchRepository.exists(jobLog.getId());
        assertThat(jobLogExistsInEs).isFalse();

        // Validate the database is empty
        List<JobLog> jobLogs = jobLogRepository.findAll();
        assertThat(jobLogs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchJobLog() throws Exception {
        // Initialize the database
        jobLogRepository.saveAndFlush(jobLog);
        jobLogSearchRepository.save(jobLog);

        // Search the jobLog
        restJobLogMockMvc.perform(get("/api/_search/job-logs?query=id:" + jobLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].logDate").value(hasItem(DEFAULT_LOG_DATE.toString())));
    }
}
