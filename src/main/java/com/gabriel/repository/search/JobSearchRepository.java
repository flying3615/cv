package com.gabriel.repository.search;

import com.gabriel.domain.Job;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data ElasticSearch repository for the Job entity.
 */
public interface JobSearchRepository extends ElasticsearchRepository<Job, Long> {


}
