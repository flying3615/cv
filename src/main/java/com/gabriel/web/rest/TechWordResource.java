package com.gabriel.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.gabriel.domain.TechWord;

import com.gabriel.repository.TechWordRepository;
import com.gabriel.repository.search.TechWordSearchRepository;
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
 * REST controller for managing TechWord.
 */
@RestController
@RequestMapping("/api")
public class TechWordResource {

    private final Logger log = LoggerFactory.getLogger(TechWordResource.class);
        
    @Inject
    private TechWordRepository techWordRepository;

    @Inject
    private TechWordSearchRepository techWordSearchRepository;

    /**
     * POST  /tech-words : Create a new techWord.
     *
     * @param techWord the techWord to create
     * @return the ResponseEntity with status 201 (Created) and with body the new techWord, or with status 400 (Bad Request) if the techWord has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tech-words")
    @Timed
    public ResponseEntity<TechWord> createTechWord(@RequestBody TechWord techWord) throws URISyntaxException {
        log.debug("REST request to save TechWord : {}", techWord);
        if (techWord.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("techWord", "idexists", "A new techWord cannot already have an ID")).body(null);
        }
        TechWord result = techWordRepository.save(techWord);
        techWordSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/tech-words/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("techWord", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tech-words : Updates an existing techWord.
     *
     * @param techWord the techWord to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated techWord,
     * or with status 400 (Bad Request) if the techWord is not valid,
     * or with status 500 (Internal Server Error) if the techWord couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tech-words")
    @Timed
    public ResponseEntity<TechWord> updateTechWord(@RequestBody TechWord techWord) throws URISyntaxException {
        log.debug("REST request to update TechWord : {}", techWord);
        if (techWord.getId() == null) {
            return createTechWord(techWord);
        }
        TechWord result = techWordRepository.save(techWord);
        techWordSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("techWord", techWord.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tech-words : get all the techWords.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of techWords in body
     */
    @GetMapping("/tech-words")
    @Timed
    public List<TechWord> getAllTechWords() {
        log.debug("REST request to get all TechWords");
        List<TechWord> techWords = techWordRepository.findAll();
        return techWords;
    }

    /**
     * GET  /tech-words/:id : get the "id" techWord.
     *
     * @param id the id of the techWord to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the techWord, or with status 404 (Not Found)
     */
    @GetMapping("/tech-words/{id}")
    @Timed
    public ResponseEntity<TechWord> getTechWord(@PathVariable Long id) {
        log.debug("REST request to get TechWord : {}", id);
        TechWord techWord = techWordRepository.findOne(id);
        return Optional.ofNullable(techWord)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /tech-words/:id : delete the "id" techWord.
     *
     * @param id the id of the techWord to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tech-words/{id}")
    @Timed
    public ResponseEntity<Void> deleteTechWord(@PathVariable Long id) {
        log.debug("REST request to delete TechWord : {}", id);
        techWordRepository.delete(id);
        techWordSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("techWord", id.toString())).build();
    }

    /**
     * SEARCH  /_search/tech-words?query=:query : search for the techWord corresponding
     * to the query.
     *
     * @param query the query of the techWord search 
     * @return the result of the search
     */
    @GetMapping("/_search/tech-words")
    @Timed
    public List<TechWord> searchTechWords(@RequestParam String query) {
        log.debug("REST request to search TechWords for query {}", query);
        return StreamSupport
            .stream(techWordSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
