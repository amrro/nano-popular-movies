package com.amr.Nano.stage2.android.goosebumps.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amr.Nano.stage2.android.goosebumps.R;
import com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters.Movie;
import com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters.MoviesAdapter;
import com.amr.Nano.stage2.android.goosebumps.database.MovieContract;
import com.amr.Nano.stage2.android.goosebumps.network.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * Created by amro on 4/16/16.
 */

public class MainFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int MOVIES_LOADER_ID = 100;
    final private int SPAN_COUNT = 2;
    final private String TAG = MainFragment.class.getSimpleName();

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private SharedPreferences prefs;

    @Bind(R.id.main_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.main_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @Bind(R.id.swipe_refresh_movies)
    SwipeRefreshLayout mRefreshMovies;

    private RecyclerView.LayoutManager mLayoutManager;
    private MoviesAdapter mMoviesAdapter;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
        mCollapsingToolbar.setTitle("Most Popular");

        mMoviesAdapter = new MoviesAdapter(getContext(), new ArrayList<Movie>(),MainActivity.mTwoPane);
        // improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRefreshMovies.setOnRefreshListener(this);

        mLayoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mMoviesAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.popular_item);
        String title = prefs.getString(
                getString(R.string.prefs_sorting),
                getString(R.string.prefs_popular)
        );

        if (title.equals(getString(R.string.prefs_popular)))
        {
            item.setTitle(getString(R.string.menu_popular).toUpperCase());
        }

        if (title.equals(getString(R.string.prefs_top_rated)))
        {
            item.setTitle(getString(R.string.menu_top).toUpperCase());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        SharedPreferences.Editor editor = prefs.edit();

        switch (item.getItemId())
        {
            case R.id.popular_item:
            {
               return updateMainFragment(
                        getString(R.string.menu_popular),
                        getString(R.string.prefs_popular)
                );
            }
            case R.id.top_rated_item:
            {
                return updateMainFragment(
                        getString(R.string.menu_top),
                        getString(R.string.prefs_top_rated)
                );
            }
            case R.id.favorites:
            {
                return updateMainFragment(
                        getString(R.string.prefs_favorites),
                        getString(R.string.prefs_favorites)
                );
            }
            default:
                Log.d("onOptionsItemSelected", "There is no menuItem with this id");
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean  updateMainFragment(String title, String prefSorting)
    {
        SharedPreferences.Editor editor = prefs.edit();
        mCollapsingToolbar.setTitle(title);

        editor.putString(
                getString(R.string.prefs_sorting),
                prefSorting
        );
        editor.apply();
        chooseAdapter();
        return true;
    }


    private void updateAdapterFromCursor(Cursor cursor)
    {
        ArrayList<Movie> favoritesList = new ArrayList<>();
        int movieIdIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COL_MOVIE_ID);
        int posterIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COL_POSTER_URL);

        try
        {
            while (cursor.moveToNext())
            {
                favoritesList.add(
                        new Movie(
                                cursor.getInt(movieIdIndex),
                                cursor.getString(posterIndex)
                        )
                );
            }
        }
        finally
        {
            cursor.close();
        }

        if (favoritesList.size() == 0)
            Snackbar.make(
                    mRecyclerView, "Seems you haven't favored any movies yet",
                    Snackbar.LENGTH_LONG)
                    .show();

        mMoviesAdapter.clear();
        mMoviesAdapter.addAll(favoritesList);
        if (mRefreshMovies.isRefreshing())
        {
            mRefreshMovies.setRefreshing(false);
            Toast.makeText(getContext(), "Movies has been updated", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        chooseAdapter();
    }

    @Override
    public void onRefresh()
    {
        chooseAdapter();
    }

    private void chooseAdapter()
    {
        String currentSetting = prefs.getString(
                getString(R.string.prefs_sorting),
                getString(R.string.prefs_popular)
        );

        if (!isNetworkAvailable())
        {
            getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
            mCollapsingToolbar.setTitle("Favorites");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(
                    getString(R.string.prefs_sorting),
                    getString(R.string.prefs_favorites)
            );
            editor.apply();
            Snackbar.make(mRecyclerView, "You are offline; Browse your favorites instead.", 5500)
                    .setActionTextColor(Color.WHITE)
                    .setAction("Enable Wi-Fi", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            enableWifi();
                        }
                    })
                    .show();
        }
        else
        {
            if (currentSetting.equals(getString(R.string.prefs_favorites)))
            {
                getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
            } else
            {
                fetchMoviesVolley();
            }
        }
    }


    public boolean isNetworkAvailable()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean enableWifi()
    {
        WifiManager wifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.setWifiEnabled(true);
    }


    private void fetchMoviesVolley()
    {
        RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
        Uri uriBuilder = Uri.parse(Movie.BASE_URL)
                .buildUpon()
                .appendPath("movie")
                .appendPath(prefs.getString(getString(R.string.prefs_sorting), getString(R.string.prefs_popular)))
                .appendQueryParameter(Movie.API_kEY_QUERY, Movie.API_KEY)
                .build();

        JsonObjectRequest moviesRequest = new JsonObjectRequest(
                Request.Method.GET,
                uriBuilder.toString(),
                null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        ArrayList<Movie> moviesList = new ArrayList<>();
                        JSONArray results = null;
                        try
                        {
                            results = response.getJSONArray("results");
                            if (results != null)
                            {
                                for (int i = 0; i < results.length(); ++i)
                                {
                                    JSONObject singleMovie = results.getJSONObject(i);
                                    moviesList.add(
                                            new Movie(
                                                    singleMovie.getInt(Movie.MOVIE_ID),
                                                    getPosterURL(singleMovie.getString(Movie.POSTER_URL))
                                            )
                                    );
                                }
                            }
                            else
                            {
                                Log.d(TAG, "results array from JSON is empty!");
                            }

                            mMoviesAdapter.clear();
                            mMoviesAdapter.addAll(moviesList);
                            if (mRefreshMovies.isRefreshing())
                            {
                                mRefreshMovies.setRefreshing(false);
                                Toast.makeText(getContext(), "Movies has been updated", Toast.LENGTH_SHORT)
                                        .show();
                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d(TAG, "Failed to catch movies JSONObject" + error.getMessage());
                    }
                }
        );
        queue.add(moviesRequest);
    }

    private String getPosterURL(String posterPath)
    {
        return Uri.parse(Movie.IMAGE_BASE_URL).buildUpon()
                .appendPath(Movie.POSTER_SIZES[1])
                .appendEncodedPath(posterPath)
                .build().toString();

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        getLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    /*************************************************
     *          Loader callbacks                     *
     *************************************************/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new CursorLoader(
                getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        updateAdapterFromCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        updateAdapterFromCursor(null);
    }
}
