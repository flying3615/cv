package com.gabriel.web.rest.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Created by liuyufei on 4/11/16.
 */
public class GeoUtil {

    private static final Logger log = LoggerFactory.getLogger(GeoUtil.class);
    Map<String, Optional<GoogleLocation>> googleLocationHashMap = new HashMap<>();


    //    https://maps.googleapis.com/maps/api/geocode/json?address=Auckland

    public Optional<GoogleLocation> getLatLonByAddress(String address) {

        if (googleLocationHashMap.containsKey(address)) {
            log.debug("{} return cache", address);
            return googleLocationHashMap.get(address);
        } else {
            log.debug("{} request Google map API", address);
            GoogleLocation googleLocation = null;
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject("https://maps.googleapis.com/maps/api/geocode/json?address=" + address, String.class);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray results = jsonObject.getJSONArray("results");
                JSONObject single_result = results.getJSONObject(0);
                if (single_result != null) {
                    JSONObject location = single_result.getJSONObject("geometry").getJSONObject("location");
                    googleLocation = new GoogleLocation(address, location.getDouble("lat"), location.getDouble("lng"));
                    googleLocationHashMap.put(address,Optional.of(googleLocation));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return Optional.ofNullable(googleLocation);
        }


    }

    public static void main(String[] args) {
        GeoUtil geoUtil = new GeoUtil();
        geoUtil.getLatLonByAddress("Auckland").ifPresent(System.out::println);
        geoUtil.getLatLonByAddress("Wellington").ifPresent(System.out::println);
        geoUtil.getLatLonByAddress("Auckland").ifPresent(System.out::println);

    }


}


class GoogleLocation {
    private String addName;
    private double lat;
    private double lon;

    public GoogleLocation(String addName, double lat, double lon) {
        this.addName = addName;
        this.lat = lat;
        this.lon = lon;
    }

    public String getAddName() {
        return addName;
    }

    public void setAddName(String addName) {
        this.addName = addName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "GoogleLocation{" +
            "addName='" + addName + '\'' +
            ", lat=" + lat +
            ", lon=" + lon +
            '}';
    }
}
