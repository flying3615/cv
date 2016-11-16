package com.gabriel.repository;

import com.gabriel.domain.Job;

import com.gabriel.domain.User;
import com.gabriel.web.rest.DTO.GoogleLocation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
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
        value = "select * from job as j where j.search_word= ?1 and j.id not in (select job_id from job_log)",
        nativeQuery = true
    )
    List<Job> countBySearchWord(String searchword);


    @Query(value = "select area, count(*) as job_count,search_word " +
        "from job j,job_log jl " +
        "where area in (SELECT distinct area FROM job where area<>null or area<>'') and search_word= ?1 and j.id=jl.id and jl.type<>'REMOVE' " +
        "group by area " +
        "union " +
        "select j.location as area, count(*) as job_count,search_word " +
        "from job j,job_log jl " +
        "where location in (SELECT distinct location FROM job where area=null or area='') and search_word= ?1 and j.id=jl.id and jl.type<>'REMOVE' and (area='' or area=null) " +
        "group by j.location",
        nativeQuery = true
    )
    Object[] getMapDataByWord(String keyword);



    @Query(value="select area, count(*) as job_count " +
        "from job j,job_log jl " +
        "where area in (SELECT distinct area FROM job where area<>null or area<>'') and j.id=jl.id and jl.type<>'REMOVE' " +
        "group by area " +
        "union " +
        "select j.location as area, count(*) as job_count " +
        "from job j,job_log jl " +
        "where location in (SELECT distinct location FROM job where area=null or area='') and j.id=jl.id and jl.type<>'REMOVE' and area='' " +
        "group by j.location",
    nativeQuery = true)
    Object[] getMapDataAll();


    @Query(value="select creation_time,search_word, count(*) from job  where search_word= ?1 group by search_word, creation_time",
        nativeQuery = true)
    Object[] getJobTrend(String keyword);


    @Query(value="select exter.external_id, search_word from job as j, (select count(*), external_id from job group by external_id having count(*)>1) as exter where j.external_id=exter.external_id and keywords is NULL order by exter.external_id",nativeQuery = true)
    Object[] getDuplicateJobs();

    List<Job> findByExternalID(String external_id);

}
