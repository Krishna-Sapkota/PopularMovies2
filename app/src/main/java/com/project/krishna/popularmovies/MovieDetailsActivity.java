package com.project.krishna.popularmovies;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.project.krishna.popularmovies.datamodel.MovieDetails;
import com.project.krishna.popularmovies.utils.NetworkUtility;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity {
    TextView mTitle;
    ImageView mMovieThumbnail;
    TextView mOverview;
    TextView mRelease;
    TextView mRating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mTitle=(TextView)findViewById(R.id.tv_title);
        mMovieThumbnail=(ImageView)findViewById(R.id.iv_movie);
        mRating=(TextView)findViewById(R.id.tv_rating);
        mOverview=(TextView)findViewById(R.id.tv_overview);
        mRelease=(TextView)findViewById(R.id.tv_release);

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
        mRating.setText(movieDetails.getRating()+"/10");

    }
}
