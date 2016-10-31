package com.gabriel.service.crawler;

import com.gabriel.domain.Job;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liuyufei on 31/10/16.
 */

@Component
public class TreadMeCrawler implements Crawler {

    @Override
    public Set<Job> listJobs(String searchWord) {
        return new HashSet<>();
    }

    @Override
    public void updateJobDetail(Job jobID) {

    }
}
