package com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters;

/**
 * Created by amro on 4/16/16.
 */
public class Movie
{

    private final int ID;

    private final String posterURL;


    public Movie(int ID, String posterURL)
    {
        this.ID = ID;
        this.posterURL = posterURL;
    }

    public int getID()
    {
        return ID;
    }

    public String getPosterURL()
    {
        return posterURL;
    }

    @Override
    public String toString()
    {
        return null;
    }

    public static final String API_BASE_URL = "https://api.themoviedb.org/3";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    public static final String API_kEY_QUERY = "api_key";
    public static final String API_KEY = "f5d2da75e7729eee412a43da5f542a9c";

    public static final String POSTER_URL       = "poster_path";
    public static final String IS_ADULT         = "adult";
    public static final String OVERVIEW         = "overview";
    public static final String RELEASE_DATE     = "release_date";
    public static final String MOVIE_ID         = "id";
    public static final String GENRE_IDS        = "genres_ids";
    public static final String ORIGINAL_TITLE   = "original_title";
    public static final String ORIGINAL_LANG    = "original_title";
    public static final String BACKDROP_URL     = "backdrop_path";
    public static final String POPULARITY       = "popularity";
    public static final String VOTE_COUNT       = "vote_count";
    public static final String VOTE_AVERAGE     = "vote_average";

    public static final String RUNTIME          = "runtime";


    public static final String[] POSTER_SIZES = new String[]
            {"w92",
                    "w154",
                    "w185",
                    "w342",
                    "w500",
                    "w780",
                    "original"
            };

    public static final String[] BACKDROP_SIZES = new String[]
            {
                    "w300",
                    "w780",
                    "w1280",
                    "original"
            };
}
