package com.project.krishna.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
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
import android.widget.Toast;

import com.project.krishna.popularmovies.datamodel.FavouriteContract;
import com.project.krishna.popularmovies.datamodel.MovieDetails;
import com.project.krishna.popularmovies.datamodel.Movies;
import com.project.krishna.popularmovies.datamodel.MoviesAdapter;
import com.project.krishna.popularmovies.utils.NetworkUtility;
import com.project.krishna.popularmovies.utils.ParseUtility;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieThumbnailClickListener, LoaderManager.LoaderCallbacks<List<Movies>>,SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String SORT_POPULAR ="Popular";
    private static final String SORT_TOP="Top Rated";
    private static final String SORT_FAVOURITE="Favourite";
    private static final int MOVIE_THUMBNAIL_LOADER =10 ;
    private static final int FAVOURITE_LOADER = 11;
    private static final int FAVOURTITE_MOVIE_DETAILS =12 ;
    private static final String MOVIE_ID ="movie_key" ;
    private TextView error;
    private RecyclerView mMoviesListRecycler;
    public  ProgressBar mLoadingIndicator;
    private List<Movies> movies;
    private boolean sortedByPopular=true;
    private boolean sortedByFavourite=false;
    private boolean sortedByTop=false;
    private Spinner spinner;
    private boolean firstLoad=true;
    private Bundle sort;

    private final static String PARCEABLE_KEY="movie";
    private final String POPULAR_PATH="popular";
    private final String HIGHEST_RATED_PATH="top_rated";
    private final String SORTED_KEY="sortedBy";
    private MoviesAdapter adapter;
    private static String movieJSON;

    final static String MOVIE_ID_INDEX= FavouriteContract.FavouriteEntry.COLUMN_MOVIE_ID;
    final static String MOVIE_POSTER_INDEX= FavouriteContract.FavouriteEntry.MOVIE_THUMBNAIL_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        mMoviesListRecycler=(RecyclerView)findViewById(R.id.rv_movies_list);
        mLoadingIndicator=(ProgressBar)findViewById(R.id.progressBar);
        error=findViewById(R.id.tv_error_message);
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new GridLayoutManager(this,numberOfColumns());
        mMoviesListRecycler.setHasFixedSize(true);
        mMoviesListRecycler.setLayoutManager(layoutManager);

        LoaderManager loaderManager=getSupportLoaderManager();
        Loader<List<Movies>> loader=loaderManager.getLoader(MOVIE_THUMBNAIL_LOADER);
        sort=new Bundle();
        String sortBy=getSortPreference();

        sort.putString(SORTED_KEY,sortBy);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);




        if(isOnline()) {
            error.setVisibility(View.INVISIBLE);

            if (sortBy.trim().equals(getString(R.string.sort_by_favourite_value).trim())) {
                if (getSupportLoaderManager().getLoader(FAVOURITE_LOADER) != null) {
                    getSupportLoaderManager().restartLoader(FAVOURITE_LOADER, null, new FavouriteLoader());
                } else {
                    getSupportLoaderManager().initLoader(FAVOURITE_LOADER, null, new FavouriteLoader());
                }
                sortedByFavourite = true;
            } else {
                loaderManager.initLoader(MOVIE_THUMBNAIL_LOADER, sort, this);
            }

        }
        else {
            error.setVisibility(View.VISIBLE);
            error.setText(R.string.internet_error);
        }

    }

    private String getSortPreference() {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(getString(R.string.sort_key),getString(R.string.default_sort));
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
        getMenuInflater().inflate(R.menu.settings_overflow,menu);

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
        String sortBy=getSortPreference();
        if(isOnline())
        setSpinnerSelection(sortBy);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.settings){
            Intent settingsActivity=new Intent(this,PreferenceActivity.class);
            startActivity(settingsActivity);
        }

        return super.onOptionsItemSelected(item);
    }

    private void spinnerSelected(){
        String item=spinner.getSelectedItem().toString();
        String popular=getResources().getStringArray(R.array.spinner_list_item_array)[0];
        String top=getResources().getStringArray(R.array.spinner_list_item_array)[1];
        String fav=getResources().getStringArray(R.array.spinner_list_item_array)[2];
        if(item.equals(popular)) {
            sortedByPopular=true;
            sortedByFavourite=false;
            sortedByTop=false;
            sort.putString(SORTED_KEY,SORT_POPULAR);
            getSupportLoaderManager().restartLoader(MOVIE_THUMBNAIL_LOADER,sort,this);
       }
       else if(item.equals(top)) {

            sortedByTop = true;
            sortedByPopular=false;
            sortedByFavourite=false;
            sort.putString(SORTED_KEY,SORT_TOP);
            getSupportLoaderManager().restartLoader(MOVIE_THUMBNAIL_LOADER,sort,this);

       }
       else if(item.equals(fav)){
            sort.putString(SORTED_KEY,SORT_FAVOURITE);
            sortedByFavourite=true;
            sortedByPopular=false;
            sortedByTop=false;
            if(getSupportLoaderManager().getLoader(FAVOURITE_LOADER)!=null) {
                getSupportLoaderManager().restartLoader(FAVOURITE_LOADER, null, new FavouriteLoader());
            }
            else {
                getSupportLoaderManager().initLoader(FAVOURITE_LOADER,null,new FavouriteLoader());
            }
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



    @Override
    public void onThumnailClick(int clickedIndex) {
        if(isOnline()) {
            String clickedMovie = movies.get(clickedIndex).getMovieId();
            if (((getSortPreference().trim().equals(getString(R.string.sort_by_favourite_value).trim())) ||
                    sortedByFavourite) && !sortedByPopular && !sortedByTop) {
                Bundle movie = new Bundle();
                movie.putString(MOVIE_ID, clickedMovie);
                if (getSupportLoaderManager().getLoader(FAVOURTITE_MOVIE_DETAILS) != null) {
                    getSupportLoaderManager().restartLoader(FAVOURTITE_MOVIE_DETAILS, movie, new MovieDetailsLoader());
                } else {
                    getSupportLoaderManager().initLoader(FAVOURTITE_MOVIE_DETAILS, movie, new MovieDetailsLoader());
                }
            }
            if (!sortedByFavourite)
                launchDetail(movieJSON, clickedMovie);
        }
        else {
            Toast.makeText(this,getString(R.string.internet_error),Toast.LENGTH_LONG).show();
        }

    }

    private void launchDetail(String movieJSON, String clickedMovie) {
        MovieDetails movieDetails=null;
        movieDetails=ParseUtility.getMovieDetails(movieJSON,clickedMovie);
        Intent detailsActivity=new Intent(this,MovieDetailsActivity.class);
        detailsActivity.putExtra(PARCEABLE_KEY,movieDetails);
        startActivity(detailsActivity);
    }
    private void launchFavDetails(MovieDetails movieDetails){
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
    public Loader<List<Movies>> onCreateLoader(int id, final Bundle args) {

        switch (id){
            case MOVIE_THUMBNAIL_LOADER:
                return new AsyncTaskLoader<List<Movies>>(this) {
                    List<Movies> moviesList=null;

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        if(moviesList!=null){
                            deliverResult(moviesList);
                        }
                        else{
                            mLoadingIndicator.setVisibility(View.VISIBLE);
                            forceLoad();
                        }
                    }

                    @Override
                    public List<Movies> loadInBackground() {
                        URL movieDBURL=null;
                        movieJSON=null;
                        String sort=null;
                        String sortOrder=args.getString(SORTED_KEY);
                        if(sortOrder.equals(SORT_POPULAR)){
                            sort=POPULAR_PATH;
                        } else if(sortOrder.equals(SORT_TOP)){
                            sort=HIGHEST_RATED_PATH;
                        }
                        else {
                            sort=POPULAR_PATH;
                        }

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
        adapter = new MoviesAdapter(this, movies, MainActivity.this);
        mMoviesListRecycler.setAdapter(adapter);

    }

    @Override
    public void onLoaderReset(Loader<List<Movies>> loader) {

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        String sortBy=sharedPreferences.getString(getString(R.string.sort_key),getString(R.string.default_sort));
        sort.putString(SORTED_KEY,sortBy);
        setSpinnerSelection(sortBy);

        if(sortBy.trim().equals(getString(R.string.sort_by_favourite_value).trim())){
            Log.i("SORT",sortBy);

            LoaderManager loaderManager=getSupportLoaderManager();
            Loader<List<Movies>> loader=loaderManager.getLoader(FAVOURITE_LOADER);

            if(loader!=null){
                loaderManager.restartLoader(FAVOURITE_LOADER,null,new FavouriteLoader());
            }
            else {
                loaderManager.initLoader(FAVOURITE_LOADER,null,new FavouriteLoader());
            }
            sortedByFavourite=true;
            sortedByPopular=false;
            sortedByTop=false;
        }
        else {
            sortedByFavourite=false;
            if(sortBy.equals(getString(R.string.sort_by_popular_value))) sortedByPopular=true;
            if(sortBy.equals(getString(R.string.sort_by_top_value))) sortedByTop=true;
            getSupportLoaderManager().restartLoader(MOVIE_THUMBNAIL_LOADER, sort, this);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public void setSpinnerSelection(String sortBy){
        int selected=0;
        String[] selectionList=getResources().getStringArray(R.array.spinner_list_item_array);
        for(int i=0;i<selectionList.length;i++) {
            if (sortBy.equals(selectionList[i])) {
                selected = i;
            }
        }
        spinner.setSelection(selected);

    }

    private class FavouriteLoader implements LoaderManager.LoaderCallbacks<Cursor>{

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MainActivity.this,FavouriteContract.FavouriteEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                List<Movies> moviesList=new ArrayList<>();
                if(cursor.moveToFirst()){
                    do {
                        Movies mov = new Movies();
                        String posterURL = cursor.getString(cursor.getColumnIndex(MOVIE_POSTER_INDEX));

                        Uri.Builder builder= NetworkUtility.getPosterBase();

                        builder.appendEncodedPath(posterURL);
                        builder.appendQueryParameter(NetworkUtility.getParamApiKey(),NetworkUtility.getApiKey());
                        Uri thumbnailUri=builder.build();
                        URL url=null;
                        try {
                            url = new URL(thumbnailUri.toString());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        String movieId = cursor.getString(cursor.getColumnIndex(MOVIE_ID_INDEX));
                        mov.setMovieId(movieId);

                        mov.setPosterURL(url.toString());
                        moviesList.add(mov);
                    } while (cursor.moveToNext());
                }
                    movies=moviesList;
                    if(adapter!=null) {
                        adapter.setMovieData(moviesList);
                    }
                    else {
                        adapter=new MoviesAdapter(MainActivity.this,moviesList,MainActivity.this);
                        mMoviesListRecycler.setAdapter(adapter);

                    }
                    cursor.close();


        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    private class MovieDetailsLoader implements LoaderManager.LoaderCallbacks<String> {
        String movieId;
        @Override
        public Loader<String> onCreateLoader(int id, final Bundle args) {
             movieId=args.getString(MOVIE_ID);

            return new AsyncTaskLoader<String>(MainActivity.this) {
                String json;
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if(json!=null){
                        deliverResult(json);
                    }
                    else {
                        mLoadingIndicator.setVisibility(View.VISIBLE);

                        forceLoad();
                    }
                }

                @Override
                public String loadInBackground() {

                    String json=null;

                    Uri.Builder builder=NetworkUtility.getBaseURI();
                    builder.appendPath(movieId);
                    builder.appendQueryParameter(NetworkUtility.getParamApiKey(),NetworkUtility.getApiKey());
                    Uri uri=builder.build();
                    try {
                        json=NetworkUtility.getRespsonseFromHttpUrl(new URL(uri.toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return json;
                }

                @Override
                public void deliverResult(String data) {
                    json=data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {
            movieJSON=data;
            MovieDetails movieDetails=null;
            try {
                 movieDetails=ParseUtility.getFavMovieDeatails(movieJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            launchFavDetails(movieDetails);
            mLoadingIndicator.setVisibility(View.INVISIBLE);

        }

        @Override
        public void onLoaderReset(Loader<String> loader) {

        }
    }
}
