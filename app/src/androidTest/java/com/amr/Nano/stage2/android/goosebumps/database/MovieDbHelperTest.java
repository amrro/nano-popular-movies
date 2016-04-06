package com.amr.Nano.stage2.android.goosebumps.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by amro on 4/6/16.
 */
public class MovieDbHelperTest extends AndroidTestCase
{
    public static final String TAG_LOG = MovieDbHelper.class.getSimpleName();

    @Override
    protected void setUp() throws Exception
    {
        // Starting with clean database.
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }


    public void testCreateDataTable()
    {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.GenresEntry.TABLE_NAME);
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);

        // is database open?
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());


        // verify if there are any tables has been created?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());


        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> MovieColumnHashSet = new HashSet<String>();
        MovieColumnHashSet.add(MovieContract.MovieEntry.COL_POSTER_URL);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COL_IS_ADULT);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COL_OVERVIEW);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COL_RELEASE_DATE);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COL_MOVIE_ID);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COL_GENRE_IDS);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COL_ORIGINAL_TITLE);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COL_ORIGINAL_LANG);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COL_BACKDROP_URL);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COL_POPULARITY);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COL_VOTE_COUNT);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COL_VOTE_AVERAGE);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COL_IS_FAVORITE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            MovieColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                MovieColumnHashSet.isEmpty());
        db.close();

        /**
         * passing creating database!! :D
         */

    }

    public void testMoviesTable()
    {

        long rowID = TestUtilities.insertDeadpolValues(mContext);

        //again
        // Make sure we have a valid row ID.
        assertFalse("Error: Location Not Inserted Correctly", rowID == -1L);

    }

    public void testCreateGenresTable()
    {

    }

    public void testInsertingMovie()
    {

    }


}