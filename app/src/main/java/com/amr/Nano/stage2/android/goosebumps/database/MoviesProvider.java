package com.amr.Nano.stage2.android.goosebumps.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
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
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        return null;
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
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return 0;
    }
}
