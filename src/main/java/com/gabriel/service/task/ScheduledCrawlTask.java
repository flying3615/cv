package com.gabriel.service.task;

import com.gabriel.domain.Job;
import com.gabriel.service.JobService;
import com.gabriel.service.MailService;
import com.gabriel.service.crawler.Crawler;
import com.gabriel.service.util.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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

//    @Inject
//    MailSender mailSender;

    @Inject
    MailService mailService;

    @Inject
    JobService jobService;

    @Scheduled(cron = "0 0 6 * * *")  //@ 6:00:00 am every day
//    @Scheduled(cron = "0 */5 * * * *") //every ten minutes for test
    public void dailyCrawl() {

        this.cleanCache();

        List<String> keywords = new ArrayList<>();

        keywords.add("Java");
        keywords.add(".Net");
        keywords.add("Python");
        keywords.add("Ruby");
        keywords.add("JavaScript");
        keywords.add("PHP");

        keywords.forEach(this::crawlByWord);

    }


    @Scheduled(cron = "0 0 10 * * *")  //@ 10:00:00 am every day
    public void GarbageCollector() {
        //TODO clean Database invalid job records
    }

    private void cleanCache() {
        //TODO clean guava cache...
    }


//    @Async
    public void crawlByWord(String searchKeyword) {
        log.info("crawl task start @ {}", LocalDateTime.now());
        Set<Map.Entry<String, Crawler>> crawlerSet = crawlerStrategy.entrySet();
        crawlerSet.stream().forEach(crawlerEntry -> {

            log.info("{} for {} ready to GO!!!", crawlerEntry.getKey(), searchKeyword);

            Crawler crawler = crawlerEntry.getValue();

            //check if job exist...
            //TODO should escape searchKey here
            Set<Job> exciting_jobs = jobService.findBySearchWordAndFromSite(searchKeyword, crawler.getFromSite());

            Map<String, Job> today_jobs = crawler.listJobs(searchKeyword);

            log.info("existing jobs size {} for search word {}", exciting_jobs.size(), searchKeyword);

            Set<Job> ready_to_remove = new HashSet<>();
            //only care about the latest jobs
            exciting_jobs.forEach(existing_job -> {
                    if (today_jobs.containsKey(existing_job.getExternalID())) {
                        today_jobs.remove(existing_job.getExternalID());
                        log.debug("duplicate jobs external id={}", existing_job.getExternalID());
                        ready_to_remove.add(existing_job);
                    }
                }

            );

            //save new jobs
            today_jobs.values().forEach(today_job -> {
                    jobService.save(today_job);
                    jobService.saveJobLog(today_job);
                }
            );

            //update job detail
            if (today_jobs.size() != 0) {
                log.info("update new coming jobs {}", today_jobs.size());
                today_jobs.values().parallelStream().forEach(job -> {
                    Job updated_job = crawler.updateJobDetail(job);
                    jobService.save(updated_job);
                });
                //send mail notify now coming jobs
//                mailService.sendNewJobMail(today_jobs.values());
            } else {
                log.info("No job today...");
            }

            //save gone jobs, trigger many times!!!!
            exciting_jobs.removeAll(ready_to_remove);
            exciting_jobs.forEach(jobService::saveVanishedJob);

        });
    }
}
