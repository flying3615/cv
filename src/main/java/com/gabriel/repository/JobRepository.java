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
public interface JobRepository extends JpaRepository<Job, Long>,JpaSpecificationExecutor<Job> {

    Set<Job> findBySearchWordAndFromSite(String java, String from_site);

    @Query(
        value = "select * from job as j where j.search_word= ?1 and j.id not in (select job_id from job_log)",
        nativeQuery = true
    )
    List<Job> countBySearchWord(String searchword);


    @Query(
        value = "select area, count(*) as job_count \n" +
            "from job j \n" +
            "where area in (SELECT distinct area FROM job where area<>null or area<>'') and search_word= ?1 and j.isremoved is null group by area \n" +
            "union \n" +
            "select j.location as area, count(*) as job_count from job j\n" +
            "where location in (SELECT distinct location FROM job where area is null or area='') and search_word= ?1 and j.isremoved is null and area is null group by j.location",
        nativeQuery = true
    )
    Object[] getMapDataByWord(String keyword);



    @Query(value = "select area, count(*) as job_count \n" +
        "from job j \n" +
        "where area in (SELECT distinct area FROM job where area<>null or area<>'') and j.isremoved is null group by area \n" +
        "union \n" +
        "select j.location as area, count(*) as job_count from job j\n" +
        "where location in (SELECT distinct location FROM job where area is null or area='') and j.isremoved is null and area is null group by j.location",
    nativeQuery = true)
    Object[] getMapDataAll();


    @Query(value="select creation_time,search_word, count(*) from job  where search_word= ?1 group by search_word, creation_time",
        nativeQuery = true)
    Object[] getJobTrend(String keyword);


    @Query(value="select exter.external_id, search_word from job as j, (select count(*), external_id from job group by external_id having count(*)>1) as exter where j.external_id=exter.external_id and keywords is NULL order by exter.external_id",nativeQuery = true)
    Object[] getDuplicateJobs();

    List<Job> findByExternalID(String external_id);

    List<Job> findByKeywordsIsNull();

    @Query(value="select * from job where experience_req is not null;",nativeQuery = true)
    List<Job> findByExpIsNotNull();
}
