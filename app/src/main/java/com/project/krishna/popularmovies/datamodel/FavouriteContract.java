package com.project.krishna.popularmovies.datamodel;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Krishna on 12/17/17.
 */

public class FavouriteContract {
    public static final String AUTHORITY="com.project.krishna.popularmovies";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+AUTHORITY);

    public static final String PATH_FAVOURITE="favourite";

    public static final class FavouriteEntry implements BaseColumns{
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_FAVOURITE).build();
        public static final String TABLE_NAME="favourite";

        public static final String COLUMN_MOVIE_ID="movie_id";
        public static final String COLUMN_MOVIE_TITLE="movie_title";


    }

}
