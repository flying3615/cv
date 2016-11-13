package com.gabriel.repository.search;

import com.gabriel.domain.JobCount;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the JobCount entity.
 */
public interface JobCountSearchRepository extends ElasticsearchRepository<JobCount, Long> {
}
