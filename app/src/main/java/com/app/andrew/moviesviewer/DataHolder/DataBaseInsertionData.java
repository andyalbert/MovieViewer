package com.app.andrew.moviesviewer.DataHolder;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by Andrew on 11/2/2016.
 */

public class DataBaseInsertionData {
    private Movie movie;
    private ArrayList<Trailer> trailers;
    private ArrayList<Review> reviews;
    private boolean add;
    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }
}
