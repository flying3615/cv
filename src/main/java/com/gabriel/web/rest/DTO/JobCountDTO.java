package com.gabriel.web.rest.DTO;

/**
 * Created by liuyufei on 3/11/16.
 */
public class JobCountDTO {

    String name;
    Long value;

    public JobCountDTO(String name, Long value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
