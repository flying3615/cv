package com.gabriel.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;

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

    @Column(name = "salary")
    private String salary;

    @Column(name = "location")
    private String location;

    @Column(name = "description")
    private String description;

    @Column(name = "keywords")
    private String keywords;

    @Column(name = "contact")
    private String contact;

    @Column(name = "creation_time")
    private ZonedDateTime creationTime;

    @Column(name = "external_id")
    private String externalID;

    @Column(name = "search_word")
    private String searchWord;

    @Column(name = "work_type")
    private String workType;

    @Column(name = "list_date")
    private LocalDate listDate;

    @Column(name = "from_site")
    private String fromSite;

    @Column(name = "orig_url")
    private String origURL;

    @Column(name = "experience_req")
    private String experienceReq;

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

    public ZonedDateTime getCreationTime() {
        return creationTime;
    }

    public Job creationTime(ZonedDateTime creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public void setCreationTime(ZonedDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public String getExternalID() {
        return externalID;
    }

    public Job externalID(String externalID) {
        this.externalID = externalID;
        return this;
    }

    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }

    public String getSearchWord() {
        return searchWord;
    }

    public Job searchWord(String searchWord) {
        this.searchWord = searchWord;
        return this;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public String getWorkType() {
        return workType;
    }

    public Job workType(String workType) {
        this.workType = workType;
        return this;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public LocalDate getListDate() {
        return listDate;
    }

    public Job listDate(LocalDate listDate) {
        this.listDate = listDate;
        return this;
    }

    public void setListDate(LocalDate listDate) {
        this.listDate = listDate;
    }

    public String getFromSite() {
        return fromSite;
    }

    public Job fromSite(String fromSite) {
        this.fromSite = fromSite;
        return this;
    }

    public void setFromSite(String fromSite) {
        this.fromSite = fromSite;
    }

    public String getOrigURL() {
        return origURL;
    }

    public Job origURL(String origURL) {
        this.origURL = origURL;
        return this;
    }

    public void setOrigURL(String origURL) {
        this.origURL = origURL;
    }

    public String getExperienceReq() {
        return experienceReq;
    }

    public Job experienceReq(String experienceReq) {
        this.experienceReq = experienceReq;
        return this;
    }

    public void setExperienceReq(String experienceReq) {
        this.experienceReq = experienceReq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Job job = (Job) o;
        if(job.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, job.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Job{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", company='" + company + "'" +
            ", salary='" + salary + "'" +
            ", location='" + location + "'" +
            ", description='" + description + "'" +
            ", keywords='" + keywords + "'" +
            ", contact='" + contact + "'" +
            ", creationTime='" + creationTime + "'" +
            ", externalID='" + externalID + "'" +
            ", searchWord='" + searchWord + "'" +
            ", workType='" + workType + "'" +
            ", listDate='" + listDate + "'" +
            ", fromSite='" + fromSite + "'" +
            ", origURL='" + origURL + "'" +
            ", experienceReq='" + experienceReq + "'" +
            '}';
    }
}
