package com.amr.Nano.stage2.android.goosebumps.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.amr.Nano.stage2.android.goosebumps.database.MovieContract.*;

/**
 * Created by amro on 4/5/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper
{

    public static final String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 2;


    public MovieDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + "("
                + MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieEntry.COL_POSTER_URL + " TEXT NOT NULL, "
                + MovieEntry.COL_IS_ADULT + " INTEGER, "
                + MovieEntry.COL_OVERVIEW + " TEXT NOT NULL, "
                + MovieEntry.COL_RELEASE_DATE + " TEXT, "
                + MovieEntry.COL_MOVIE_ID + " TEXT NOT NULL, "
                + MovieEntry.COL_GENRE_IDS + " TEXT, "
                + MovieEntry.COL_ORIGINAL_TITLE + " TEXT NOT NULL, "
                + MovieEntry.COL_ORIGINAL_LANG + " TEXT NOT NULL, "
                + MovieEntry.COL_BACKDROP_URL + " TEXT, "
                + MovieEntry.COL_POPULARITY + " REAL, "
                + MovieEntry.COL_VOTE_COUNT + " INTEGER, "
                + MovieEntry.COL_VOTE_AVERAGE + " REAL, "
                + MovieEntry.COL_IS_FAVORITE + " INTEGER);";

        db.execSQL(CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("DATABASE_VERSION: ", "Database version has changed from"
        + oldVersion + "to " + newVersion);

        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
