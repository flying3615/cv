package com.gabriel.web.rest;

import com.gabriel.CvApp;

import com.gabriel.domain.TechWord;
import com.gabriel.repository.TechWordRepository;
import com.gabriel.repository.search.TechWordSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TechWordResource REST controller.
 *
 * @see TechWordResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CvApp.class)
public class TechWordResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final String DEFAULT_LANGUAGE = "AAAAA";
    private static final String UPDATED_LANGUAGE = "BBBBB";

    @Inject
    private TechWordRepository techWordRepository;

    @Inject
    private TechWordSearchRepository techWordSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restTechWordMockMvc;

    private TechWord techWord;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TechWordResource techWordResource = new TechWordResource();
        ReflectionTestUtils.setField(techWordResource, "techWordSearchRepository", techWordSearchRepository);
        ReflectionTestUtils.setField(techWordResource, "techWordRepository", techWordRepository);
        this.restTechWordMockMvc = MockMvcBuilders.standaloneSetup(techWordResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TechWord createEntity(EntityManager em) {
        TechWord techWord = new TechWord()
                .name(DEFAULT_NAME)
                .language(DEFAULT_LANGUAGE);
        return techWord;
    }

    @Before
    public void initTest() {
        techWordSearchRepository.deleteAll();
        techWord = createEntity(em);
    }

    @Test
    @Transactional
    public void createTechWord() throws Exception {
        int databaseSizeBeforeCreate = techWordRepository.findAll().size();

        // Create the TechWord

        restTechWordMockMvc.perform(post("/api/tech-words")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(techWord)))
                .andExpect(status().isCreated());

        // Validate the TechWord in the database
        List<TechWord> techWords = techWordRepository.findAll();
        assertThat(techWords).hasSize(databaseSizeBeforeCreate + 1);
        TechWord testTechWord = techWords.get(techWords.size() - 1);
        assertThat(testTechWord.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTechWord.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);

        // Validate the TechWord in ElasticSearch
        TechWord techWordEs = techWordSearchRepository.findOne(testTechWord.getId());
        assertThat(techWordEs).isEqualToComparingFieldByField(testTechWord);
    }

    @Test
    @Transactional
    public void getAllTechWords() throws Exception {
        // Initialize the database
        techWordRepository.saveAndFlush(techWord);

        // Get all the techWords
        restTechWordMockMvc.perform(get("/api/tech-words?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(techWord.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));
    }

    @Test
    @Transactional
    public void getTechWord() throws Exception {
        // Initialize the database
        techWordRepository.saveAndFlush(techWord);

        // Get the techWord
        restTechWordMockMvc.perform(get("/api/tech-words/{id}", techWord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(techWord.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTechWord() throws Exception {
        // Get the techWord
        restTechWordMockMvc.perform(get("/api/tech-words/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTechWord() throws Exception {
        // Initialize the database
        techWordRepository.saveAndFlush(techWord);
        techWordSearchRepository.save(techWord);
        int databaseSizeBeforeUpdate = techWordRepository.findAll().size();

        // Update the techWord
        TechWord updatedTechWord = techWordRepository.findOne(techWord.getId());
        updatedTechWord
                .name(UPDATED_NAME)
                .language(UPDATED_LANGUAGE);

        restTechWordMockMvc.perform(put("/api/tech-words")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTechWord)))
                .andExpect(status().isOk());

        // Validate the TechWord in the database
        List<TechWord> techWords = techWordRepository.findAll();
        assertThat(techWords).hasSize(databaseSizeBeforeUpdate);
        TechWord testTechWord = techWords.get(techWords.size() - 1);
        assertThat(testTechWord.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTechWord.getLanguage()).isEqualTo(UPDATED_LANGUAGE);

        // Validate the TechWord in ElasticSearch
        TechWord techWordEs = techWordSearchRepository.findOne(testTechWord.getId());
        assertThat(techWordEs).isEqualToComparingFieldByField(testTechWord);
    }

    @Test
    @Transactional
    public void deleteTechWord() throws Exception {
        // Initialize the database
        techWordRepository.saveAndFlush(techWord);
        techWordSearchRepository.save(techWord);
        int databaseSizeBeforeDelete = techWordRepository.findAll().size();

        // Get the techWord
        restTechWordMockMvc.perform(delete("/api/tech-words/{id}", techWord.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean techWordExistsInEs = techWordSearchRepository.exists(techWord.getId());
        assertThat(techWordExistsInEs).isFalse();

        // Validate the database is empty
        List<TechWord> techWords = techWordRepository.findAll();
        assertThat(techWords).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTechWord() throws Exception {
        // Initialize the database
        techWordRepository.saveAndFlush(techWord);
        techWordSearchRepository.save(techWord);

        // Search the techWord
        restTechWordMockMvc.perform(get("/api/_search/tech-words?query=id:" + techWord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(techWord.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));
    }
}
