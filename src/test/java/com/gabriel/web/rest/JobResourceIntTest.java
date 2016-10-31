package com.gabriel.web.rest;

import com.gabriel.CvApp;

import com.gabriel.domain.Job;
import com.gabriel.repository.JobRepository;
import com.gabriel.service.JobService;
import com.gabriel.repository.search.JobSearchRepository;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the JobResource REST controller.
 *
 * @see JobResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CvApp.class)
public class JobResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAA";
    private static final String UPDATED_TITLE = "BBBBB";

    private static final String DEFAULT_COMPANY = "AAAAA";
    private static final String UPDATED_COMPANY = "BBBBB";

    private static final String DEFAULT_SALARY = "AAAAA";
    private static final String UPDATED_SALARY = "BBBBB";

    private static final String DEFAULT_LOCATION = "AAAAA";
    private static final String UPDATED_LOCATION = "BBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final String DEFAULT_KEYWORDS = "AAAAA";
    private static final String UPDATED_KEYWORDS = "BBBBB";

    private static final String DEFAULT_CONTACT = "AAAAA";
    private static final String UPDATED_CONTACT = "BBBBB";

    private static final ZonedDateTime DEFAULT_CREATION_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATION_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATION_TIME_STR = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(DEFAULT_CREATION_TIME);

    private static final String DEFAULT_EXTERNAL_ID = "AAAAA";
    private static final String UPDATED_EXTERNAL_ID = "BBBBB";

    private static final String DEFAULT_SEARCH_WORD = "AAAAA";
    private static final String UPDATED_SEARCH_WORD = "BBBBB";

    private static final String DEFAULT_WORK_TYPE = "AAAAA";
    private static final String UPDATED_WORK_TYPE = "BBBBB";

    private static final LocalDate DEFAULT_LIST_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LIST_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_FROM_SITE = "AAAAA";
    private static final String UPDATED_FROM_SITE = "BBBBB";

    @Inject
    private JobRepository jobRepository;

    @Inject
    private JobService jobService;

    @Inject
    private JobSearchRepository jobSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restJobMockMvc;

    private Job job;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        JobResource jobResource = new JobResource();
        ReflectionTestUtils.setField(jobResource, "jobService", jobService);
        this.restJobMockMvc = MockMvcBuilders.standaloneSetup(jobResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Job createEntity(EntityManager em) {
        Job job = new Job()
                .title(DEFAULT_TITLE)
                .company(DEFAULT_COMPANY)
                .salary(DEFAULT_SALARY)
                .location(DEFAULT_LOCATION)
                .description(DEFAULT_DESCRIPTION)
                .keywords(DEFAULT_KEYWORDS)
                .contact(DEFAULT_CONTACT)
                .creationTime(DEFAULT_CREATION_TIME)
                .externalID(DEFAULT_EXTERNAL_ID)
                .searchWord(DEFAULT_SEARCH_WORD)
                .workType(DEFAULT_WORK_TYPE)
                .listDate(DEFAULT_LIST_DATE)
                .fromSite(DEFAULT_FROM_SITE);
        return job;
    }

    @Before
    public void initTest() {
        jobSearchRepository.deleteAll();
        job = createEntity(em);
    }

    @Test
    @Transactional
    public void createJob() throws Exception {
        int databaseSizeBeforeCreate = jobRepository.findAll().size();

        // Create the Job

        restJobMockMvc.perform(post("/api/jobs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(job)))
                .andExpect(status().isCreated());

        // Validate the Job in the database
        List<Job> jobs = jobRepository.findAll();
        assertThat(jobs).hasSize(databaseSizeBeforeCreate + 1);
        Job testJob = jobs.get(jobs.size() - 1);
        assertThat(testJob.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testJob.getCompany()).isEqualTo(DEFAULT_COMPANY);
        assertThat(testJob.getSalary()).isEqualTo(DEFAULT_SALARY);
        assertThat(testJob.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testJob.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testJob.getKeywords()).isEqualTo(DEFAULT_KEYWORDS);
        assertThat(testJob.getContact()).isEqualTo(DEFAULT_CONTACT);
        assertThat(testJob.getCreationTime()).isEqualTo(DEFAULT_CREATION_TIME);
        assertThat(testJob.getExternalID()).isEqualTo(DEFAULT_EXTERNAL_ID);
        assertThat(testJob.getSearchWord()).isEqualTo(DEFAULT_SEARCH_WORD);
        assertThat(testJob.getWorkType()).isEqualTo(DEFAULT_WORK_TYPE);
        assertThat(testJob.getListDate()).isEqualTo(DEFAULT_LIST_DATE);
        assertThat(testJob.getFromSite()).isEqualTo(DEFAULT_FROM_SITE);

        // Validate the Job in ElasticSearch
        Job jobEs = jobSearchRepository.findOne(testJob.getId());
        assertThat(jobEs).isEqualToComparingFieldByField(testJob);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobRepository.findAll().size();
        // set the field null
        job.setTitle(null);

        // Create the Job, which fails.

        restJobMockMvc.perform(post("/api/jobs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(job)))
                .andExpect(status().isBadRequest());

        List<Job> jobs = jobRepository.findAll();
        assertThat(jobs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCompanyIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobRepository.findAll().size();
        // set the field null
        job.setCompany(null);

        // Create the Job, which fails.

        restJobMockMvc.perform(post("/api/jobs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(job)))
                .andExpect(status().isBadRequest());

        List<Job> jobs = jobRepository.findAll();
        assertThat(jobs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllJobs() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobs
        restJobMockMvc.perform(get("/api/jobs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].company").value(hasItem(DEFAULT_COMPANY.toString())))
                .andExpect(jsonPath("$.[*].salary").value(hasItem(DEFAULT_SALARY.toString())))
                .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS.toString())))
                .andExpect(jsonPath("$.[*].contact").value(hasItem(DEFAULT_CONTACT.toString())))
                .andExpect(jsonPath("$.[*].creationTime").value(hasItem(DEFAULT_CREATION_TIME_STR)))
                .andExpect(jsonPath("$.[*].externalID").value(hasItem(DEFAULT_EXTERNAL_ID.toString())))
                .andExpect(jsonPath("$.[*].searchWord").value(hasItem(DEFAULT_SEARCH_WORD.toString())))
                .andExpect(jsonPath("$.[*].workType").value(hasItem(DEFAULT_WORK_TYPE.toString())))
                .andExpect(jsonPath("$.[*].listDate").value(hasItem(DEFAULT_LIST_DATE.toString())))
                .andExpect(jsonPath("$.[*].fromSite").value(hasItem(DEFAULT_FROM_SITE.toString())));
    }

    @Test
    @Transactional
    public void getJob() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get the job
        restJobMockMvc.perform(get("/api/jobs/{id}", job.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(job.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.company").value(DEFAULT_COMPANY.toString()))
            .andExpect(jsonPath("$.salary").value(DEFAULT_SALARY.toString()))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.keywords").value(DEFAULT_KEYWORDS.toString()))
            .andExpect(jsonPath("$.contact").value(DEFAULT_CONTACT.toString()))
            .andExpect(jsonPath("$.creationTime").value(DEFAULT_CREATION_TIME_STR))
            .andExpect(jsonPath("$.externalID").value(DEFAULT_EXTERNAL_ID.toString()))
            .andExpect(jsonPath("$.searchWord").value(DEFAULT_SEARCH_WORD.toString()))
            .andExpect(jsonPath("$.workType").value(DEFAULT_WORK_TYPE.toString()))
            .andExpect(jsonPath("$.listDate").value(DEFAULT_LIST_DATE.toString()))
            .andExpect(jsonPath("$.fromSite").value(DEFAULT_FROM_SITE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingJob() throws Exception {
        // Get the job
        restJobMockMvc.perform(get("/api/jobs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJob() throws Exception {
        // Initialize the database
        jobService.save(job);

        int databaseSizeBeforeUpdate = jobRepository.findAll().size();

        // Update the job
        Job updatedJob = jobRepository.findOne(job.getId());
        updatedJob
                .title(UPDATED_TITLE)
                .company(UPDATED_COMPANY)
                .salary(UPDATED_SALARY)
                .location(UPDATED_LOCATION)
                .description(UPDATED_DESCRIPTION)
                .keywords(UPDATED_KEYWORDS)
                .contact(UPDATED_CONTACT)
                .creationTime(UPDATED_CREATION_TIME)
                .externalID(UPDATED_EXTERNAL_ID)
                .searchWord(UPDATED_SEARCH_WORD)
                .workType(UPDATED_WORK_TYPE)
                .listDate(UPDATED_LIST_DATE)
                .fromSite(UPDATED_FROM_SITE);

        restJobMockMvc.perform(put("/api/jobs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedJob)))
                .andExpect(status().isOk());

        // Validate the Job in the database
        List<Job> jobs = jobRepository.findAll();
        assertThat(jobs).hasSize(databaseSizeBeforeUpdate);
        Job testJob = jobs.get(jobs.size() - 1);
        assertThat(testJob.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testJob.getCompany()).isEqualTo(UPDATED_COMPANY);
        assertThat(testJob.getSalary()).isEqualTo(UPDATED_SALARY);
        assertThat(testJob.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testJob.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testJob.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testJob.getContact()).isEqualTo(UPDATED_CONTACT);
        assertThat(testJob.getCreationTime()).isEqualTo(UPDATED_CREATION_TIME);
        assertThat(testJob.getExternalID()).isEqualTo(UPDATED_EXTERNAL_ID);
        assertThat(testJob.getSearchWord()).isEqualTo(UPDATED_SEARCH_WORD);
        assertThat(testJob.getWorkType()).isEqualTo(UPDATED_WORK_TYPE);
        assertThat(testJob.getListDate()).isEqualTo(UPDATED_LIST_DATE);
        assertThat(testJob.getFromSite()).isEqualTo(UPDATED_FROM_SITE);

        // Validate the Job in ElasticSearch
        Job jobEs = jobSearchRepository.findOne(testJob.getId());
        assertThat(jobEs).isEqualToComparingFieldByField(testJob);
    }

    @Test
    @Transactional
    public void deleteJob() throws Exception {
        // Initialize the database
        jobService.save(job);

        int databaseSizeBeforeDelete = jobRepository.findAll().size();

        // Get the job
        restJobMockMvc.perform(delete("/api/jobs/{id}", job.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean jobExistsInEs = jobSearchRepository.exists(job.getId());
        assertThat(jobExistsInEs).isFalse();

        // Validate the database is empty
        List<Job> jobs = jobRepository.findAll();
        assertThat(jobs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchJob() throws Exception {
        // Initialize the database
        jobService.save(job);

        // Search the job
        restJobMockMvc.perform(get("/api/_search/jobs?query=id:" + job.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(job.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].company").value(hasItem(DEFAULT_COMPANY.toString())))
            .andExpect(jsonPath("$.[*].salary").value(hasItem(DEFAULT_SALARY.toString())))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS.toString())))
            .andExpect(jsonPath("$.[*].contact").value(hasItem(DEFAULT_CONTACT.toString())))
            .andExpect(jsonPath("$.[*].creationTime").value(hasItem(DEFAULT_CREATION_TIME_STR)))
            .andExpect(jsonPath("$.[*].externalID").value(hasItem(DEFAULT_EXTERNAL_ID.toString())))
            .andExpect(jsonPath("$.[*].searchWord").value(hasItem(DEFAULT_SEARCH_WORD.toString())))
            .andExpect(jsonPath("$.[*].workType").value(hasItem(DEFAULT_WORK_TYPE.toString())))
            .andExpect(jsonPath("$.[*].listDate").value(hasItem(DEFAULT_LIST_DATE.toString())))
            .andExpect(jsonPath("$.[*].fromSite").value(hasItem(DEFAULT_FROM_SITE.toString())));
    }
}
