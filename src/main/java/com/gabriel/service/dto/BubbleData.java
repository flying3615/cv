package com.gabriel.service.dto;

/**
 * Created by liuyufei on 14/11/16.
 */
public class BubbleData {

    //radius
    private int location_total_jobs;

    //y
    private double score;

    //x
    private String list_date;

    //type
    private String language;

    //tag
    private String location;


    private long job_id;


    public int getLocation_total_jobs() {
        return location_total_jobs;
    }

    public void setLocation_total_jobs(int location_total_jobs) {
        this.location_total_jobs = location_total_jobs;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getList_date() {
        return list_date;
    }

    public void setList_date(String list_date) {
        this.list_date = list_date;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getJob_id() {
        return job_id;
    }

    public void setJob_id(long job_id) {
        this.job_id = job_id;
    }
}
