package com.gabriel.web.rest.DTO;

import java.util.List;
import java.util.Map;

/**
 * Created by liuyufei on 12/11/16.
 */
public class StateDTO {

    private String word;

    public StateDTO(String word, List<Long> jobID, Long value, Map<String, String> statsticMap) {
        this.word = word;
        this.value = value;
        this.statsticMap = statsticMap;
    }

    private Long value;

    private Map<String, String> statsticMap;


    public String getWord() {
        return word;
    }

    public Long getValue() {
        return value;
    }

    public Map<String, String> getStatsticMap() {
        return statsticMap;
    }
}
