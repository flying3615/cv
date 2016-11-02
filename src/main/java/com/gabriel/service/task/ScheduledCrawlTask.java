package com.gabriel.service.task;

import com.gabriel.domain.Job;
import com.gabriel.service.JobService;
import com.gabriel.service.crawler.Crawler;
import com.gabriel.service.util.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by liuyufei on 31/10/16.
 */
@Component
public class ScheduledCrawlTask {


    private final Logger log = LoggerFactory.getLogger(ScheduledCrawlTask.class);

    @Inject
    Map<String, Crawler> crawlerStrategy = new HashMap<>();

    @Inject
    MailSender mailSender;

    @Inject
    JobService jobService;

    @Scheduled(cron = "0 0 6 * * *")  //@ 6:00:00 am every day
//    @Scheduled(cron = "0 */5 * * * *") //every ten minutes for test
    public void dailyCrawl() {

        String searchKeyword = "java";


        log.info("crawl task start @ {}", LocalDateTime.now());
        Set<Map.Entry<String, Crawler>> crawlerSet = crawlerStrategy.entrySet();
        crawlerSet.stream().forEach(crawlerEntry -> {

            log.info("{} ready to GO!!!", crawlerEntry.getKey());

            Crawler crawler = crawlerEntry.getValue();

            //check if job exist...
            Set<Job> exciting_jobs = jobService.findBySearchWordAndFromSite(searchKeyword, crawler.getFromSite());
            Map<String, Job> now_jobs = crawler.listJobs(searchKeyword);


            log.info("existing jobs size {}", exciting_jobs.size());

            Set<Job> ready_to_remove = new HashSet<>();
            //only care about the latest jobs
            exciting_jobs.forEach(existing_job -> {
                    if (now_jobs.containsKey(existing_job.getExternalID())) {
                        now_jobs.remove(existing_job.getExternalID());
                        ready_to_remove.add(existing_job);
                    }
                }

            );

            //save new jobs
            now_jobs.values().forEach(jobService::save);
            //save gone jobs
            exciting_jobs.removeAll(ready_to_remove);
            exciting_jobs.forEach(jobService::saveVanishedJob);

            //update job detail
            if (now_jobs.size() != 0) {
                log.info("update new coming jobs {}", now_jobs.size());
                List<Job> sortedList = new ArrayList<>();
                now_jobs.values().parallelStream().forEach(job -> sortedList.add(crawler.updateJobDetail(job)));
                //send mail notify now coming jobs

                //sort by date now_jobs.values()
                Collections.sort(sortedList,(a,b)->b.getListDate().compareTo(a.getListDate()));

//                mailSender.sendMail(sortedList);
            } else {
                log.info("No job today...");
            }

        });

    }
}
