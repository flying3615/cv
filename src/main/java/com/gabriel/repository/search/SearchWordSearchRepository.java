package com.gabriel.repository.search;

import com.gabriel.domain.SearchWord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the SearchWord entity.
 */
public interface SearchWordSearchRepository extends ElasticsearchRepository<SearchWord, Long> {
}
