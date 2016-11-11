package com.gabriel.web.rest.DTO;

import com.gabriel.web.rest.util.GeoUtil;
import org.json.JSONException;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.SqlResultSetMapping;

/**
 * Created by liuyufei on 5/11/16.
 */
public class GoogleLocation {
    private String location;
    private double lat;
    private double lon;
    private String search_word;
    private long job_count;


    public GoogleLocation(String location, String search_word, long job_count) {
        this.location = location;
        this.search_word = search_word;
        this.job_count = job_count;
        double[] latlon = GeoUtil.getLatLonByAddress(location);
        setLat(latlon[0]);
        setLon(latlon[1]);
    }

    public String getSearch_word() {
        return search_word;
    }

    public void setSearch_word(String search_word) {
        this.search_word = search_word;
    }

    public long getJob_count() {
        return job_count;
    }

    public void setJob_count(long job_count) {
        this.job_count = job_count;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        double[] latlon = GeoUtil.getLatLonByAddress(location);
        setLat(latlon[0]);
        setLon(latlon[1]);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoogleLocation that = (GoogleLocation) o;

        if (Double.compare(that.lat, lat) != 0) return false;
        if (Double.compare(that.lon, lon) != 0) return false;
        if (job_count != that.job_count) return false;
        if (!location.equals(that.location)) return false;
        return search_word.equals(that.search_word);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = location.hashCode();
        temp = Double.doubleToLongBits(lat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + search_word.hashCode();
        result = 31 * result + (int) (job_count ^ (job_count >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "GoogleLocation{" +
            "location='" + location + '\'' +
            ", lat=" + lat +
            ", lon=" + lon +
            ", search_word='" + search_word + '\'' +
            ", job_count=" + job_count +
            '}';
    }
}
