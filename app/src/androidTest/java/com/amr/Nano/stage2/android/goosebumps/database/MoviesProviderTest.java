package com.amr.Nano.stage2.android.goosebumps.database;

import android.test.AndroidTestCase;

/**
 * Created by amro on 4/6/16.
 */
public class MoviesProviderTest extends AndroidTestCase
{
    public void testGetType()
    {
        // content://com.amr.Nano.stage2.android.goosebumps.database/movie
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);

        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

    }
}