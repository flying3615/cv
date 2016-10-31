package com.gabriel.service.crawler;

import com.gabriel.domain.Job;
import com.gabriel.repository.JobRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Created by liuyufei on 31/10/16.
 */


@Component
public class SeekCrawler implements Crawler {

    private final Logger log = LoggerFactory.getLogger(SeekCrawler.class);

    @Value("${crawler.userAgent}")
    String userAgent;

    @Value("${crawler.seek.requestUrl}")
    String requestUrl;

    @Value("${crawler.seek.callback}")
    String callback;

    @Value("${crawler.seek.nation}")
    String nation;

    @Value("${crawler.seek.itemsPerPage}")
    String itemsPerPage;

    @Value("${crawler.seek.fromSite}")
    String from_site;

    @Value("${crawler.seek.detailUrl}")
    String detail_url;

    @Inject
    JobRepository jobRepository;

//
//    String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36";
//
//    String requestUrl = "https://jobsearch-api.cloud.seek.com.au/search";
//
//    String callback = "jQuery18203420653892844612_1477784271109";
//
//    String nation = "3001";
//
//    Integer itemsPerPage = 20;

    private String searchWord;


    @Override
    public Map<String,Job> listJobs(String searchWord) {
        this.searchWord = searchWord;
        Map<String,Job> all_jobs = new HashMap<>();
        try {
            int total_pages = this.getTotalPage(searchWord);
            //use parallel to process each page
            IntStream.rangeClosed(1, total_pages).parallel().forEach(i -> {
                try {
                    all_jobs.putAll(this.listJobsByPage(searchWord, i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

        return all_jobs;
    }

    private Map<String,Job> listJobsByPage(String searchWord, int pageNum) throws Exception {
        String job_raw = this.getRawResponse(searchWord, pageNum);
        return this.parseTextToJson(job_raw);
    }

    private int getTotalPage(String searchWord) throws Exception {
        String raw = this.getRawResponse(searchWord, 1);
        String tmp = raw.split(callback + "\\(")[1];
        String rawJSON = tmp.substring(0, tmp.length() - 2);
        JSONObject jsonObject = new JSONObject(rawJSON);

        int total_items = Integer.parseInt(jsonObject.getString("totalCount"));
        int total_page;
        int itemsPerPage_int = Integer.parseInt(itemsPerPage);
        if (total_items % itemsPerPage_int == 0) {
            total_page = total_items / itemsPerPage_int;
        } else {
            total_page = total_items / itemsPerPage_int + 1;
        }
        return total_page;
    }


    private String getRawResponse(String searchWord, int pageNum) throws Exception {

        Connection.Response response = null;
        try {
            response = Jsoup.connect(requestUrl)
                .header("Accept", "*/*")
                .header("Content-Type", "application/json;charset=UTF-8")
                .data("callback", callback)
                .data("keywords", searchWord)
                .data("nation", nation)
                .data("page", String.valueOf(pageNum))
                .userAgent(userAgent)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

            if (response.statusCode() == 200) {
                return response.parse().body().ownText();
            } else {
                //TODO throw exception NETWORKERROR
                throw new Exception("remote response code is NOT 200");
            }
        } catch (Exception e) {
            throw e;
        }

    }


    private Map<String,Job> parseTextToJson(String rawStr) throws JSONException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String tmp = rawStr.split(callback + "\\(")[1];
        String rawJSON = tmp.substring(0, tmp.length() - 2);
        JSONObject jsonObject = new JSONObject(rawJSON);


        JSONArray jobs = jsonObject.getJSONArray("data");
        Map<String,Job> jobList = new HashMap<>();
        for (int i = 0; i < jobs.length(); i++) {
            JSONObject job = (JSONObject) jobs.get(i);
            String external_id = job.get("id").toString();
            Job job_domain = new Job();
            job_domain.setWorkType(job.getString("workType"));
            job_domain.setExternalID(external_id);
            job_domain.setTitle(job.get("title").toString());
            job_domain.setCompany(job.getJSONObject("advertiser").getString("description"));
            job_domain.setSalary(job.getString("salary"));
            job_domain.setLocation(job.getString("location"));
            //"2016-10-17T05:10:54Z"
            job_domain.setListDate(LocalDate.parse(job.getString("listingDate"), formatter));
            job_domain.setFromSite(from_site);
            job_domain.setSearchWord(searchWord);
            job_domain.setCreationTime(ZonedDateTime.now());
            job_domain.setOrigURL(detail_url+external_id);
            jobList.put(external_id,job_domain);
        }
        //return standard job json array format
        return jobList;

    }

    public static void main(String[] args) {
//        SeekCrawler seekCrawler = new SeekCrawler();
//        seekCrawler.detail_url = "https://www.seek.co.nz/job/";
//        seekCrawler.userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36";
//        seekCrawler.jobDetail("32149854");
    }


    @Override
    public void updateJobDetail(Job job) {
        Connection.Response response;
        try {
            response = Jsoup.connect(detail_url + job.getExternalID())
                .header("Accept", "*/*")
                .header("Content-Type", "application/json;charset=UTF-8")
                .userAgent(userAgent)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

            if (response.statusCode() == 200) {
                Document document = response.parse();
                Elements divs = document.getElementsByClass("templatetext");
                Element div_description;
                if ((div_description = divs.get(0)) != null) {
                    String email = "";
                    String description = div_description.text();
                    Matcher emailMatcher = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(description);
                    while (emailMatcher.find()) {
                        //email
                        email += emailMatcher.group() + " ";
                    }
                    job.setDescription(description);
                    job.setContact(email);
                    jobRepository.save(job);
                }else{
                    log.error("Job detail not found in <div class=templatetext>");
                }

            } else {
                //TODO throw exception NETWORKERROR
//                throw new Exception("remote response code is NOT 200");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
