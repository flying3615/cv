package com.gabriel.web.rest;

import com.gabriel.CvApp;

import com.gabriel.domain.SearchWord;
import com.gabriel.repository.SearchWordRepository;
import com.gabriel.repository.search.SearchWordSearchRepository;

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
 * Test class for the SearchWordResource REST controller.
 *
 * @see SearchWordResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CvApp.class)
public class SearchWordResourceIntTest {

    private static final String DEFAULT_WORD_NAME = "AAAAA";
    private static final String UPDATED_WORD_NAME = "BBBBB";

    @Inject
    private SearchWordRepository searchWordRepository;

    @Inject
    private SearchWordSearchRepository searchWordSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restSearchWordMockMvc;

    private SearchWord searchWord;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SearchWordResource searchWordResource = new SearchWordResource();
        ReflectionTestUtils.setField(searchWordResource, "searchWordSearchRepository", searchWordSearchRepository);
        ReflectionTestUtils.setField(searchWordResource, "searchWordRepository", searchWordRepository);
        this.restSearchWordMockMvc = MockMvcBuilders.standaloneSetup(searchWordResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SearchWord createEntity(EntityManager em) {
        SearchWord searchWord = new SearchWord()
                .wordName(DEFAULT_WORD_NAME);
        return searchWord;
    }

    @Before
    public void initTest() {
        searchWordSearchRepository.deleteAll();
        searchWord = createEntity(em);
    }

    @Test
    @Transactional
    public void createSearchWord() throws Exception {
        int databaseSizeBeforeCreate = searchWordRepository.findAll().size();

        // Create the SearchWord

        restSearchWordMockMvc.perform(post("/api/search-words")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(searchWord)))
                .andExpect(status().isCreated());

        // Validate the SearchWord in the database
        List<SearchWord> searchWords = searchWordRepository.findAll();
        assertThat(searchWords).hasSize(databaseSizeBeforeCreate + 1);
        SearchWord testSearchWord = searchWords.get(searchWords.size() - 1);
        assertThat(testSearchWord.getWordName()).isEqualTo(DEFAULT_WORD_NAME);

        // Validate the SearchWord in ElasticSearch
        SearchWord searchWordEs = searchWordSearchRepository.findOne(testSearchWord.getId());
        assertThat(searchWordEs).isEqualToComparingFieldByField(testSearchWord);
    }

    @Test
    @Transactional
    public void getAllSearchWords() throws Exception {
        // Initialize the database
        searchWordRepository.saveAndFlush(searchWord);

        // Get all the searchWords
        restSearchWordMockMvc.perform(get("/api/search-words?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(searchWord.getId().intValue())))
                .andExpect(jsonPath("$.[*].wordName").value(hasItem(DEFAULT_WORD_NAME.toString())));
    }

    @Test
    @Transactional
    public void getSearchWord() throws Exception {
        // Initialize the database
        searchWordRepository.saveAndFlush(searchWord);

        // Get the searchWord
        restSearchWordMockMvc.perform(get("/api/search-words/{id}", searchWord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(searchWord.getId().intValue()))
            .andExpect(jsonPath("$.wordName").value(DEFAULT_WORD_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSearchWord() throws Exception {
        // Get the searchWord
        restSearchWordMockMvc.perform(get("/api/search-words/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSearchWord() throws Exception {
        // Initialize the database
        searchWordRepository.saveAndFlush(searchWord);
        searchWordSearchRepository.save(searchWord);
        int databaseSizeBeforeUpdate = searchWordRepository.findAll().size();

        // Update the searchWord
        SearchWord updatedSearchWord = searchWordRepository.findOne(searchWord.getId());
        updatedSearchWord
                .wordName(UPDATED_WORD_NAME);

        restSearchWordMockMvc.perform(put("/api/search-words")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSearchWord)))
                .andExpect(status().isOk());

        // Validate the SearchWord in the database
        List<SearchWord> searchWords = searchWordRepository.findAll();
        assertThat(searchWords).hasSize(databaseSizeBeforeUpdate);
        SearchWord testSearchWord = searchWords.get(searchWords.size() - 1);
        assertThat(testSearchWord.getWordName()).isEqualTo(UPDATED_WORD_NAME);

        // Validate the SearchWord in ElasticSearch
        SearchWord searchWordEs = searchWordSearchRepository.findOne(testSearchWord.getId());
        assertThat(searchWordEs).isEqualToComparingFieldByField(testSearchWord);
    }

    @Test
    @Transactional
    public void deleteSearchWord() throws Exception {
        // Initialize the database
        searchWordRepository.saveAndFlush(searchWord);
        searchWordSearchRepository.save(searchWord);
        int databaseSizeBeforeDelete = searchWordRepository.findAll().size();

        // Get the searchWord
        restSearchWordMockMvc.perform(delete("/api/search-words/{id}", searchWord.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean searchWordExistsInEs = searchWordSearchRepository.exists(searchWord.getId());
        assertThat(searchWordExistsInEs).isFalse();

        // Validate the database is empty
        List<SearchWord> searchWords = searchWordRepository.findAll();
        assertThat(searchWords).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSearchWord() throws Exception {
        // Initialize the database
        searchWordRepository.saveAndFlush(searchWord);
        searchWordSearchRepository.save(searchWord);

        // Search the searchWord
        restSearchWordMockMvc.perform(get("/api/_search/search-words?query=id:" + searchWord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(searchWord.getId().intValue())))
            .andExpect(jsonPath("$.[*].wordName").value(hasItem(DEFAULT_WORD_NAME.toString())));
    }
}
