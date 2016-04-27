package com.amr.Nano.stage2.android.goosebumps.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amr.Nano.stage2.android.goosebumps.R;
import com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters.Movie;
import com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters.Review;
import com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters.ReviewsAdapter;
import com.amr.Nano.stage2.android.goosebumps.database.MovieContract;
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

import java.util.ArrayList;

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

    private ContentValues mMovieValues;

    private int movieID;
    private RecyclerView.LayoutManager mLayoutManager;
    private ReviewsAdapter mReviewsAdapter;

    private RequestQueue mRequestQueue;

    @Bind(R.id.recycler_view_reviews)
    RecyclerView mReviewRecycler;

    @Bind(R.id.details_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @Bind(R.id.main_toolbar)
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

    @Bind(R.id.favorite_fab)
    FloatingActionButton mFavoriteFab;

    @Bind(R.id.play_trailer)
    ImageButton mPlayButton;

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

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        movieID = (int) getArguments().get(MovieContract.MovieEntry.COL_MOVIE_ID);

        Log.d(TAG, "::: Recieved Movie ID is: " + movieID);

        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        updateFragment(movieID);
        mReviewRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mReviewRecycler.setLayoutManager(mLayoutManager);

        mReviewsAdapter = new ReviewsAdapter(getContext(), new ArrayList<Review>());

        mReviewRecycler.setAdapter(mReviewsAdapter);


        setPlayFab();

        // called this method cuz I have to wait for a moment before tapping playFAB.
        getYouTubeKey();
        mFavoriteFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                ContentResolver resolver = getContext().getContentResolver();

                /**
                 * in case the movie is already in favorites:
                 */
                if (mFavoriteFab.isSelected())
                {
                    resolver.delete(
                            MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COL_MOVIE_ID + "=?",
                            new String[] {(String) mMovieValues.get(MovieContract.MovieEntry.COL_MOVIE_ID)}
                    );

                    Snackbar.make(
                            view, "Deleting from your favorites.",
                            Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.RED)
                            .setAction("UNDO", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    getContext().getContentResolver().insert(
                                            MovieContract.MovieEntry.CONTENT_URI,
                                            mMovieValues
                                    );
                                    setPlayFab();
                                }
                            })
                            .show();

                    /**
                     * mFavoriteFab.setSelected(false) could be used
                     * instead, I used setPlayFab() to make sure that the entry really deleted
                     * Consequently, mFavoriteFab depends on the database.
                     */

                    setPlayFab();
                }

                /**
                 * in case the movie is not in favorites
                 */
                else
                {
                    resolver.insert(MovieContract.MovieEntry.CONTENT_URI, mMovieValues);

                    Snackbar.make(
                            view, "Adding to your favorites.",
                            Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    getContext().getContentResolver().delete(
                                            MovieContract.MovieEntry.CONTENT_URI,
                                            MovieContract.MovieEntry.COL_MOVIE_ID + "=?",
                                            new String[]{(String) mMovieValues.get(MovieContract.MovieEntry.COL_MOVIE_ID)}
                                    );
                                    setPlayFab();
                                }
                            })
                            .setActionTextColor(Color.GREEN)
                            .show();
                    setPlayFab();
                }

            }
        });


        mPlayButton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        Log.d(TAG, "movie id is : " + movieID);
                        // getting youtube url:
                        if (getYouTubeKey() != null)
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + getYouTubeKey())));
                        else
                            Snackbar.make(v, "No trailer available.", Snackbar.LENGTH_LONG)
                                    .show();
                    }
                }
        );

        return rootView;
    }



    public void updateFragment(int newID)
    {
        movieID = newID;
        mMovieValues = new ContentValues();
        ContentResolver resolver= getActivity().getContentResolver();
        Cursor cursor = resolver.query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COL_MOVIE_ID + "=?",
                new String[]{String.valueOf(movieID)},
                null
        );

        try
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String sorting = prefs.getString(getString(R.string.prefs_sorting),getString(R.string.prefs_popular));
            assert cursor != null;
            if (cursor.getCount() == 1
                    && cursor.moveToNext() && sorting.equals(getString(R.string.prefs_favorites))
                    && !isNetworkAvailable())
            {
                DatabaseUtils.cursorRowToContentValues(cursor, mMovieValues);
                updateViews();
            }
            else
            {
//                new FetchMovieDataTask().execute();
                fetchMovieVolley();
                updateReviewsAdapter();
            }
        }
        finally
        {
            if (cursor != null)
                cursor.close();
        }

    }

    /***
     * Helper method to set the selection state of the fab (.setSelected()) from (selector<></>)
     * this method makes the the selection state depend on the database.
     */
    private void setPlayFab()
    {
        ContentResolver resolver = getContext().getContentResolver();
        Cursor cursor = resolver.query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COL_MOVIE_ID},
                MovieContract.MovieEntry.COL_MOVIE_ID + "=?",
                new String[]{String.valueOf(movieID)},
                null
        );

        try
        {
            if (cursor == null)
                Log.d(TAG, "Cursor is null, check again!!");

            assert cursor != null;
            if (cursor.getCount() > 0)
            {
                mFavoriteFab.setSelected(true);
            }
            else
                mFavoriteFab.setSelected(false);
        }
        finally
        {
            assert cursor != null;
            cursor.close();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.details_fragment_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = new ShareActionProvider(getContext());
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
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
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
        Log.d("USER_PROFILE", "" + reviewsUri.toString());

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
        if (mRequestQueue == null)
            Log.d(TAG, "request queue is null");

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

        Log.d(TAG, "youtube uri is: " + YouTubeUri.toString());

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
                        Log.d(TAG, "FAILED to get jsonObjet movie" + error.getMessage());
                    }
                }
        );

        mRequestQueue.add(YouTube);
        Log.d(TAG, "youtube link is : " + mYouTubeKey);
        return mYouTubeKey;
    }


    private void updateViews()
    {
        Log.d(TAG, "poster url: " + mMovieValues.get(MovieContract.MovieEntry.COL_BACKDROP_URL));
        Glide
                .with(getContext())
                .load(mMovieValues.get(MovieContract.MovieEntry.COL_BACKDROP_URL))
                .centerCrop()
                .into(mBackdrop);

        Glide
                .with(getContext())
                .load(mMovieValues.get(MovieContract.MovieEntry.COL_POSTER_URL))
                .into(mPoster);

        mCollapsingToolbar.setTitle((CharSequence) mMovieValues.get(MovieContract.MovieEntry.COL_ORIGINAL_TITLE));
        String rating =  mMovieValues.get(MovieContract.MovieEntry.COL_VOTE_AVERAGE).toString();
        mRatingTextView
                .setText(rating.toString() + " /10");

        mRatingBar.setRating(Float.parseFloat(rating.toString()) / 10);

        String voteCount = mMovieValues.get(MovieContract.MovieEntry.COL_VOTE_COUNT).toString();
        mVotesCountTextView.setText("" + voteCount);
        mOverviewTextView.setText((String) mMovieValues.get(MovieContract.MovieEntry.COL_OVERVIEW));

        mReleaseDateTextView.setText(""+ mMovieValues.get(MovieContract.MovieEntry.COL_RELEASE_DATE));
        mDurationTextView.setText("Runtime: " + mMovieValues.get(MovieContract.MovieEntry.COL_RUNTIME));
    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @Override
    public void onStart()
    {
        super.onStart();
        mMovieValues = new ContentValues();
        updateFragment(movieID);
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


    private void fetchMovieVolley()
    {
        Uri uriBuilder = Uri.parse(Movie.BASE_URL)
                .buildUpon()
                .appendPath("movie")
                .appendPath(String.valueOf(movieID))
                .appendQueryParameter(Movie.API_kEY_QUERY, Movie.API_KEY)
                .build();

        JsonObjectRequest movieRequest = new JsonObjectRequest(
                Request.Method.GET,
                uriBuilder.toString(),
                null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            fillMovieValues(response);
                            updateViews();
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
        mRequestQueue.add(movieRequest);
    }

    private void fillMovieValues(JSONObject movieData) throws JSONException
    {
        mMovieValues.put(
                MovieContract.MovieEntry.COL_POSTER_URL,
                getPosterURL(movieData.getString(MovieContract.MovieEntry.COL_POSTER_URL)
                )
        );

        mMovieValues.put(MovieContract.MovieEntry.COL_IS_ADULT,
                movieData.getString(MovieContract.MovieEntry.COL_IS_ADULT)
        );

        mMovieValues.put(
                MovieContract.MovieEntry.COL_OVERVIEW,
                movieData.getString(MovieContract.MovieEntry.COL_OVERVIEW)
        );

        mMovieValues.put(
                MovieContract.MovieEntry.COL_RELEASE_DATE,
                movieData.getString(MovieContract.MovieEntry.COL_RELEASE_DATE)
        );

        mMovieValues.put(
                MovieContract.MovieEntry.COL_MOVIE_ID,
                movieData.getString(MovieContract.MovieEntry.COL_MOVIE_ID)
        );

        mMovieValues.put(
                MovieContract.MovieEntry.COL_ORIGINAL_TITLE,
                movieData.getString(MovieContract.MovieEntry.COL_ORIGINAL_TITLE)
        );

        mMovieValues.put(
                MovieContract.MovieEntry.COL_BACKDROP_URL,
                getBackdropURL(movieData.getString(MovieContract.MovieEntry.COL_BACKDROP_URL))
        );

        mMovieValues.put(
                MovieContract.MovieEntry.COL_POPULARITY,
                movieData.getDouble(MovieContract.MovieEntry.COL_POPULARITY)
        );

        mMovieValues.put(
                MovieContract.MovieEntry.COL_VOTE_COUNT,
                movieData.getInt(MovieContract.MovieEntry.COL_VOTE_COUNT)
        );

        mMovieValues.put(
                MovieContract.MovieEntry.COL_VOTE_AVERAGE,
                movieData.getDouble(MovieContract.MovieEntry.COL_VOTE_AVERAGE)
        );

        mMovieValues.put(
                MovieContract.MovieEntry.COL_RUNTIME,
                movieData.getInt(MovieContract.MovieEntry.COL_RUNTIME)
        );
    }

    private String getPosterURL(String posterPath)
    {
        return Uri.parse(Movie.IMAGE_BASE_URL).buildUpon()
                .appendPath(Movie.POSTER_SIZES[1])
                .appendEncodedPath(posterPath)
                .build().toString();

    }

    private String getBackdropURL(String backdropPath)
    {
        return Uri.parse(Movie.IMAGE_BASE_URL)
                .buildUpon()
                .appendPath(Movie.BACKDROP_SIZES[0])
                .appendEncodedPath(backdropPath)
                .build().toString();
    }
}
