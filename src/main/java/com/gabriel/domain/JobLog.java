package com.gabriel.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import com.gabriel.domain.enumeration.JobLogType;

/**
 * A JobLog.
 */
@Entity
@Table(name = "job_log")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "joblog")
public class JobLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private JobLogType type;

    @Column(name = "log_date")
    private LocalDate logDate;

    @ManyToOne
    private Job job;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobLogType getType() {
        return type;
    }

    public JobLog type(JobLogType type) {
        this.type = type;
        return this;
    }

    public void setType(JobLogType type) {
        this.type = type;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public JobLog logDate(LocalDate logDate) {
        this.logDate = logDate;
        return this;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public Job getJob() {
        return job;
    }

    public JobLog job(Job job) {
        this.job = job;
        return this;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobLog jobLog = (JobLog) o;
        if(jobLog.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, jobLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "JobLog{" +
            "id=" + id +
            ", type='" + type + "'" +
            ", logDate='" + logDate + "'" +
            '}';
    }

    public  JobLog(){}

    public JobLog(JobLogType type, LocalDate logDate, Job job) {
        this.type = type;
        this.logDate = logDate;
        this.job = job;
    }
}
