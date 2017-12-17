package com.project.krishna.popularmovies.datamodel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.project.krishna.popularmovies.datamodel.FavouriteContract.FavouriteEntry;

/**
 * Created by Krishna on 12/17/17.
 */

public class FavouriteDBHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME="favDb.db";
    private static final int DB_VERSION=2;

    FavouriteDBHelper(Context context){
        super(context,DATABASE_NAME,null,DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
            final String CREATE_TABLE="CREATE TABLE "+FavouriteEntry.TABLE_NAME + " (" +
                    FavouriteEntry.COLUMN_MOVIE_ID   +" TEXT PRIMARY KEY ,"+
                    FavouriteEntry.COLUMN_MOVIE_TITLE+" TEXT NOT NULL);";
            db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteEntry.TABLE_NAME);
        onCreate(db);

    }
}
