package com.amr.Nano.stage2.android.goosebumps.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

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
        values.put(MovieContract.MovieEntry.COL_IS_FAVORITE, 7.18);

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


    /**
     * creates whiplash movies data.
     *
     * @return
     */
    public static ContentValues createWhiplashValues()
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COL_POSTER_URL    ,"/lIv1QinFqz4dlp5U4lQ6HaiskOZ.jpg");
        values.put(MovieContract.MovieEntry.COL_IS_ADULT      , 0);
        values.put(MovieContract.MovieEntry.COL_OVERVIEW      , "Under the direction of a ruthless instructor, a talented young drummer begins to pursue perfection at any cost, even his humanity.");
        values.put(MovieContract.MovieEntry.COL_RELEASE_DATE  , "2014-10-10");
        values.put(MovieContract.MovieEntry.COL_MOVIE_ID      , 244786 );
        values.put(MovieContract.MovieEntry.COL_GENRE_IDS     , "18, 10402");
        values.put(MovieContract.MovieEntry.COL_ORIGINAL_TITLE, "Whiplash");
        values.put(MovieContract.MovieEntry.COL_ORIGINAL_LANG , "en");
        values.put(MovieContract.MovieEntry.COL_BACKDROP_URL  , "/6bbZ6XyvgfjhQwbplnUh1LSj1ky.jpg");
        values.put(MovieContract.MovieEntry.COL_POPULARITY    , 9.250192);
        values.put(MovieContract.MovieEntry.COL_VOTE_COUNT    , 1762);
        values.put(MovieContract.MovieEntry.COL_VOTE_AVERAGE  , 0);
        values.put(MovieContract.MovieEntry.COL_IS_FAVORITE, 8.36);

        return values;
    }






    public static boolean compareBetweenTwoContentValues(ContentValues values, ContentValues toCompareValues)
    {
        String title = MovieContract.MovieEntry.COL_ORIGINAL_TITLE;
        String vote  = MovieContract.MovieEntry.COL_VOTE_AVERAGE;

        return
                values.getAsString(title).compareTo(toCompareValues.getAsString(title)) == 0 ? true : false  &&
                values.getAsFloat(vote) == toCompareValues.getAsFloat(vote);
    }


    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }


    public static boolean compareCursorToValues(Cursor cursor, ContentValues values)
    {
        ContentValues fromCursor = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, fromCursor);
        return compareBetweenTwoContentValues(values, fromCursor);

    }





}
