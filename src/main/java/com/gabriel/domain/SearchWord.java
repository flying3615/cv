package com.gabriel.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A SearchWord.
 */
@Entity
@Table(name = "search_word")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "searchword")
public class SearchWord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "word_name")
    private String wordName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWordName() {
        return wordName;
    }

    public SearchWord wordName(String wordName) {
        this.wordName = wordName;
        return this;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchWord searchWord = (SearchWord) o;
        if(searchWord.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, searchWord.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SearchWord{" +
            "id=" + id +
            ", wordName='" + wordName + "'" +
            '}';
    }
}
