package com.project.krishna.popularmovies.datamodel;

/**
 * Created by Krishna on 11/13/17.
 */

public class Movies {
    private String movieId;
    private String posterURL;



    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}
