package com.gabriel.service.task;

import com.gabriel.domain.Job;
import com.gabriel.repository.JobRepository;
import com.gabriel.service.crawler.Crawler;
import com.gabriel.service.util.MailSender;
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

    @Inject
    MailSender mailSender;

    @Value("${crawler.seek.fromSite}")
    String from_site;

    @Inject
    JobRepository jobRepository;

//    @Scheduled(cron = "0 0 6 * * *")  //@ 6:00:00 am every day
    @Scheduled(cron = "0 */5 * * * *") //every ten minutes for test
    public void dailyCrawl() {
        log.info("crawl task start @ {}", LocalDateTime.now());
        Set<Map.Entry<String, Crawler>> crawlerSet = crawlerStrategy.entrySet();
        crawlerSet.stream().forEach(crawlerEntry -> {

            log.info("{} ready to GO!!!", crawlerEntry.getKey());

            Crawler crawler = crawlerEntry.getValue();

            //check if job exist...
            Set<Job> exciting_jobs = jobRepository.findBySearchWordAndFromSite("java", from_site);
            Map<String,Job> now_jobs = crawler.listJobs("java");


            log.info("existing jobs {}, {}",exciting_jobs.size(),exciting_jobs);

            //only care about the latest jobs
            exciting_jobs.forEach(existing_job -> {
                    if (now_jobs.containsKey(existing_job.getExternalID())) {
                        now_jobs.remove(existing_job.getExternalID());
                    }
                }

            );


            jobRepository.save(now_jobs.values());

            //update job detail
            if(now_jobs.size()!=0){
                log.info("update new coming jobs {}, {}",now_jobs.size(),now_jobs);
                now_jobs.values().parallelStream().forEach(rest_job->{
                    crawler.updateJobDetail(rest_job);
                });
                //send mail notify now coming jobs
                mailSender.sendMail(now_jobs.values());
            }else{
                log.info("No job today...");
            }

        });

    }
}
