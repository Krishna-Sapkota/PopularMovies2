package com.project.krishna.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
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
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieThumbnailClickListener, LoaderManager.LoaderCallbacks<List<Movies>> {
    private static final String SORT_POPULAR ="popular" ;
    private static final int MOVIE_THUMBNAIL_LOADER =10 ;
    private TextView error;
    private RecyclerView mMoviesListRecycler;
    public  ProgressBar mLoadingIndicator;
    private List<Movies> movies;
    private boolean sortedByPopular=true;
    private String movieDetailsJSON;
    private Spinner spinner;
    private boolean firstLoad=true;
    private boolean movieLoaded=false;





    private final static String PARCEABLE_KEY="movie";
    private final String POPULAR_PATH="popular";
    private final String HIGHEST_RATED_PATH="top_rated";
    private final String SORTED_KEY="sortedBy";
    private Bundle savedInstance;
    private MoviesAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesListRecycler=findViewById(R.id.rv_movies_list);
        mLoadingIndicator=findViewById(R.id.progressBar);
        error=findViewById(R.id.tv_error_message);
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new GridLayoutManager(this,numberOfColumns());
        mMoviesListRecycler.setHasFixedSize(true);
        mMoviesListRecycler.setLayoutManager(layoutManager);

        LoaderManager loaderManager=getSupportLoaderManager();
        Loader<List<Movies>> loader=loaderManager.getLoader(MOVIE_THUMBNAIL_LOADER);
        Log.i("TEST","OnCreate");
       /* if(loader==null) {
            Log.i("TEST","Loader is null");
           loaderManager.initLoader(MOVIE_THUMBNAIL_LOADER, null, this);
        }
        else {
            Log.i("TEST","Loader is not null");

            loaderManager.restartLoader(MOVIE_THUMBNAIL_LOADER,null,this);
        }*/
       loaderManager.initLoader(MOVIE_THUMBNAIL_LOADER,null,this);





    }

    /**
     * save the sort order to display same sorting when screen rotates

     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SORTED_KEY,sortedByPopular);

    }
    public static String getParceableKey() {
        return PARCEABLE_KEY;
    }


  /*  private void loadMoviesThumnail(String sortBy) {

        if(isOnline()) {
            error.setVisibility(View.INVISIBLE);
            MovieDataLoadTask movieDataLoadTask = new MovieDataLoadTask();
            movieDataLoadTask.execute(sortBy);
        }
        else {
            error.setText(getResources().getString(R.string.internet_error));
            error.setVisibility(View.VISIBLE);
        }
    }*

    /**
     * Creating spinner on toolbar from
     * https://stackoverflow.com/questions/37250397/how-to-add-a-spinner-next-to-a-menu-in-the-toolbar
     *
     * @param menu
     * @return
     */
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
             //  loadMoviesThumnail(POPULAR_PATH);
       }
       else if(item.equals(top)) {

        sortedByPopular = false;
        //loadMoviesThumnail(HIGHEST_RATED_PATH);

       }



    }

    /**
     checking online code used from
     https://www.google.com/url?q=http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-timeouts&sa=D&ust=1510813688269000&usg=AFQjCNF0VNbteS1wdDxpwk5kwL7t-zQlyA
     **/

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

   /* public  class MovieDataLoadTask extends AsyncTask<String,Void,String>{

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
            RecyclerView.LayoutManager layoutManager;
            layoutManager = new GridLayoutManager(getApplicationContext(),numberOfColumns());
            mMoviesListRecycler.setHasFixedSize(true);
            mMoviesListRecycler.setLayoutManager(layoutManager);
            MoviesAdapter adapter = new MoviesAdapter(getApplicationContext(), moviesList,MainActivity.this);
            mMoviesListRecycler.setAdapter(adapter);
        }
    }*/

    @Override
    public void onThumnailClick(int clickedIndex) {
        String clickedMovie=movies.get(clickedIndex).getMovieId();
        MovieDetails movieDetails=ParseUtility.getMovieDetails(movieDetailsJSON,clickedMovie);
        Intent detailsActivity=new Intent(this,MovieDetailsActivity.class);
        detailsActivity.putExtra(PARCEABLE_KEY,movieDetails);
        startActivity(detailsActivity);
    }
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    @Override
    public Loader<List<Movies>> onCreateLoader(int id, Bundle args) {

        final String sort="popular";
        switch (id){
            case MOVIE_THUMBNAIL_LOADER:
                return new AsyncTaskLoader<List<Movies>>(this) {
                    List<Movies> moviesList=null;

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        if(moviesList!=null){
                            deliverResult(moviesList);
                            Log.i("TEST","movie list contains data");
                        }
                        else{
                            mLoadingIndicator.setVisibility(View.VISIBLE);
                            forceLoad();
                            Log.i("TEST","movie list does not contains data");

                        }
                    }

                    @Override
                    public List<Movies> loadInBackground() {
                        URL movieDBURL=null;
                        String movieJSON=null;

                        try {
                            movieDBURL= NetworkUtility.buildURL(sort);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        try {
                            movieJSON=NetworkUtility.getRespsonseFromHttpUrl(movieDBURL);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        moviesList = ParseUtility.getMoviesList(movieJSON);

                        return moviesList;
                    }



                    @Override
                    public void deliverResult(List<Movies> data) {
                        moviesList=data;
                        super.deliverResult(data);
                    }
                };
            default:
                return null;
        }


    }

    @Override
    public void onLoadFinished(Loader<List<Movies>> loader, List<Movies> moviesList) {
        movies=moviesList;
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        MoviesAdapter adapter = new MoviesAdapter(this, movies, MainActivity.this);
        mMoviesListRecycler.setAdapter(adapter);

    }

    @Override
    public void onLoaderReset(Loader<List<Movies>> loader) {

    }


}
