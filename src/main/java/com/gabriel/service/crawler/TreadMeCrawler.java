package com.gabriel.service.crawler;

import com.gabriel.domain.Job;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liuyufei on 31/10/16.
 */

@Component
public class TreadMeCrawler implements Crawlers {
    @Override
    public List<Job> listJobs(String searchWord) {
        return null;
    }

    @Override
    public Job jobDetail(String jobID) {
        return null;
    }
}
