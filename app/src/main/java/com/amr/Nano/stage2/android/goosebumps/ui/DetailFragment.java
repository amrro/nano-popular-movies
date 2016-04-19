package com.amr.Nano.stage2.android.goosebumps.ui;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amr.Nano.stage2.android.goosebumps.R;
import com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters.Movie;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by amro on 4/16/16.
 */
public class DetailFragment extends Fragment
{

    private static final String TAG = DetailFragment.class.getSimpleName();
    private static final int SPAN_COUNT = 1;
    private int movieID;

    private RecyclerView.LayoutManager mLayoutManager;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.recycler_view_reviews)
    RecyclerView mReviewRecycler;

    // Linking views id to fields
    @Bind(R.id.backdrop_image_view)
    ImageView mBackdrop;

    @Bind(R.id.poster_image)
    ImageView mPoster;

    @Bind(R.id.rating_star)
    RatingBar mRatingBar;

    @Bind(R.id.overview_text_view)
    TextView mOverviewTextView;

    @Bind(R.id.rating_text)
    TextView mRatingTextView;

    @Bind(R.id.votes_text_view)
    TextView mVotesCountTextView;

    @Bind(R.id.realase_date_text)
    TextView mReleaseDateTextView;

    @Bind(R.id.duration_text)
    TextView mDurationTextView;

    @Bind(R.id.play_fab)
    FloatingActionButton mPlayFab;

    //TODO remember you can bind strings too using @BindString

    /*
     @OnClick(R.id.submit) void submit() {
        // TODO call server...
    }
    */


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);

        ((DetailActivity) getActivity()).setSupportActionBar(mToolbar);
        ((DetailActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        movieID = getActivity().getIntent().getExtras().getInt(Movie.MOVIE_ID);
        Log.d(TAG, "::: Recieved Movie ID is: "  + movieID);

        mReviewRecycler.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        mReviewRecycler.setLayoutManager(mLayoutManager);



        return rootView;
    }


    @Override
    public void onStart()
    {
        super.onStart();
        FetchMovieDataTask fetchTask = new FetchMovieDataTask();
        fetchTask.execute();
    }

    class FetchMovieDataTask extends AsyncTask<Void, Void, Void>
    {
        private  String movieJsonStr;

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            try
            {
                JSONObject movieData = new JSONObject(movieJsonStr);
//                getPosterURL(movieData.getString(Movie.BACKDROP_URL));

                Log.d(TAG, "backdrop url is: " + getBackdropURL(movieData.getString(Movie.BACKDROP_URL)));

                Glide
                    .with(getContext())
                    .load(getBackdropURL(movieData.getString(Movie.BACKDROP_URL)))
                    .into(mBackdrop);

                Glide
                    .with(getContext())
                    .load(getPosterURL(movieData.getString(Movie.POSTER_URL)))
                    .into(mPoster);

                mCollapsingToolbar.setTitle(movieData.getString(Movie.ORIGINAL_TITLE));
                mRatingTextView.setText(movieData.getString(Movie.VOTE_AVERAGE) + " /10");
                mVotesCountTextView.setText(movieData.getString(Movie.VOTE_COUNT));
                mOverviewTextView.setText(movieData.getString(Movie.OVERVIEW));
                mReleaseDateTextView.setText(movieData.getString(Movie.RELEASE_DATE));
                mDurationTextView.setText("Runtime: " + movieData.getString(Movie.RUNTIME));


            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }


        }

        @Override
        protected Void doInBackground(Void... params)
        {
            HttpsURLConnection httpsURLConnection = null;
            BufferedReader reader = null;
            movieJsonStr = null;
            try
            {
                Uri uriBuilder = Uri.parse(Movie.API_BASE_URL)
                        .buildUpon()
                        .appendPath("movie")
                        .appendPath(String.valueOf(movieID))
                        .appendQueryParameter(Movie.API_kEY_QUERY, Movie.API_KEY)
                        .build();


                URL url = new URL(uriBuilder.toString());

                Log.d(TAG, "movie url is " + String.valueOf(url));

                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.connect();


                // parsing input stream:
                InputStream input = httpsURLConnection.getInputStream();
                if (input == null)
                {
                    Log.d(TAG, "null InputStream.");
                    return null;
                }


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

                movieJsonStr = buffer.toString();


            }
            catch (MalformedURLException e)
            {
                Log.d(TAG, ":: ERROR in Building url.");
                e.printStackTrace();
            }
            catch (IOException e)
            {
                Log.d(TAG, "url.openConnection() has failed");
                e.printStackTrace();
            }

            return null;
        }

        private String getPosterURL(String posterPath)
        {
            return Uri.parse(Movie.IMAGE_BASE_URL).buildUpon()
                    .appendPath(Movie.POSTER_SIZES[1])
                    .appendEncodedPath(posterPath)
                    .build().toString();

        }

        private String getBackdropURL (String backdropPath)
        {
            return Uri.parse(Movie.IMAGE_BASE_URL)
                    .buildUpon()
                    .appendPath(Movie.BACKDROP_SIZES[0])
                    .appendEncodedPath(backdropPath)
                    .build().toString();
        }

    }
}
