package com.amr.Nano.stage2.android.goosebumps.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by amro on 4/6/16.
 */
public class MoviesProvider extends ContentProvider
{

    public static final String CAT_TAG = MoviesProvider.class.getSimpleName();
    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final int MOVIES = 100;
    public static final int MOVIE_ID = 101;


    private SQLiteOpenHelper mOpenHelper;

    private static UriMatcher buildUriMatcher()
    {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MovieContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(authority, "movies", MOVIES);
        uriMatcher.addURI(authority, "movies/#", MOVIE_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate()
    {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri))
        {
            case MOVIES:
            {
                // return all movies.
                cursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return cursor;
            }

            case MOVIE_ID:
            {
                /* return movie with specific _ID  */
                String movieId = MovieContract.MovieEntry.getMovieId(uri);
                cursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COL_MOVIE_ID + "= ?",
                        new String[]{movieId},
                        null, null, null
                );
                return cursor;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


    }

    @Nullable
    @Override
    public String getType(Uri uri)
    {
        final int matchNum = sUriMatcher.match(uri);

        switch (matchNum)
        {
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("not valid Uri");
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri retUri = null;

        switch (sUriMatcher.match(uri))
        {
            case MOVIES:
            {
                long rowId = db.insert(
                        MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        values
                );
                if (rowId > 0)
                    retUri = MovieContract.MovieEntry
                            .buildMovieUri(values.getAsString(MovieContract.MovieEntry.COL_MOVIE_ID));
               break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int deletedRow;

        switch (sUriMatcher.match(uri))
        {
            case MOVIES:
            {
                deletedRow = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (deletedRow != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return deletedRow;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int updatedRows;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri))
        {
            case MOVIES:
            {
                updatedRows = db.update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (updatedRows != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }
}
