package com.gabriel.repository;

import com.gabriel.domain.Job;

import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for the Job entity.
 */
@SuppressWarnings("unused")
public interface JobRepository extends JpaRepository<Job,Long> {


    Set<Job> findBySearch_wordAndFrom_site(String search_word,String from_site);

    Optional<Job> findByExternal_id(String external_id);

}
