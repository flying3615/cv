package com.gabriel.web.rest.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by liuyufei on 4/11/16.
 */
public class GeoUtil {

    private static final Logger log = LoggerFactory.getLogger(GeoUtil.class);
    static Map<String, double []> googleLocationHashMap = new HashMap<>();


    //    https://maps.googleapis.com/maps/api/geocode/json?address=Auckland

    public static double[] getLatLonByAddress(String location) {

        double[] lanLong = new double[2];

        //in case of Hamilton in other countries
        location+=" New Zealand";

        if (googleLocationHashMap.containsKey(location)) {
            log.debug("{} return cache", location);
            return googleLocationHashMap.get(location);
        } else {
            log.debug("{} request Google map API", location);
            RestTemplate restTemplate = new RestTemplate();
            try {
                String result = restTemplate.getForObject("https://maps.googleapis.com/maps/api/geocode/json?address=" + location, String.class);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray results = jsonObject.getJSONArray("results");
                JSONObject single_result = results.getJSONObject(0);
                if (single_result != null) {
                    JSONObject locationJSONObj = single_result.getJSONObject("geometry").getJSONObject("location");
                    lanLong[0] = locationJSONObj.getDouble("lat");
                    lanLong[1] = locationJSONObj.getDouble("lng");
                    googleLocationHashMap.put(location,lanLong);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (HttpClientErrorException e) {
                log.error("Cannot to access google map API");
            }
            return lanLong;
        }

    }

    public static void main(String[] args) {
        GeoUtil geoUtil = new GeoUtil();
        double[] auckland = geoUtil.getLatLonByAddress("Auckland");
        for(double l:auckland){
            System.out.println(l);
        }

        double[] aucklandC = geoUtil.getLatLonByAddress("Auckland Central");
        for(double l:aucklandC){
            System.out.println(l);
        }

        double[] wellingtons = geoUtil.getLatLonByAddress("Wellington");
        for(double l:wellingtons){
            System.out.println(l);
        }

        double[] wellingtonsC = geoUtil.getLatLonByAddress("Wellington Central");
        for(double l:wellingtonsC){
            System.out.println(l);
        }

        auckland = geoUtil.getLatLonByAddress("Auckland");
        for(double l:auckland){
            System.out.println(l);
        }

    }


}



