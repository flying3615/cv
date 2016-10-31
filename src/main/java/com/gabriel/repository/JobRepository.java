package com.gabriel.repository;

import com.gabriel.domain.Job;

import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the Job entity.
 */
@SuppressWarnings("unused")
public interface JobRepository extends JpaRepository<Job,Long> {

    Set<Job> findBySearchWordAndFromSite(String searchWord, String fromSite);

}
