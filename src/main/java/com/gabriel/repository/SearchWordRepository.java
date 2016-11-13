package com.gabriel.repository;

import com.gabriel.domain.SearchWord;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the SearchWord entity.
 */
public interface SearchWordRepository extends JpaRepository<SearchWord,Long> {

    SearchWord findByWordName(String wordName);
}
