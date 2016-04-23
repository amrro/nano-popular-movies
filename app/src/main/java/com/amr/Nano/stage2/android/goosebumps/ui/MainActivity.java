package com.amr.Nano.stage2.android.goosebumps.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amr.Nano.stage2.android.goosebumps.R;
import com.amr.Nano.stage2.android.goosebumps.database.MovieContract;

public class MainActivity extends AppCompatActivity
{
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpSharedPreferences();

        if (findViewById(R.id.fragment_details_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle bundle = new Bundle();
            bundle.putInt(MovieContract.MovieEntry.COL_MOVIE_ID, 550);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_details_container, detailFragment, DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    }

    private void setUpSharedPreferences()
    {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_sorting), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.prefs_sorting),getString(R.string.prefs_popular));
        editor.putBoolean(DETAILFRAGMENT_TAG, mTwoPane);
        editor.commit();
    }

    public static boolean getPane(){
        Log.i("pane",mTwoPane+"");
        return mTwoPane;
    }
}
