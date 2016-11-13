package com.gabriel.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.gabriel.domain.SearchWord;

import com.gabriel.repository.SearchWordRepository;
import com.gabriel.repository.search.SearchWordSearchRepository;
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
 * REST controller for managing SearchWord.
 */
@RestController
@RequestMapping("/api")
public class SearchWordResource {

    private final Logger log = LoggerFactory.getLogger(SearchWordResource.class);
        
    @Inject
    private SearchWordRepository searchWordRepository;

    @Inject
    private SearchWordSearchRepository searchWordSearchRepository;

    /**
     * POST  /search-words : Create a new searchWord.
     *
     * @param searchWord the searchWord to create
     * @return the ResponseEntity with status 201 (Created) and with body the new searchWord, or with status 400 (Bad Request) if the searchWord has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/search-words")
    @Timed
    public ResponseEntity<SearchWord> createSearchWord(@RequestBody SearchWord searchWord) throws URISyntaxException {
        log.debug("REST request to save SearchWord : {}", searchWord);
        if (searchWord.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("searchWord", "idexists", "A new searchWord cannot already have an ID")).body(null);
        }
        SearchWord result = searchWordRepository.save(searchWord);
        searchWordSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/search-words/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("searchWord", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /search-words : Updates an existing searchWord.
     *
     * @param searchWord the searchWord to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated searchWord,
     * or with status 400 (Bad Request) if the searchWord is not valid,
     * or with status 500 (Internal Server Error) if the searchWord couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/search-words")
    @Timed
    public ResponseEntity<SearchWord> updateSearchWord(@RequestBody SearchWord searchWord) throws URISyntaxException {
        log.debug("REST request to update SearchWord : {}", searchWord);
        if (searchWord.getId() == null) {
            return createSearchWord(searchWord);
        }
        SearchWord result = searchWordRepository.save(searchWord);
        searchWordSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("searchWord", searchWord.getId().toString()))
            .body(result);
    }

    /**
     * GET  /search-words : get all the searchWords.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of searchWords in body
     */
    @GetMapping("/search-words")
    @Timed
    public List<SearchWord> getAllSearchWords() {
        log.debug("REST request to get all SearchWords");
        List<SearchWord> searchWords = searchWordRepository.findAll();
        return searchWords;
    }

    /**
     * GET  /search-words/:id : get the "id" searchWord.
     *
     * @param id the id of the searchWord to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the searchWord, or with status 404 (Not Found)
     */
    @GetMapping("/search-words/{id}")
    @Timed
    public ResponseEntity<SearchWord> getSearchWord(@PathVariable Long id) {
        log.debug("REST request to get SearchWord : {}", id);
        SearchWord searchWord = searchWordRepository.findOne(id);
        return Optional.ofNullable(searchWord)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /search-words/:id : delete the "id" searchWord.
     *
     * @param id the id of the searchWord to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/search-words/{id}")
    @Timed
    public ResponseEntity<Void> deleteSearchWord(@PathVariable Long id) {
        log.debug("REST request to delete SearchWord : {}", id);
        searchWordRepository.delete(id);
        searchWordSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("searchWord", id.toString())).build();
    }

    /**
     * SEARCH  /_search/search-words?query=:query : search for the searchWord corresponding
     * to the query.
     *
     * @param query the query of the searchWord search 
     * @return the result of the search
     */
    @GetMapping("/_search/search-words")
    @Timed
    public List<SearchWord> searchSearchWords(@RequestParam String query) {
        log.debug("REST request to search SearchWords for query {}", query);
        return StreamSupport
            .stream(searchWordSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
