package com.amr.Nano.stage2.android.goosebumps.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by amro on 4/6/16.
 */
public class MoviesProviderTest extends AndroidTestCase
{



    /*
     * starting with clean provider.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(
                "Doesn't delete all record properly",
                0,
                cursor.getCount()
        );
    }



    public void testGetType()
    {
        // content://com.amr.Nano.stage2.android.goosebumps.database/movie
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        Log.i("URI TYPE: ", type);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

    }

    public void testQueryFromMoviesProvider()
    {


        /*
         * Testing basic queries:
         */
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        ContentValues deadpool = TestUtilities.createDeadpoolValues();
        ContentValues whiplash = TestUtilities.createWhiplashValues();

        long rowDeadpoolID = db.insert(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                deadpool
        );

        long rowWhiplashID = db.insert(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                whiplash
        );
        // first, be sure that insert work properly?
        assertTrue("Sorry insert() doesn't work properly.", rowDeadpoolID != -1);
        assertTrue("Sorry insert() doesn't work properly.", rowWhiplashID != -1);

        db.close();

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertTrue("insert(): Empty cursor returned. ", cursor.moveToFirst());

        ContentValues valuesReturnedFromCursor;
        do
        {
            valuesReturnedFromCursor = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor, valuesReturnedFromCursor);
        } while (cursor.moveToNext());

        assertTrue("return ContentValues doesn't match the original.",
                TestUtilities.compareBetweenTwoContentValues(whiplash, valuesReturnedFromCursor));
    }



    public void testQueriesWithId()
    {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        ContentValues whiplash = TestUtilities.createWhiplashValues();

        long whiplashRowId = db.insert(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                whiplash
        );

        assertTrue("FAiled to insert whiplash movie!", whiplashRowId != -1);
    }




    public void testUpdateMovie()
    {
        deleteAllRecordsFromProvider();

        ContentValues whiplash = TestUtilities.createWhiplashValues();
        Uri whiplashUri = mContext.getContentResolver().insert(
                MovieContract.MovieEntry.CONTENT_URI,
                whiplash
        );

        long whiplashRowID = ContentUris.parseId(whiplashUri);
        assertTrue(whiplashRowID != -1);

        ContentValues updatedWhiplash = new ContentValues(whiplash);
        updatedWhiplash.put(MovieContract.MovieEntry.COL_ORIGINAL_TITLE, "Whiplash part 2");
        updatedWhiplash.put(MovieContract.MovieEntry.COL_MOVIE_ID, "65160");

        int count = mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                updatedWhiplash,
                MovieContract.MovieEntry.COL_MOVIE_ID + "= ?",
                new String[]{whiplash.getAsString(MovieContract.MovieEntry.COL_MOVIE_ID)}
        );
        Log.i("count is :", " " + count);
        assertTrue(count == 1);


        Cursor updatedWhiplashCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COL_MOVIE_ID + " = " + updatedWhiplash.getAsString(MovieContract.MovieEntry.COL_MOVIE_ID),
                null,
                null
        );

        assertTrue(updatedWhiplashCursor.moveToFirst());
        assertTrue(TestUtilities.compareCursorToValues(updatedWhiplashCursor, updatedWhiplash));

    }


    public void testInsertReadMovie()
    {
        deleteAllRecordsFromProvider();
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues whiplash = TestUtilities.createWhiplashValues();
        ContentValues deadpool = TestUtilities.createDeadpoolValues();


        Uri deadPoolUri = mContext.getContentResolver()
                .insert(MovieContract.MovieEntry.CONTENT_URI,
                        deadpool);


        Uri whiplashUri = mContext.getContentResolver().insert(
                MovieContract.MovieEntry.CONTENT_URI,
                whiplash
        );

        Log.d("WhiplashURI: ", whiplashUri.toString());
        Log.d("DeadpoolURI: ", deadPoolUri.toString());

        long WhiplashRowID = ContentUris.parseId(whiplashUri);

        long DeadpoolRowID = ContentUris.parseId(deadPoolUri);
        assertTrue("Failed to insert Deadpool!", DeadpoolRowID != -1);
        assertTrue("Failed to insert Deadpool!", WhiplashRowID != -1);


        Cursor cursor = mContext.getContentResolver()
                .query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );


        Cursor DeadpoolCursor = mContext.getContentResolver()
                .query(
                        deadPoolUri,
                        null,
                        null,
                        null,
                        null
                );



        Cursor WiplashCursor = mContext.getContentResolver()
                .query(
                        whiplashUri,
                        null,
                        null,
                        null,
                        null
                );

        assertTrue("count of Cursor should be 2.", cursor.getCount() == 2);

        Log.i("cursor        : ", cursor.getCount() + " ");
        Log.i("DeadpoolCursor: ", DeadpoolCursor.getCount() + " ");
        Log.i("WhiplashCursor: ", WiplashCursor.getCount() + " ");

        assertTrue("DeadpoolCursor is empty! ", DeadpoolCursor.moveToFirst());
        assertTrue("WhiplashCursor is empty! ", WiplashCursor.moveToFirst());


        assertTrue("return ContentValues doesn't match the original.",
                TestUtilities.compareCursorToValues(DeadpoolCursor, deadpool));


        // PASSED inserting and query with id working as expected.
    }



}