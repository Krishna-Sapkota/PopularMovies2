package com.project.krishna.popularmovies.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Krishna on 11/13/17.
 */

public class MovieDetails implements Parcelable{

    private String id;
    private String title;
    private String thumbnailURL;
    private String overview;
    private String rating;
    private String releaseDate;

    public MovieDetails(String id,String title, String thumbnailURL, String overview, String rating, String releaseDate) {
        this.id=id;
        this.title = title;
        this.thumbnailURL = thumbnailURL;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }
    public String getId(){return id;}
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    private   MovieDetails(Parcel in){
        this.id=in.readString();
        this.title=in.readString();
        this.thumbnailURL=in.readString();
        this.overview=in.readString();
        this.rating=in.readString();
        this.releaseDate=in.readString();

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(thumbnailURL);
        parcel.writeString(overview);
        parcel.writeString(rating);
        parcel.writeString(releaseDate);
    }

    public static final Parcelable.Creator<MovieDetails> CREATOR = new Parcelable.Creator<MovieDetails>() {
        @Override
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };
}



