package com.gabriel.repository;

import com.gabriel.domain.Job;

import com.gabriel.domain.User;
import com.gabriel.web.rest.DTO.GoogleLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.SqlResultSetMapping;
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




    @Query(value = "select area, count(*) as job_count,search_word " +
        "from job j,job_log jl " +
        "where area in (SELECT distinct area FROM JOB where area<>null or area<>'') and search_word= ?1 and j.id=jl.id and jl.type<>'REMOVE' " +
        "group by area " +
        "union " +
        "select location as area, count(*) as job_count,search_word " +
        "from job j,job_log jl " +
        "where location in (SELECT distinct location FROM JOB where area=null or area='') and search_word= ?1 and j.id=jl.id and jl.type<>'REMOVE' and (area='' or area=null) " +
        "group by area",
        nativeQuery = true
    )
    Object[] getMapDataByWord(String keyword);
}
