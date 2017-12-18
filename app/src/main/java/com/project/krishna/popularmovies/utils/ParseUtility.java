package com.project.krishna.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import com.project.krishna.popularmovies.datamodel.MovieDetails;
import com.project.krishna.popularmovies.datamodel.Movies;
import com.project.krishna.popularmovies.datamodel.Review;
import com.project.krishna.popularmovies.datamodel.Trailers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krishna on 11/13/17.
 */

public class ParseUtility {
    private static final String MOVIE_ID_KEY="id";
    private static final String POSTER_KEY="poster_path";
    private static final String MOVIE_ARRAY="results";
    private static final String MOVIE_TITLE_KEY="original_title";
    private static final String MOVIE_OVERVIEW_KEY="overview";
    private static final String MOVIE_RELEASE_KEY="release_date";
    private static final String MOVIE_BACKDROP_KEY="backdrop_path";
    private static final String MOVIE_RATING_KEY="vote_average";
    private static final String TRAILER_ID_KEY ="id" ;
    private static final String TRAILER_NAME_KEY ="name";
    private static final String TRAILER_YOUTUBE_KEY ="key" ;
    private static final String REVIEW_ID_KEY ="id" ;
    private static final String REVIEW_AUTHOR_KEY ="author" ;
    private static final String REVIEW_TEXT_KEY ="content";
    private static final String REVIEW_URL_KEY ="url" ;

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
            Log.i("TEST",movieId);

            if(mid.equals(movieId)){
                String id=null;
                String title=null;
                String overview=null;
                String releaseDate=null;
                String backdrop=null;
                String poster=null;
                String rating=null;

                try {
                    id=movieRoot.getString(MOVIE_ID_KEY);
                    title = movieRoot.getString(MOVIE_TITLE_KEY);
                    overview = movieRoot.getString(MOVIE_OVERVIEW_KEY);
                    releaseDate = movieRoot.getString(MOVIE_RELEASE_KEY);
                    backdrop = movieRoot.getString(MOVIE_BACKDROP_KEY);
                    poster=movieRoot.getString(POSTER_KEY);
                    rating=movieRoot.getString(MOVIE_RATING_KEY);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }

                movieDetails=new MovieDetails(id,title,backdrop,poster,overview,rating,releaseDate);
                break;
            }
            current++;
        }
    return movieDetails;

    }

    public static List<Trailers> getTrailers(String trailerJSON) {
        JSONObject trailers=null;
        try {
            trailers = new JSONObject(trailerJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray results=null;
        String moid=null;
        try {
             moid=trailers.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            results = trailers.getJSONArray(MOVIE_ARRAY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int current=0;
        List<Trailers> trailersList=new ArrayList<>();

        while (current<results.length()){
            Trailers trailers1=new Trailers();
            JSONObject trailerObject=null;
            try {
                 trailerObject=results.getJSONObject(current);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            trailers1.setMovieId(moid);
            try {
                trailers1.setId(trailerObject.getString(TRAILER_ID_KEY));
                trailers1.setName(trailerObject.getString(TRAILER_NAME_KEY));
                trailers1.setYouTubeKey(trailerObject.getString(TRAILER_YOUTUBE_KEY));

            }
            catch (JSONException e){


            }

            trailersList.add(trailers1);
            current++;
        }

        return  trailersList;

    }

    public static List<Review> getReviews(String reviewsJSON) {
        JSONObject trailers=null;
        try {
            trailers = new JSONObject(reviewsJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray results=null;
        String moid=null;
        try {
            moid=trailers.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            results = trailers.getJSONArray(MOVIE_ARRAY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int current=0;
        List<Review> reviewList=new ArrayList<>();
        while (current<results.length()){
            Review review=new Review();
            JSONObject reviewObject=null;
            try {
                reviewObject=results.getJSONObject(current);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            review.setMovieId(moid);
            try {
                review.setId(reviewObject.getString(REVIEW_ID_KEY));
                review.setAuthor(reviewObject.getString(REVIEW_AUTHOR_KEY));
                review.setReviewText(reviewObject.getString(REVIEW_TEXT_KEY));
                review.setUrl(reviewObject.getString(REVIEW_URL_KEY));

            }
            catch (JSONException e){

            }
            reviewList.add(review);
            current++;
        }
    return reviewList;

    }


    public static MovieDetails getFavMovieDeatails(String movieJSON) throws JSONException {
        JSONObject movie=null;
        MovieDetails movieDetails=null;

        movie=new JSONObject(movieJSON);

        String id=movie.getString(MOVIE_ID_KEY);
        String title = movie.getString(MOVIE_TITLE_KEY);
        String overview = movie.getString(MOVIE_OVERVIEW_KEY);
        String releaseDate = movie.getString(MOVIE_RELEASE_KEY);
        String backdrop = movie.getString(MOVIE_BACKDROP_KEY);
        String poster=movie.getString(POSTER_KEY);
        String rating=movie.getString(MOVIE_RATING_KEY);
        movieDetails=new MovieDetails(id,title,backdrop,poster,overview,rating,releaseDate);
        return movieDetails;

    }
}
