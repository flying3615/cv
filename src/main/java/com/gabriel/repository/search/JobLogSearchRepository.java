package com.gabriel.repository.search;

import com.gabriel.domain.JobLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the JobLog entity.
 */
public interface JobLogSearchRepository extends ElasticsearchRepository<JobLog, Long> {
}
