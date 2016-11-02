package com.gabriel.repository;

import com.gabriel.domain.JobLog;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the JobLog entity.
 */
@SuppressWarnings("unused")
public interface JobLogRepository extends JpaRepository<JobLog,Long> {

}
