package com.atakishiyev.playstation__brat_;

import java.util.ArrayList;

public class PSClub {
    static class Review {
        private String uname, review;

        Review(String uname, String review) {
            this.uname = uname;
            this.review = review;
        }
    }

    private Double lat, lng, price;
    private String name;
    private ArrayList<Review> reviews;
    private ArrayList<String> consoles;
    private Integer rateSum, rateCnt;


    public PSClub(Double lat, Double lng, Double price, String name, ArrayList<Review> reviews, ArrayList<String> consoles, Integer rateSum, Integer rateCnt) {
        this.lat = lat;
        this.lng = lng;
        this.price = price;
        this.name = name;
        this.reviews = reviews;
        this.consoles = consoles;
        this.rateSum = rateSum;
        this.rateCnt = rateCnt;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public ArrayList<String> getConsoles() {
        return consoles;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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
        return Double.valueOf(this.rateSum/rateCnt);
    }
}
