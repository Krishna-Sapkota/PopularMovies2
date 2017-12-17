package com.project.krishna.popularmovies.datamodel;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Krishna on 12/17/17.
 */

public class FavouriteProvider extends ContentProvider {

    public static final int FAVOURITES=100;

    public static final int FAVOURITE_WITH_ID=101;

    private static final UriMatcher sUriMatcher=buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavouriteContract.AUTHORITY,FavouriteContract.PATH_FAVOURITE,FAVOURITES);
        uriMatcher.addURI(FavouriteContract.AUTHORITY,FavouriteContract.PATH_FAVOURITE+"/*",FAVOURITE_WITH_ID);
        return uriMatcher;
    }
    FavouriteDBHelper mFavouriteDbHelper;


    @Override
    public boolean onCreate() {
        Context context=getContext();
        mFavouriteDbHelper=new FavouriteDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase database=mFavouriteDbHelper.getReadableDatabase();

        int match=sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match){
            case FAVOURITES:
                retCursor=database.query(FavouriteContract.FavouriteEntry.TABLE_NAME,
                                         projection,
                                         selection,
                                         selectionArgs,
                                        null,
                                        null,
                                         sortOrder);
                break;
            case FAVOURITE_WITH_ID:
                String idExists=uri.getLastPathSegment();
                String select= FavouriteContract.FavouriteEntry.COLUMN_MOVIE_ID+" =?";
                String[] args={idExists};
                retCursor=database.query(FavouriteContract.FavouriteEntry.TABLE_NAME,
                            null,
                             select,
                             args,
                            null,
                            null,
                             sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri"+uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase database=mFavouriteDbHelper.getWritableDatabase();

        int match=sUriMatcher.match(uri);
        Uri returnUri;
        switch (match){
            case FAVOURITES:
                long id = database.insert(FavouriteContract.FavouriteEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavouriteContract.FavouriteEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
                // Set the value for the returnedUri and write the default case for unknown URI's
                // Default case throws an UnsupportedOperationException

                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase database=mFavouriteDbHelper.getWritableDatabase();
        int del;
        String idToBeDeleted=uri.getLastPathSegment();
        Log.i("PATH",idToBeDeleted);
        String args[]={idToBeDeleted};
        String select= FavouriteContract.FavouriteEntry.COLUMN_MOVIE_ID+" =?";
        switch (sUriMatcher.match(uri)){
            case FAVOURITE_WITH_ID:
                del=database.delete(FavouriteContract.FavouriteEntry.TABLE_NAME,select,args);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri,null);

        return del;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
