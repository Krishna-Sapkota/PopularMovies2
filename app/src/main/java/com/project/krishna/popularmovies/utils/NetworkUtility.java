package com.project.krishna.popularmovies.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Krishna on 11/13/17.
 */

public class NetworkUtility {
    public static final String MOVIE_DB_SCHEME="https";

    public static final String MOVIE_DB_BASE_URL="api.themoviedb.org";
    public static final String MOVIE_DB_BASE_PATH="3/movie";
    public static final String API_KEY="e258b44703031d92257a4a7eb9907108";

    public static final String PARAM_API_KEY="api_key";

    public static final String POSTER_BASE_URL="image.tmdb.org";
    public static final String POSTER_BASE_PATH="/t/p/w185/";



    public static String getRespsonseFromHttpUrl(URL url)throws IOException{
        HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
        InputStream inputStream=null;

        try {
            inputStream=urlConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner scanner=new Scanner(inputStream);
        scanner.useDelimiter("\\A");
        boolean hasInput=scanner.hasNext();
        if(hasInput){
            return scanner.next();
        }
        else {
            return null;
        }
    }

    public static URL buildURL(String sortPath) throws MalformedURLException{

        Uri.Builder builder=new Uri.Builder();

        builder.scheme(MOVIE_DB_SCHEME);
        builder.authority(MOVIE_DB_BASE_URL);
        builder.appendEncodedPath(MOVIE_DB_BASE_PATH);
        builder.appendPath(sortPath);
        builder.appendQueryParameter(PARAM_API_KEY,API_KEY);
        Uri buildUri=builder.build();
        return new URL(buildUri.toString());

    }

    public static Uri.Builder getPosterBase(){
        Uri.Builder builder=new Uri.Builder();
        builder.scheme(MOVIE_DB_SCHEME);
        builder.authority(POSTER_BASE_URL);
        builder.appendEncodedPath(POSTER_BASE_PATH);
        return builder;
    }

}
