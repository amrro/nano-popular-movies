package com.amr.Nano.stage2.android.goosebumps.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import com.amr.Nano.stage2.android.goosebumps.R;
import com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters.Movie;
import com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters.MoviesAdapter;
import com.amr.Nano.stage2.android.goosebumps.database.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * Created by amro on 4/16/16.
 */

public class MainFragment extends Fragment
{
    final private int SPAN_COUNT = 2;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private SharedPreferences prefs;

    @Bind(R.id.main_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.main_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    private RecyclerView.LayoutManager mLayoutManager;
    private MoviesAdapter mMoviesAdapter;
    private FetchMoviesTask mFetchMoviesTask;
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

        prefs = getActivity()
                .getSharedPreferences(getString(R.string.prefs_sorting), Context.MODE_PRIVATE);

        ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
        mCollapsingToolbar.setTitle("Most Popular");

        mMoviesAdapter = new MoviesAdapter(getContext(), new ArrayList<Movie>(),MainActivity.mTwoPane);
        // improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mMoviesAdapter);

        mFetchMoviesTask = new FetchMoviesTask();
        mFetchMoviesTask.execute();
        return rootView;
    }

    private boolean isTwoPane()
    {
        SharedPreferences prefs = getContext().getSharedPreferences(getString(R.string.prefs_sorting), Context.MODE_PRIVATE);
        return prefs.getBoolean(DETAILFRAGMENT_TAG, true);
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
                mCollapsingToolbar.setTitle(getString(R.string.menu_popular));
                mRecyclerView.swapAdapter(mMoviesAdapter, false);
                new FetchMoviesTask().execute();

                editor.putString(
                        getString(R.string.prefs_sorting),
                        getString(R.string.prefs_popular)
                );
                editor.apply();
                return true;


            }
            case R.id.top_rated_item:
            {
                mCollapsingToolbar.setTitle(getString(R.string.menu_popular));
                mCollapsingToolbar.setTitle(getString(R.string.menu_top));
                new FetchMoviesTask().execute();

                editor.putString(
                        getString(R.string.prefs_sorting),
                        getString(R.string.prefs_top_rated)
                );
                editor.apply();

                mRecyclerView.swapAdapter(mMoviesAdapter, false);
                return true;
            }
            case R.id.favorites:
            {
                mCollapsingToolbar.setTitle("Favorites");
                updateAdapterFromCursor();
                editor.putString(
                        getString(R.string.prefs_sorting),
                        getString(R.string.prefs_favorites)
                );
                editor.apply();
                return true;
            }
            default:
        }
        return super.onOptionsItemSelected(item);
    }



    private void updateAdapterFromCursor()
    {
        Cursor cursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

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
    }

    @Override
    public void onStart()
    {
        super.onStart();
        String currentSetting = prefs.getString(
                getString(R.string.prefs_sorting),
                getString(R.string.prefs_popular)
        );

        if (currentSetting == getString(R.string.prefs_favorites))
        {
            updateAdapterFromCursor();
        }
        else
        {
            mFetchMoviesTask = new FetchMoviesTask();
            mFetchMoviesTask.execute();
        }
    }



    class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>>
    {

        private final String TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(Void... params)
        {
            HttpsURLConnection httpsURLConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;
            try
            {
                Log.d(TAG, "preference is set to: " +
                        prefs.getString(getString(R.string.prefs_sorting), getString(R.string.prefs_popular)));
                Uri uriBuilder = Uri.parse(Movie.BASE_URL)
                        .buildUpon()
                        .appendPath("movie")
                        .appendPath(prefs.getString(getString(R.string.prefs_sorting), getString(R.string.prefs_popular)))
                        .appendQueryParameter(Movie.API_kEY_QUERY, Movie.API_KEY)
                        .build();


                URL url = new URL(uriBuilder.toString());

                // create request and open connection with TheMoviesDB:
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.connect();


                // Read input stream::
                //   >>>
                InputStream input = httpsURLConnection.getInputStream();
                if (input == null)
                    return null;

                reader = new BufferedReader(new InputStreamReader(input));

                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line + "\n");

                if (buffer.length() == 0)
                {
                    Log.d(TAG, "    empty buffer!");
                    return null;
                }

                // finally getting jason string
                moviesJsonStr = buffer.toString();
                //  <<<
            } catch (MalformedURLException e)
            {
                Log.e(TAG, "Invalid URL" + e.getMessage());
            } catch (IOException e)
            {
                Log.e(TAG, "error! can't open connection" + e.getMessage());
            } finally
            {
                if (httpsURLConnection != null)
                    httpsURLConnection.disconnect();

                if (reader != null)
                    try
                    {
                        reader.close();
                    } catch (IOException e)
                    {
                        Log.e(TAG, "error closing BufferedReader! " + e.getMessage());
                    }
            }


            try
            {
                return getMoviesFromJson(moviesJsonStr);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        private ArrayList<Movie> getMoviesFromJson(String jsonUrl)
                throws JSONException
        {
            ArrayList<Movie> retList = new ArrayList<>();


            JSONObject list = new JSONObject(jsonUrl);
            JSONArray results = list.getJSONArray("results");

            for (int i = 0; i < results.length(); ++i)
            {
                JSONObject singleMovie = results.getJSONObject(i);
                retList.add(
                        new Movie(
                                singleMovie.getInt(Movie.MOVIE_ID),
                                getPosterURL(singleMovie.getString(Movie.POSTER_URL))
                        )
                );
            }
            return  retList;
        }

        private String getPosterURL(String posterPath)
        {
            return Uri.parse(Movie.IMAGE_BASE_URL).buildUpon()
                    .appendPath(Movie.POSTER_SIZES[1])
                    .appendEncodedPath(posterPath)
                    .build().toString();

        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies)
        {
            if (movies != null)
            {
                Log.d(TAG, String.valueOf(movies.get(0).getID()));
                Log.d(TAG, movies.get(0).getPosterURL());
                mMoviesAdapter.clear();
                mMoviesAdapter.addAll(movies);
            }
        }
    }

}
