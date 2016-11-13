package com.gabriel.repository;

import com.gabriel.domain.JobCount;

import com.gabriel.domain.SearchWord;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the JobCount entity.
 */
public interface JobCountRepository extends JpaRepository<JobCount,Long> {

    List<JobCount> findBySearchWord(SearchWord search_word_id);

}
