package com.amr.Nano.stage2.android.goosebumps.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amr.Nano.stage2.android.goosebumps.R;
import com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters.Movie;
import com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters.Review;
import com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters.ReviewsAdapter;
import com.amr.Nano.stage2.android.goosebumps.network.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

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
 * Created by amro on 4/16/16.
 */
public class DetailFragment extends Fragment
{

    private static final String TAG = DetailFragment.class.getSimpleName();
    private static final int SPAN_COUNT = 1;
    // boolean flag if(review total_results == 0) mIsReviews = false;
    private boolean mIsReviews;
    private String mYouTubeKey;
    private ShareActionProvider mShareActionProvider;

    private int movieID;
    private RecyclerView.LayoutManager mLayoutManager;
    private ReviewsAdapter mReviewsAdapter;

    private RequestQueue mRequestQueue;

    @Bind(R.id.recycler_view_reviews)
    RecyclerView mReviewRecycler;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

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
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);

        ((DetailActivity) getActivity()).setSupportActionBar(mToolbar);
        ((DetailActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        movieID = getActivity().getIntent().getExtras().getInt(Movie.MOVIE_ID);
        Log.d(TAG, "::: Recieved Movie ID is: "  + movieID);

        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

//        mReviewRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mReviewRecycler.setLayoutManager(mLayoutManager);

        mReviewsAdapter = new ReviewsAdapter(getContext(), new ArrayList<Review>());
        updateReviewsAdapter();
        mReviewRecycler.setAdapter(mReviewsAdapter);

        // called this method cuz I have to wait for a moment before tapping playFAB.
        getYouTubeKey();
        mPlayFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "movie id is : " + movieID);
                // getting youtube url:
                if (getYouTubeKey() != null)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + getYouTubeKey())));
                else
                    Snackbar.make(view, "No trailer available.", Snackbar.LENGTH_LONG)
                        .show();
            }
        });

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.details_fragment_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = new ShareActionProvider(getContext());
//                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        mShareActionProvider.setShareIntent(createShareMovieIntent());
        MenuItemCompat.setActionProvider(menuItem, mShareActionProvider);
        if (mShareActionProvider != null)
        {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        } else
        {
            Log.d(TAG, "Share Action Provider is null?");
        }



    }

    private Intent createShareMovieIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "check out this movie, \n" + "https://www.youtube.com/watch?v=" + mYouTubeKey);
        return shareIntent;
    }

    private void updateReviewsAdapter()
    {
        Uri reviewsUri = Uri.parse(Movie.BASE_URL)
                .buildUpon()
                .appendPath("movie")
                .appendPath(String.valueOf(movieID))
                .appendPath("reviews")
                .appendQueryParameter(Movie.API_kEY_QUERY, Movie.API_KEY)
                .build();

        // working perfectly:
//        Log.d("USER_PROFILE", "" + reviewsUri.toString());

        JsonObjectRequest jsonReviewRequest = new JsonObjectRequest(
                Request.Method.GET,
                reviewsUri.toString(),
                null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        ArrayList<Review> reviewsToAdapter = new ArrayList<>();

                        try
                        {
                            if (!(Integer.parseInt(response.getString("total_results")) > 0))
                            {
                                Log.d("REVIEWS_COUNT", "THERE IS NO REVIEWS");
                                mIsReviews = false;
                                return;
                            }

                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++)
                            {
                                JSONObject review = results.getJSONObject(i);
                                reviewsToAdapter.add(
                                        new Review(
                                                review.getString("author"),
                                                review.getString("content")
                                        )
                                );
                            }
                            mReviewsAdapter.clear();
                            mReviewsAdapter.addAll(reviewsToAdapter);
                            mIsReviews = true;

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

                    }
                }
        );

        mRequestQueue.add(jsonReviewRequest);
    }


    public String getYouTubeKey()
    {
        final Uri YouTubeUri = Uri.parse(Movie.BASE_URL)
                .buildUpon()
                .appendPath("movie")
                .appendPath(String.valueOf(movieID))
                .appendPath("videos")
                .appendQueryParameter(Movie.API_kEY_QUERY, Movie.API_KEY)
                .build();

        Log.d(TAG, "youtube uri is: "+ YouTubeUri.toString());

        JsonObjectRequest YouTube = new JsonObjectRequest(
                Request.Method.GET,
                YouTubeUri.toString(),
                null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            JSONArray results = response.getJSONArray("results");
                            if (true)
                            {
                                mYouTubeKey = results.getJSONObject(0).getString("key");
                                /*
                                mYouTubeKey = Uri.parse("http://www.youtube.com/watch")
                                        .buildUpon()
                                        .appendQueryParameter("v", results.getJSONObject(0).getString("key"))
                                        .build().toString();
                                Log.d(TAG, "youtube link is: " + mYouTubeKey);
                                Log.d(TAG, "youtube key is: " + results.getJSONObject(0).getString("key"));
                                */
                            }
                        }
                        catch (JSONException e)
                        {
                            Log.d(TAG, "Failed to fetch Json results");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                    }
                }
        );

        mRequestQueue.add(YouTube);
        Log.d(TAG, "youtube link is : " + mYouTubeKey);
        return mYouTubeKey;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FetchMovieDataTask fetchTask = new FetchMovieDataTask();
        fetchTask.execute();
        updateReviewsAdapter();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mRequestQueue.cancelAll(
                new RequestQueue.RequestFilter()
                {
                    @Override
                    public boolean apply(Request<?> request)
                    {
                        return true;
                    }
                }
        );
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
                Uri uriBuilder = Uri.parse(Movie.BASE_URL)
                        .buildUpon()
                        .appendPath("movie")
                        .appendPath(String.valueOf(movieID))
                        .appendQueryParameter(Movie.API_kEY_QUERY, Movie.API_KEY)
                        .build();


                URL url = new URL(uriBuilder.toString());

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
