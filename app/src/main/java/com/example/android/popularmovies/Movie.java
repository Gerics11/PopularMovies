package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;


public class Movie implements Parcelable {

    private String title;
    private String movieId;
    private String releaseDate;
    private String image;
    private double rating;
    private String plot;

    Movie(String title, String movieId, String releaseDate, String image, double rating, String plot) {
        this.title = title;
        this.movieId = movieId;
        this.releaseDate = releaseDate;
        this.image = image;
        this.rating = rating;
        this.plot = plot;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getImage() {


        if (image != null) {
            return "http://image.tmdb.org/t/p/w342/" + image;
        } else {
            return null;
        }
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    //parcelable implementation
    private Movie(Parcel in) {
        String[] data = new String[6];

        in.readStringArray(data);
        this.title = data[0];
        this.movieId = data[1];
        this.releaseDate = data[2];
        this.image = data[3];
        this.rating = Double.parseDouble(data[4]);
        this.plot = data[5];
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.title,
                this.movieId,
                this.releaseDate,
                this.image,
                String.valueOf(this.rating),
                this.plot});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
