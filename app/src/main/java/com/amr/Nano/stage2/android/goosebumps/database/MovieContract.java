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


        // movie  columns:
        public static final String TABLE_NAME           = "movie_table";
        public static final String COL_POSTER_URL       = "poster_path";
        public static final String COL_IS_ADULT         = "adult";
        public static final String COL_OVERVIEW         = "overview";
        public static final String COL_RELEASE_DATE     = "release_date";
        public static final String COL_MOVIE_ID         = "id";
        public static final String COL_GENRE_IDS        = "genres_ids";
        public static final String COL_ORIGINAL_TITLE   = "id";
        public static final String COL_ORIGINAL_LANG    = "original_title";
        public static final String COL_BACKDROP_URL     = "backdrop_path";
        public static final String COL_POPULARITY       = "popularity";
        public static final String COL_VOTE_COUNT       = "vote_count";
        public static final String COL_VOTE_AVERAGE     = "vote_average";
        public static final String COL_IS_FAVORITE      = "is_favorite_movie";


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
