package com.gabriel.web.rest.DTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyufei on 5/11/16.
 */
public class JobTrendDTO {

    private String name;

    private List<String> date;

    private List<Long> jobNum;

    public JobTrendDTO(String name, List<String> date, List<Long> jobNum) {
        this.name = name;
        this.date = date;
        this.jobNum = jobNum;
    }

    public String getName() {
        return name;
    }

    public List<String> getDate() {
        return date;
    }

    public List<Long> getJobNum() {
        return jobNum;
    }
}
