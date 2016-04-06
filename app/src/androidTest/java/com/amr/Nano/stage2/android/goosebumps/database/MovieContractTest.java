package com.amr.Nano.stage2.android.goosebumps.database;

import android.net.Uri;

import junit.framework.TestCase;

/**
 * Created by amro on 4/6/16.
 */
public class MovieContractTest extends TestCase
{
    public void testBuildingMovieUri()
    {
        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(646);
        assertNotNull("Error: Null uri returned.", movieUri);
        assertEquals("Error in building the Uri", 646, MovieContract.MovieEntry.getMovieId(movieUri));
    }
    
}