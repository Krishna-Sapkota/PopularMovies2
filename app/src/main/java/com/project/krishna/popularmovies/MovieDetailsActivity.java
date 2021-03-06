package com.project.krishna.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.project.krishna.popularmovies.datamodel.FavouriteContract;
import com.project.krishna.popularmovies.datamodel.MovieDetails;
import com.project.krishna.popularmovies.datamodel.Review;
import com.project.krishna.popularmovies.datamodel.Trailers;
import com.project.krishna.popularmovies.utils.NetworkUtility;
import com.project.krishna.popularmovies.utils.ParseUtility;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {
    private static final String VIDEO_PATH ="videos" ;
    private static final int TRAILER_LOADER =20;
    private static final int REVIEW_LOADER = 21;
    private static final String REVIEW_PATH ="reviews" ;
    private static final String REVIEW_LIST_STATE ="reviewstate" ;
    private static final String DETAILS_SCROLL_POSITION ="details_scroll" ;
    private static final String TRAILER_SCROLL ="trailer_scroll" ;
    private String movieId;
    private TextView mTitle;
    private ImageView mMovieThumbnail;
    private TextView mOverview;
    private TextView mRelease;
    private TextView mRating;
    private String mRatingFull;
    private RecyclerView recyclerViewReview;
    private ScrollView mScrollView;
    boolean isFavouriteEnable=false;
    TextView mLoadingTrailers;
    Parcelable reviewListState;
    Parcelable trailerListState;


    ListView trailerList;
    Trailers[] trailers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mRatingFull=getResources().getString(R.string.full_rating);
        mTitle=findViewById(R.id.tv_title);
        mMovieThumbnail=findViewById(R.id.iv_movie);
        mRating=findViewById(R.id.tv_rating);
        mOverview=findViewById(R.id.tv_overview);
        mRelease=findViewById(R.id.tv_release);
        trailerList=findViewById(R.id.lv_trailer_list);
        recyclerViewReview=findViewById(R.id.rv_reviews);
        mLoadingTrailers=findViewById(R.id.tv_loading_trailer_message);
        mScrollView=findViewById(R.id.sv_md);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewReview.setLayoutManager(layoutManager);
        final MovieDetails movieDetails=getIntent().getExtras().getParcelable(MainActivity.getParceableKey());
        movieId=movieDetails.getId();
        Uri.Builder builder= NetworkUtility.getPosterBase();

        builder.appendEncodedPath(movieDetails.getThumbnailURL());
        builder.appendQueryParameter(NetworkUtility.getParamApiKey(),NetworkUtility.getApiKey());
        Uri thumbnailUri=builder.build();
        URL url=null;
        try {
              url = new URL(thumbnailUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Glide.with(this)
                .load(url.toString())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .into(mMovieThumbnail);

        mTitle.setText(movieDetails.getTitle());
        mOverview.setText(movieDetails.getOverview());
        mRelease.setText(movieDetails.getReleaseDate());
        String rating=movieDetails.getRating();
        rating.concat(mRatingFull);
        mRating.setText(rating);
        final ImageButton favouriteButton = (ImageButton) findViewById(R.id.favourite_button);
        Cursor cursor=getContentResolver().query(FavouriteContract.FavouriteEntry.CONTENT_URI.
                        buildUpon().appendPath(movieDetails.getId()).build(),
                null,
                null,
                null,
                null
        );
        if(cursor.getCount()!=0){
            setFavouriteSelected(favouriteButton);
        }
        else {
            setFavouriteDisabled(favouriteButton);
        }
        cursor.close();

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavouriteEnable){
                    setFavouriteDisabled(favouriteButton);
                    String selection= FavouriteContract.FavouriteEntry.COLUMN_MOVIE_ID;
                    String[] args={movieDetails.getId()};
                    getContentResolver().delete(FavouriteContract.FavouriteEntry.CONTENT_URI.
                                    buildUpon().appendPath(movieDetails.getId()).build(),
                                    null,null);
                    Toast.makeText(MovieDetailsActivity.this,"Removed from favourite collection",Toast.LENGTH_SHORT).show();
                    isFavouriteEnable=false;
                }else{
                    setFavouriteSelected(favouriteButton);
                    ContentValues contentValues=new ContentValues();
                    contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_MOVIE_ID,movieDetails.getId());
                    contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_MOVIE_TITLE,movieDetails.getTitle());
                    contentValues.put(FavouriteContract.FavouriteEntry.MOVIE_THUMBNAIL_URL,movieDetails.getPosterURL());
                    Log.i("TESTID",movieDetails.getId());
                    getContentResolver().insert(FavouriteContract.FavouriteEntry.CONTENT_URI,contentValues);
                    Toast.makeText(MovieDetailsActivity.this,"Added to favourite collection",Toast.LENGTH_SHORT).show();
                    isFavouriteEnable=true;

                }
            }
        });
        getSupportLoaderManager().initLoader(TRAILER_LOADER,null,new TrailerLoader());
        getSupportLoaderManager().initLoader(REVIEW_LOADER,null,new ReviewLoader());
        if(savedInstanceState!=null){
            reviewListState=savedInstanceState.getParcelable(REVIEW_LIST_STATE);
            trailerListState=savedInstanceState.getParcelable(TRAILER_SCROLL);
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    recyclerViewReview.getLayoutManager().onRestoreInstanceState(reviewListState);
                }
            },300);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    trailerList.onRestoreInstanceState(trailerListState);
                }
            },300);

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        if(item.getItemId()==R.id.settings){
            Intent settingsActivity=new Intent(this,PreferenceActivity.class);
            startActivity(settingsActivity);
        }
        if(item.getItemId()==R.id.share_action){
            shareTrailer();
        }
        return true;
    }

    private void shareTrailer() {

            String mimeType = "text/plain";
            String title = "Share the trailer";
            if(trailers!=null) {
                String URL = "http://www.youtube.com/watch?v=" + trailers[0].getYouTubeKey();
                Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                        .setChooserTitle(title)
                        .setType(mimeType)
                        .setText(URL)
                        .getIntent();
                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(shareIntent, title));
                }
            }
            else {
                Toast.makeText(this,"Trailers not loaded",Toast.LENGTH_SHORT).show();
            }

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TRAILER_SCROLL,trailerList.onSaveInstanceState());
        outState.putParcelable(REVIEW_LIST_STATE, recyclerViewReview.getLayoutManager().onSaveInstanceState());
        outState.putIntArray(DETAILS_SCROLL_POSITION,
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray(DETAILS_SCROLL_POSITION);
        if(position != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_overflow,menu);
        getMenuInflater().inflate(R.menu.share_button,menu);
        return super.onCreateOptionsMenu(menu);
    }
    private URL buildURL(String path){
        Uri.Builder builder=NetworkUtility.getBaseURI();
        builder.appendPath(movieId);
        builder.appendPath(path);
        builder.appendQueryParameter(NetworkUtility.getParamApiKey(),NetworkUtility.getApiKey());
        Uri trailerUri=builder.build();
        try {
            return new URL(trailerUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void setFavouriteSelected(ImageButton favouriteButton){
        favouriteButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_on));
        isFavouriteEnable=true;
    }
    private void setFavouriteDisabled(ImageButton favouriteButton){
        favouriteButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_off));
        isFavouriteEnable=false;
    }





     private class TrailerLoader implements LoaderManager.LoaderCallbacks<Trailers[]> {

         @Override
         public Loader<Trailers[]> onCreateLoader(int id, Bundle args) {

             switch (id) {
                 case TRAILER_LOADER:
                     return new AsyncTaskLoader<Trailers[]>(MovieDetailsActivity.this) {
                         Trailers[] trailers;

                         @Override
                         protected void onStartLoading() {
                             super.onStartLoading();
                             if (trailers != null) {
                                 deliverResult(trailers);
                             } else {
                                 mLoadingTrailers.setText(getString(R.string.trailer_loading_message));
                                 mLoadingTrailers.setVisibility(View.VISIBLE);
                                 forceLoad();
                             }
                         }

                         @Override
                         public Trailers[] loadInBackground() {

                             URL videosURL = buildURL(VIDEO_PATH);
                             String trailerJSON = null;

                             try {
                                 trailerJSON = NetworkUtility.getRespsonseFromHttpUrl(videosURL);
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }
                             List<Trailers> trailersList = ParseUtility.getTrailers(trailerJSON);
                             Trailers[] trailers = new Trailers[trailersList.size()];
                             trailers = trailersList.toArray(trailers);
                             return trailers;
                         }

                         @Override
                         public void deliverResult(Trailers[] data) {
                             trailers = data;
                             super.deliverResult(data);
                         }
                     };
                     default:
                         return null;


             }


         }

         @Override
         public void onLoadFinished(Loader<Trailers[]> loader, final Trailers[] data) {
             trailers=data;
             mLoadingTrailers.setVisibility(View.GONE);
             String[] trailerNames = new String[data.length];
             int[] playIcons = new int[data.length];
             String[] ids=new String[data.length];
             for (int i = 0; i < data.length; i++) {
                 ids[i]=data[i].getId();
                 trailerNames[i] = data[i].getName();
                 playIcons[i] = R.drawable.ic_play_circle_filled_black_24dp;
             }
             TrailerAdapter adapter = new TrailerAdapter(MovieDetailsActivity.this, ids,trailerNames, playIcons);
             trailerList.setAdapter(adapter);
             trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String trailerId=(String)adapterView.getItemAtPosition(i);
                    String key=getTrailerURLKey(trailerId,data);
                    if(key!=null)
                    watchYoutubeVideo(MovieDetailsActivity.this,key);

                 }
             });

         }

         private String getTrailerURLKey(String trailerId, Trailers[] data) {
             for(Trailers trailer:data){
                 if(trailer.getId().equals(trailerId)){
                     return  trailer.getYouTubeKey();
                 }
             }
             return null;
         }

         @Override
         public void onLoaderReset(Loader<Trailers[]> loader) {


         }

     }

     private class ReviewLoader implements LoaderManager.LoaderCallbacks<Review[]>{


         @Override
         public Loader<Review[]> onCreateLoader(int id, Bundle args) {
             return new AsyncTaskLoader<Review[]>(MovieDetailsActivity.this) {
                 Review[] reviews;
                 @Override
                 protected void onStartLoading() {
                     super.onStartLoading();

                     if(reviews!=null){
                         deliverResult(reviews);
                     }
                     else {
                         forceLoad();
                     }

                 }

                 @Override
                 public Review[] loadInBackground() {
                     URL reviewsURL=buildURL(REVIEW_PATH);
                     String reviewsJSON=null;

                     try {
                         reviewsJSON= NetworkUtility.getRespsonseFromHttpUrl(reviewsURL);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                     List<Review> reviewList= ParseUtility.getReviews(reviewsJSON);
                     Review[] reviews=new Review[reviewList.size()];
                     reviews=reviewList.toArray(reviews);
                     return reviews;
                 }

                 @Override
                 public void deliverResult(Review[] data) {
                     reviews=data;
                     super.deliverResult(data);
                 }
             };
         }

         @Override
         public void onLoadFinished(Loader<Review[]> loader, Review[] data) {
             ReviewAdapter reviewAdapter=new ReviewAdapter(MovieDetailsActivity.this,data);
             recyclerViewReview.setAdapter(reviewAdapter);

         }

         @Override
         public void onLoaderReset(Loader<Review[]> loader) {

         }
     }
    public  void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }
}
