package com.gabriel.service.crawler;

import com.gabriel.domain.Job;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuyufei on 31/10/16.
 */
public interface Crawler {


    Map<String,Job> listJobs(String searchWord);

    void updateJobDetail(Job jobID);



}
