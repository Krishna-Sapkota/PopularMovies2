package com.project.krishna.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.project.krishna.popularmovies.datamodel.Movies;
import com.project.krishna.popularmovies.datamodel.MoviesAdapter;
import com.project.krishna.popularmovies.utils.NetworkUtility;
import com.project.krishna.popularmovies.utils.ParseUtility;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    TextView error;
    RecyclerView mMoviesListRecycler;
    List<Movies> moviesList;
    boolean sortedByPopular=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMoviesListRecycler=(RecyclerView)findViewById(R.id.rv_movies_list);
        error=findViewById(R.id.tv_error_message);
        loadMoviesThumnail("popular");

    }

    private void loadMoviesThumnail(String sortBy) {

        moviesList=new ArrayList<>();
        if(sortBy.equals("popular")){
            sortedByPopular=true;
        }
        else {
            sortedByPopular=false;
        }
        if(isOnline()) {
            MovieDataLoadTask movieDataLoadTask = new MovieDataLoadTask();
            String movieJSON = null;
            try {
                movieJSON = movieDataLoadTask.execute(sortBy).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            moviesList = ParseUtility.getMoviesList(movieJSON);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
            mMoviesListRecycler.setHasFixedSize(true);
            mMoviesListRecycler.setLayoutManager(layoutManager);
            MoviesAdapter adapter = new MoviesAdapter(this, moviesList);
            mMoviesListRecycler.setAdapter(adapter);
        }
        else {

            error.setText("No Internet Connection, Try again");
            error.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        if (!isOnline()) {
            disableMenu(menu);
        }
        else {
            enableMenu(menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void enableMenu(Menu menu) {
        menu.getItem(0).setEnabled(true);
        menu.getItem(1).setEnabled(true);
    }

    private void disableMenu(Menu menu) {
        menu.getItem(0).setEnabled(false);
        menu.getItem(1).setEnabled(false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedId=item.getItemId();
        switch (selectedId) {
            case R.id.sort_popular:
                if(sortedByPopular){
                    Toast.makeText(this,"Already sorted by Popularity!",Toast.LENGTH_SHORT).show();
                    return true;
                }
                else {
                    loadMoviesThumnail("popular");
                }
                break;
            case R.id.sort_top :
                if(sortedByPopular){
                    loadMoviesThumnail("top_rated");
                }
                else {
                    Toast.makeText(this,"Already sorted by Top rated!",Toast.LENGTH_SHORT).show();
                    return true;
                }
                break;


        }
        return super.onOptionsItemSelected(item);
    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo==null||!netInfo.isConnected()){
            return false;
        }
        else {
            return true;
        }
    }

    public static class MovieDataLoadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String sortPath=strings[0];
            URL movieDBURL=null;
            String movieJSON=null;

            try {
                movieDBURL=NetworkUtility.buildURL(sortPath);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                 movieJSON=NetworkUtility.getRespsonseFromHttpUrl(movieDBURL);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return movieJSON;
        }


    }
}
