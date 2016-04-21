package com.amr.Nano.stage2.android.goosebumps.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amr.Nano.stage2.android.goosebumps.R;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpSharedPreferences();
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, new MainFragment())
                    .commit();
        }
    }

    private void setUpSharedPreferences()
    {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_sorting), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.prefs_sorting),getString(R.string.prefs_popular));
        editor.apply();
    }
}
