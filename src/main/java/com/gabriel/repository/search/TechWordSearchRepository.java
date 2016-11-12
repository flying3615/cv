package com.gabriel.repository.search;

import com.gabriel.domain.TechWord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the TechWord entity.
 */
public interface TechWordSearchRepository extends ElasticsearchRepository<TechWord, Long> {
}
