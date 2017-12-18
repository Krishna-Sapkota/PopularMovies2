package com.project.krishna.popularmovies.datamodel;

/**
 * Created by Krishna on 12/17/17.
 */

public class Trailers {

    String id;
    String movieId;
    String name;
    String youTubeKey;


    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
    public String getMovieId() {
        return movieId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYouTubeKey() {
        return youTubeKey;
    }

    public void setYouTubeKey(String youTubeKey) {
        this.youTubeKey = youTubeKey;
    }






}
