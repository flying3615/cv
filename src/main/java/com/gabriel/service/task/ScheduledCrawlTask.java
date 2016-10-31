package com.gabriel.service.task;

import com.gabriel.domain.Job;
import com.gabriel.repository.JobRepository;
import com.gabriel.service.crawler.Crawlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuyufei on 31/10/16.
 */
@Component
public class ScheduledCrawlTask {


    private final Logger log = LoggerFactory.getLogger(ScheduledCrawlTask.class);

    @Inject
    Map<String,Crawlers> crawlerStrategy = new HashMap<>();

    @Value("${crawler.seek.from_site}")
    String from_site;

    @Inject
    JobRepository jobRepository;

    @Scheduled(cron = "0 0 6 * * *")  //@ 6:00:00 am every day
//    @Scheduled(cron = "0 */5 * * * *") //every ten minutes for test
    public void dailyCrawl(){
        log.info("crawl task start @ {}", LocalDateTime.now());
        Set<Map.Entry<String,Crawlers>> crawlerSet = crawlerStrategy.entrySet();
        crawlerSet.stream().forEach(crawler -> {
            log.info("{}->{} ready to GO!!!",crawler.getKey(),crawler.getValue());

            Set<Job> jobs = jobRepository.findBySearch_wordAndFrom_site("java",from_site);
            jobs.addAll(crawler.getValue().listJobs("java"));
            //TODO check if job exist...
            jobRepository.save(jobs);
        });

    }
}
