package com.gabriel.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A JobCount.
 */
@Entity
@Table(name = "job_count")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "jobcount")
public class JobCount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "log_date")
    private LocalDate logDate;

    @Column(name = "job_number")
    private Long jobNumber;

    @ManyToOne
    private SearchWord searchWord;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public JobCount logDate(LocalDate logDate) {
        this.logDate = logDate;
        return this;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public Long getJobNumber() {
        return jobNumber;
    }

    public JobCount jobNumber(Long jobNumber) {
        this.jobNumber = jobNumber;
        return this;
    }

    public void setJobNumber(Long jobNumber) {
        this.jobNumber = jobNumber;
    }

    public SearchWord getSearchWord() {
        return searchWord;
    }

    public JobCount searchWord(SearchWord searchWord) {
        this.searchWord = searchWord;
        return this;
    }

    public void setSearchWord(SearchWord searchWord) {
        this.searchWord = searchWord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobCount jobCount = (JobCount) o;
        if(jobCount.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, jobCount.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "JobCount{" +
            "id=" + id +
            ", logDate='" + logDate + "'" +
            ", jobNumber='" + jobNumber + "'" +
            '}';
    }
}
