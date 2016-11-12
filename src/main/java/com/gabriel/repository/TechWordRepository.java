package com.gabriel.repository;

import com.gabriel.domain.TechWord;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the TechWord entity.
 */
@SuppressWarnings("unused")
public interface TechWordRepository extends JpaRepository<TechWord,Long> {

    @Query("select techWord from TechWord techWord where techWord.user.login = ?#{principal.username}")
    List<TechWord> findByUserIsCurrentUser();

}
