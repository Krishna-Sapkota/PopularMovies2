package com.project.krishna.popularmovies;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.project.krishna.popularmovies.datamodel.MovieDetails;
import com.project.krishna.popularmovies.utils.NetworkUtility;


import java.net.MalformedURLException;
import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity {
    private TextView mTitle;
    private ImageView mMovieThumbnail;
    private TextView mOverview;
    private TextView mRelease;
    private TextView mRating;
    private String mRatingFull;
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

        MovieDetails movieDetails=getIntent().getExtras().getParcelable("movie");
        Uri.Builder builder= NetworkUtility.getPosterBase();
        builder.appendEncodedPath(movieDetails.getThumbnailURL());
        builder.appendQueryParameter(NetworkUtility.PARAM_API_KEY,NetworkUtility.API_KEY);
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
         //.apply(RequestOptions.bitmapTransform(new CircleCrop()))

        mTitle.setText(movieDetails.getTitle());
        mOverview.setText(movieDetails.getOverview());
        mRelease.setText(movieDetails.getReleaseDate());
        String rating=new String(movieDetails.getRating());
        rating.concat(mRatingFull);
        mRating.setText(rating.toString());

    }
}
