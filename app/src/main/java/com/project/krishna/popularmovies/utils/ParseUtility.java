package com.project.krishna.popularmovies.utils;

import android.graphics.Movie;
import android.net.Uri;
import android.util.Log;

import com.project.krishna.popularmovies.datamodel.MovieDetails;
import com.project.krishna.popularmovies.datamodel.Movies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krishna on 11/13/17.
 */

public class ParseUtility {
    public static final String MOVIE_ID_KEY="id";
    public static final String POSTER_KEY="poster_path";
    public static final String MOVIE_ARRAY="results";


    public static List<Movies> getMoviesList(String movieJSON)  {
        List<Movies> moviesList=new ArrayList<>();
        JSONObject movies=null;
        try {
            movies = new JSONObject(movieJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray results=null;
        try {
            assert movies != null;
            results = movies.getJSONArray(MOVIE_ARRAY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int current=0;
        assert results != null;
        while(current<results.length()){

            JSONObject movieRoot=null;
            try {
                movieRoot = results.getJSONObject(current);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String mid= null;
            try {
                assert movieRoot != null;
                mid = movieRoot.getString(MOVIE_ID_KEY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String relativePosterPath= null;
            try {
                relativePosterPath = movieRoot.getString(POSTER_KEY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Uri.Builder builder=NetworkUtility.getPosterBase();
            builder.appendEncodedPath(relativePosterPath);
            String posterURL=builder.build().toString();
            Movies movie=new Movies();
            movie.setMovieId(mid);
            movie.setPosterURL(posterURL);
            moviesList.add(movie);
            current++;
        }
        return moviesList;
    }

    public static MovieDetails getMovieDetails(String movieDetailsJSON,String movieId) {
        JSONObject movies=null;
        try {
            movies = new JSONObject(movieDetailsJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray results=null;
        try {
            assert movies != null;
            results = movies.getJSONArray(MOVIE_ARRAY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int current=0;
        MovieDetails  movieDetails=null;
        assert results != null;
        while(current<results.length()){

            JSONObject movieRoot=null;
            try {
                movieRoot = results.getJSONObject(current);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String mid= null;
            try {
                assert movieRoot != null;
                mid = movieRoot.getString(MOVIE_ID_KEY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assert mid != null;
            if(mid.equals(movieId)){

                String title=null;
                String overview=null;
                String releaseDate=null;
                String backdrop=null;
                String rating=null;
                try {


                    title = movieRoot.getString("title");
                    overview = movieRoot.getString("overview");
                    releaseDate = movieRoot.getString("release_date");
                    backdrop = movieRoot.getString("backdrop_path");
                    rating=movieRoot.getString("vote_average");
                }
                catch (JSONException e){
                    e.printStackTrace();
                }

                movieDetails=new MovieDetails(title,backdrop,overview,rating,releaseDate);
                break;
            }
            current++;
        }
    return movieDetails;

    }
}
