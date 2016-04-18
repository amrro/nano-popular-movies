package com.amr.Nano.stage2.android.goosebumps.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amr.Nano.stage2.android.goosebumps.R;

/**
 * Created by amro on 4/16/16.
 */
public class DetailActivity extends AppCompatActivity
{

    @Override
    protected void onCreate( Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
            .replace(R.id.detail_container, new DetailFragment())
            .commit();
    }
}
