package com.gabriel.service.crawler;

import com.gabriel.domain.Job;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuyufei on 31/10/16.
 */

@Component
public class TreadMeCrawler implements Crawler {

    @Override
    public String getFromSite() {
        return "TREADME";
    }

    @Override
    public Map<String,Job> listJobs(String searchWord) {
        return new HashMap<>();
    }

    @Override
    public Job updateJobDetail(Job job) {
        return job;
    }

    @Override
    public boolean isJobValid(String url) {
        return true;
    }
}
