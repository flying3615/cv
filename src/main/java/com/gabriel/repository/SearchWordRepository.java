package com.gabriel.repository;

import com.gabriel.domain.SearchWord;

import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the SearchWord entity.
 */
public interface SearchWordRepository extends JpaRepository<SearchWord,Long> {

    Optional<SearchWord> findByWordName(String wordName);
}
