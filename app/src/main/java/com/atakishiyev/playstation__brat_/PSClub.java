package com.atakishiyev.playstation__brat_;

import java.util.ArrayList;
import java.util.HashMap;

public class PSClub {
    static class revData implements java.io.Serializable {
        Float rate;
        String reviewContent;

        public revData() {
        }

        revData(Float rate, String reviewContent) {
            this.rate = rate;
            this.reviewContent = reviewContent;
        }
    }

    private Double lat, lng;
    private String name;
    private HashMap<String, revData> reviews;
    private HashMap<String, Double> consoles;
    private Integer rateSum, rateCnt;


    PSClub(Double lat, Double lng, String name, HashMap<String, revData> reviews, HashMap<String, Double> consoles, Integer rateSum, Integer rateCnt) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.reviews = reviews;
        this.consoles = consoles;
        this.rateSum = rateSum;
        this.rateCnt = rateCnt;
    }

    public HashMap<String, revData> getReviews() {
        return reviews;
    }

    public HashMap<String, Double> getConsoles() {
        return consoles;
    }

    public Integer getRateSum() {
        return rateSum;
    }

    public void setRateSum(Integer rateSum) {
        this.rateSum = rateSum;
    }

    public Integer getRateCnt() {
        return rateCnt;
    }

    public void setRateCnt(Integer rateCnt) {
        this.rateCnt = rateCnt;
    }

    public PSClub() {

    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double rating() {
        return (this.rateSum + 0.0d)/rateCnt;
    }
}
