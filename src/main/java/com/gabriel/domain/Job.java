package com.gabriel.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Job.
 */
@Entity
@Table(name = "job")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "job")
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "company", nullable = false)
    private String company;

    @NotNull
    @Column(name = "external_id", nullable = false)
    private String external_id;

    @Column(name = "list_date")
    private LocalDate list_date;

    @Column(name = "salary")
    private String salary;

    @Column(name = "location")
    private String location;

    @Column(name = "work_type")
    private String work_type;

    @Column(name = "description")
    private String description;

    @Column(name = "keywords")
    private String keywords;

    @NotNull
    @Column(name = "search_word", nullable = false)
    private String search_word;

    @NotNull
    @Column(name = "from_site", nullable = false)
    private String from_site;

    @Column(name = "contact")
    private String contact;

    @Column(name = "creation_time")
    private ZonedDateTime creation_time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Job title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public Job company(String company) {
        this.company = company;
        return this;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getExternal_id() {
        return external_id;
    }

    public Job external_id(String external_id) {
        this.external_id = external_id;
        return this;
    }

    public void setExternal_id(String external_id) {
        this.external_id = external_id;
    }

    public LocalDate getList_date() {
        return list_date;
    }

    public Job list_date(LocalDate list_date) {
        this.list_date = list_date;
        return this;
    }

    public void setList_date(LocalDate list_date) {
        this.list_date = list_date;
    }

    public String getSalary() {
        return salary;
    }

    public Job salary(String salary) {
        this.salary = salary;
        return this;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getLocation() {
        return location;
    }

    public Job location(String location) {
        this.location = location;
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWork_type() {
        return work_type;
    }

    public Job work_type(String work_type) {
        this.work_type = work_type;
        return this;
    }

    public void setWork_type(String work_type) {
        this.work_type = work_type;
    }

    public String getDescription() {
        return description;
    }

    public Job description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public Job keywords(String keywords) {
        this.keywords = keywords;
        return this;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getSearch_word() {
        return search_word;
    }

    public Job search_word(String search_word) {
        this.search_word = search_word;
        return this;
    }

    public void setSearch_word(String search_word) {
        this.search_word = search_word;
    }

    public String getFrom_site() {
        return from_site;
    }

    public Job from_site(String from_site) {
        this.from_site = from_site;
        return this;
    }

    public void setFrom_site(String from_site) {
        this.from_site = from_site;
    }

    public String getContact() {
        return contact;
    }

    public Job contact(String contact) {
        this.contact = contact;
        return this;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public ZonedDateTime getCreation_time() {
        return creation_time;
    }

    public Job creation_time(ZonedDateTime creation_time) {
        this.creation_time = creation_time;
        return this;
    }

    public void setCreation_time(ZonedDateTime creation_time) {
        this.creation_time = creation_time;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        Job job = (Job) o;
//        if(job.id == null || id == null) {
//            return false;
//        }
//        return Objects.equals(id, job.id);
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return external_id.equals(job.external_id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(external_id);
    }

    @Override
    public String toString() {
        return "Job{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", company='" + company + "'" +
            ", external_id='" + external_id + "'" +
            ", list_date='" + list_date + "'" +
            ", salary='" + salary + "'" +
            ", location='" + location + "'" +
            ", work_type='" + work_type + "'" +
            ", description='" + description + "'" +
            ", keywords='" + keywords + "'" +
            ", search_word='" + search_word + "'" +
            ", from_site='" + from_site + "'" +
            ", contact='" + contact + "'" +
            ", creation_time='" + creation_time + "'" +
            '}';
    }
}
