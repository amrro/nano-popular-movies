package com.amr.Nano.stage2.android.goosebumps.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * Created by amro on 4/6/16.
 */
public class TestUtilities extends AndroidTestCase
{

    public static ContentValues createDeadpoolValues()
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COL_POSTER_URL    , "/inVq3FRqcYIRl2la8iZikYYxFNR.jpg");
        values.put(MovieContract.MovieEntry.COL_IS_ADULT      , 1);
        values.put(MovieContract.MovieEntry.COL_OVERVIEW      , "Based upon Marvel Comics’ most unconventional anti-hero, DEADPOOL tells the origin stocgcf sflkgdf lg dl");
        values.put(MovieContract.MovieEntry.COL_RELEASE_DATE  , "2016-02-09");
        values.put(MovieContract.MovieEntry.COL_MOVIE_ID      , 293660 );
        values.put(MovieContract.MovieEntry.COL_GENRE_IDS     , "28, 12, 35");
        values.put(MovieContract.MovieEntry.COL_ORIGINAL_TITLE, "Deadpool");
        values.put(MovieContract.MovieEntry.COL_ORIGINAL_LANG , "en");
        values.put(MovieContract.MovieEntry.COL_BACKDROP_URL  , "/n1y094tVDFATSzkTnFxoGZ1qNsG.jpg");
        values.put(MovieContract.MovieEntry.COL_POPULARITY    , 25.320459);
        values.put(MovieContract.MovieEntry.COL_VOTE_COUNT    , 2564);
        values.put(MovieContract.MovieEntry.COL_VOTE_AVERAGE  , 0);
        values.put(MovieContract.MovieEntry.COL_IS_FAVORITE   , 7.18);

        return values;
    }


    public static long insertDeadpolValues(Context context)
    {
        SQLiteDatabase db = new MovieDbHelper(context).getWritableDatabase();
        long rowId;
        rowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, createDeadpoolValues());

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", rowId != -1);

        return rowId;
    }


    public void insertGenresValues(Context context)
    {
        SQLiteDatabase db = new MovieDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MovieContract.GenresEntry.GENERE_NAME, "Action");
        values.put(MovieContract.GenresEntry.GENRE_ID, 28);
        db.insert(MovieContract.GenresEntry.TABLE_NAME, null, values);
        values.clear();

        values.put(MovieContract.GenresEntry.GENERE_NAME, "Adventure");
        values.put(MovieContract.GenresEntry.GENRE_ID, 12);
        long rowId = db.insert(MovieContract.GenresEntry.TABLE_NAME, null, values);
        assertTrue("Error: Failure to insert North Pole Location Values", rowId != -1);
    }
}
