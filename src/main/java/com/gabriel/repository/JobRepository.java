package com.gabriel.repository;

import com.gabriel.domain.Job;

import com.gabriel.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the Job entity.
 */
@SuppressWarnings("unused")
public interface JobRepository extends JpaRepository<Job, Long> {

    Set<Job> findBySearchWordAndFromSite(String java, String from_site);

    @Query(
        value = "SELECT j from Job j, JobLog jl where  j.id=jl.job.id and jl.type <> 'REMOVE' and j.searchWord= ?1",
        countQuery = "SELECT count(j) from Job j, JobLog jl where  j.id=jl.job.id and jl.type <> 'REMOVE' and j.searchWord= ?1"
    )
    Page<Job> countBySearchWord(String searchword, Pageable pageable);


}
