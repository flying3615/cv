package com.gabriel.web.rest.DTO;

import java.util.Date;
import java.util.Map;

/**
 * Created by liuyufei on 5/11/16.
 */
public class JobTrendDTO {

    private String name;

    private Map<Date,Long> trend;


    public JobTrendDTO(String name, Map<Date, Long> trend) {
        this.name = name;
        this.trend = trend;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Date, Long> getTrend() {
        return trend;
    }

    public void setTrend(Map<Date, Long> trend) {
        this.trend = trend;
    }
}
