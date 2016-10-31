package com.gabriel.service.crawler;

import com.gabriel.domain.Job;

import java.util.List;

/**
 * Created by liuyufei on 31/10/16.
 */
public interface Crawlers {


    List<Job> listJobs(String searchWord);

    Job jobDetail(String jobID);



}
