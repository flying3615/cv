package com.gabriel.service.task;

import com.gabriel.domain.Job;
import com.gabriel.repository.JobRepository;
import com.gabriel.service.crawler.Crawler;
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
    Map<String, Crawler> crawlerStrategy = new HashMap<>();

    @Value("${crawler.seek.from_site}")
    String from_site;

    @Inject
    JobRepository jobRepository;

//    @Scheduled(cron = "0 0 6 * * *")  //@ 6:00:00 am every day
    @Scheduled(cron = "0 */5 * * * *") //every ten minutes for test
    public void dailyCrawl() {
        log.info("crawl task start @ {}", LocalDateTime.now());
        Set<Map.Entry<String, Crawler>> crawlerSet = crawlerStrategy.entrySet();
        crawlerSet.stream().forEach(crawlerEntry -> {

            log.info("{}->{} ready to GO!!!", crawlerEntry.getKey(), crawlerEntry.getValue());

            Crawler crawler = crawlerEntry.getValue();


            //check if job exist...
            Set<Job> exsiting_jobs = jobRepository.findBySearchWordAndFromSite("java", from_site);
            Set<Job> now_jobs = crawler.listJobs("java");

            //only care about the latest jobs
            exsiting_jobs.forEach(exsiting_job -> {
                    if (now_jobs.contains(exsiting_job)) {
                        now_jobs.remove(exsiting_jobs);
                    }
                }

            );


            jobRepository.save(now_jobs);

            //update job detail
            if(now_jobs.size()!=0){
                log.info("update new coming jobs {}, {}",now_jobs.size(),now_jobs);
                now_jobs.forEach(rest_job->{
                    crawler.updateJobDetail(rest_job);
                });
                //send mail notify now coming jobs
            }else{
                log.info("No job today...");
            }


        });

    }
}
