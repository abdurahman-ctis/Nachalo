package com.atakishiyev.playstation__brat_;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PSClub {

    private LatLng location;
    private String name, description;
    private ArrayList<String> reviews;
    private Double rating;
    private Integer ratecnt;

    public PSClub(LatLng location, String name, String description) {
        this.location = location;
        this.name = name;
        this.description = description;
        ratecnt = 0;
        reviews = new ArrayList<>();
    }

    public ArrayList<String> getReviews() {
        return reviews;
    }

    public void addReview(String review) {
        this.reviews.add(review);
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = (this.rating+rating)/++ratecnt;
    }
}
