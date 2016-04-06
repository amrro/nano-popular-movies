package com.amr.Nano.stage2.android.goosebumps.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by amro on 4/5/16.
 */
public class MovieContract
{
    public static final String CONTENT_AUTHORITY = "com.amr.Nano.stage2.android.goosebumps.database";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);




    /*
     * the table to hold different types of genres ids and names.
     * A movie has many types of genres ids.
     * found here: https://api.themoviedb.org/3/genre/movie/list?api_key=API_KEY_HERE
     */
    public static class GenresEntry implements BaseColumns
    {
        public static final String PATH_LOCATION = "GENRES";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME   = "GENERE_IDS_TABLE";
        public static final String GENRE_ID     = "GENRE_ID";
        public static final String GENERE_NAME  = "GENRE_NAME";
    }



    /*
     *  table to hold the movie data and info found in the result page.
     *  api link: https://api.themoviedb.org/3/movie/popular?api_key=API_KEY_HERE
     */
    public static class MovieEntry implements BaseColumns
    {
        public static final String PATH_LOCATION = "movies";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                 + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_LOCATION;


        // movie table columns:
        public static final String TABLE_NAME           = "MOVIE_TABLE";
        public static final String COL_POSTER_URL       = "POSTER_URL";
        public static final String COL_IS_ADULT         = "IS_ADULT";
        public static final String COL_OVERVIEW         = "MOVIE_OVERVIEW";
        public static final String COL_RELEASE_DATE     = "RELEASE_DATE";
        public static final String COL_MOVIE_ID         = "MOVIE_ID";
        public static final String COL_GENRE_IDS        = "GENRES_IDS";
        public static final String COL_ORIGINAL_TITLE   = "ID";
        public static final String COL_ORIGINAL_LANG    = "ORIGINAL_TITLE";
        public static final String COL_BACKDROP_URL     = "BACKDROP_URL";
        public static final String COL_POPULARITY       = "MOVIE_POPULARITY";
        public static final String COL_VOTE_COUNT       = "VOTE_COUNT";
        public static final String COL_VOTE_AVERAGE     = "VOTE_AVERAGE";
        public static final String COL_IS_FAVORITE      = "IS_FAVORITE_MOVIE";


        public static Uri buildMovieUri (long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * override the previous method to give more flexibility!
         * @param movieId integer value of the movie id.
         * @return the uri
         */
        public static Uri buildMovieUri (String movieId)
        {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieId(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

    }
}
