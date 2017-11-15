package com.project.krishna.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.project.krishna.popularmovies.datamodel.MovieDetails;
import com.project.krishna.popularmovies.datamodel.Movies;
import com.project.krishna.popularmovies.datamodel.MoviesAdapter;
import com.project.krishna.popularmovies.utils.NetworkUtility;
import com.project.krishna.popularmovies.utils.ParseUtility;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieThumbnailClickListener {
    private TextView error;
    private RecyclerView mMoviesListRecycler;
    private ProgressBar mLoadingIndicator;
    private List<Movies> moviesList;
    private boolean sortedByPopular=true;
    private RecyclerView.LayoutManager layoutManager;
    private String movieDetailsJSON;
    private Spinner spinner;
    private boolean firstLoad=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMoviesListRecycler=findViewById(R.id.rv_movies_list);
        mLoadingIndicator=findViewById(R.id.progressBar);
        error=findViewById(R.id.tv_error_message);
        moviesList=new ArrayList<>();
        if(savedInstanceState!=null) {
            boolean sortOrder=savedInstanceState.getBoolean("sortedBy");
            if(sortOrder){
                sortedByPopular=true;
                loadMoviesThumnail("popular");
            }
            else {
                sortedByPopular=false;
                loadMoviesThumnail("top_rated");
            }
        }
        else {
            loadMoviesThumnail("popular");
        }

    }

    /*
    save the sort order to display same sorting when screen rotates
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("sortedBy",sortedByPopular);

    }

    private void loadMoviesThumnail(String sortBy) {

        if(isOnline()) {
            error.setVisibility(View.INVISIBLE);
            MovieDataLoadTask movieDataLoadTask = new MovieDataLoadTask();
            movieDataLoadTask.execute(sortBy);
        }
        else {
            error.setText(getResources().getString(R.string.internet_error));
            error.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list_item_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                if(firstLoad) {
                    firstLoad=false;
                }
                else{
                    spinnerSelected();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return true;
    }




    private void spinnerSelected(){
        String item=spinner.getSelectedItem().toString();
        String popular=getResources().getStringArray(R.array.spinner_list_item_array)[0];
        String top=getResources().getStringArray(R.array.spinner_list_item_array)[1];
        if(item.equals(popular)) {
               sortedByPopular=true;
               loadMoviesThumnail("popular");
       }
       else if(item.equals(top)) {

        sortedByPopular = false;
        loadMoviesThumnail("top_rated");

       }



    }
    private boolean isOnline() {
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

    public  class MovieDataLoadTask extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);

        }

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

        @Override
        protected void onPostExecute(String movieJSON) {
            super.onPostExecute(movieJSON);
            movieDetailsJSON=movieJSON;
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            moviesList = ParseUtility.getMoviesList(movieJSON);
            layoutManager = new GridLayoutManager(getApplicationContext(), 2);
            mMoviesListRecycler.setHasFixedSize(true);
            mMoviesListRecycler.setLayoutManager(layoutManager);
            MoviesAdapter adapter = new MoviesAdapter(getApplicationContext(), moviesList,MainActivity.this);
            RecyclerView.ItemAnimator itemAnimator=new DefaultItemAnimator();
            mMoviesListRecycler.setItemAnimator(itemAnimator);
            mMoviesListRecycler.setAdapter(adapter);

        }
    }

    @Override
    public void onThumnailClick(int clickedIndex) {
        String clickedMovie=moviesList.get(clickedIndex).getMovieId();
        MovieDetails movieDetails=ParseUtility.getMovieDetails(movieDetailsJSON,clickedMovie);
        Intent detailsActivity=new Intent(this,MovieDetailsActivity.class);
        detailsActivity.putExtra("movie",movieDetails);
        startActivity(detailsActivity);
    }
}
