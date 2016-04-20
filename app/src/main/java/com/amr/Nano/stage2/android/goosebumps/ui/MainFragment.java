package com.amr.Nano.stage2.android.goosebumps.ui;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amr.Nano.stage2.android.goosebumps.R;
import com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters.Movie;
import com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters.MoviesAdapter;

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

/**
 *
 * Created by amro on 4/16/16.
 */

public class MainFragment extends Fragment
{
    final private int SPAN_COUNT = 2;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MoviesAdapter mMoviesAdapter;
    private FetchMoviesTask mFetchMoviesTask;



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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mMoviesAdapter = new MoviesAdapter(getContext(), new ArrayList<Movie>());
        // improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mMoviesAdapter);

        mFetchMoviesTask = new FetchMoviesTask();
        mFetchMoviesTask.execute("popular");
        return rootView;
    }


    @Override
    public void onStart()
    {
        super.onStart();
        mFetchMoviesTask = new FetchMoviesTask();
        mFetchMoviesTask.execute("popular");
    }

    class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>>
    {

        private final String TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(String... params)
        {
            HttpsURLConnection httpsURLConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try
            {

                Uri uriBuilder = Uri.parse(Movie.BASE_URL)
                        .buildUpon()
                        .appendPath("movie")
                        .appendPath(params[0])
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



    class FetchReviewsTask extends AsyncTask<Void, Void, Void>
    {
        public final String TAG = FetchReviewsTask.class.getSimpleName();
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            return null;
        }
    }
}
