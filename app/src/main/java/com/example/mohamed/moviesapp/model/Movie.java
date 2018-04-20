package com.example.mohamed.moviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;


public class Movie implements Parcelable{
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

    private Movie(Parcel in) {
        id = in.readInt();
        originalTitle = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

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


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(originalTitle);
            dest.writeString(posterPath);
            dest.writeString(overview);
            dest.writeDouble(voteAverage);
            dest.writeString(releaseDate);
        }
    }
