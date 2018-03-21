package com.example.mohamed.moviesapp.model;

import java.io.Serializable;

public class Movie implements Serializable{
    private int id ;
    private String originalTitle ;
    private String posterPath;
    private String overview;
    private double voteAverage;
    private String releaseDate;


    public Movie(int id,String originalTitle , String posterPath ,String overview ,double voteAverage,String releaseDate){
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview ;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }



    public String getPosterPath() {
        return posterPath;
    }



    public String getOverview() {
        return overview;
    }



    public double getVoteAverage() {
        return voteAverage;
    }



    public String getReleaseDate() {
        return releaseDate;
    }



}
