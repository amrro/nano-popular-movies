package com.amr.Nano.stage2.android.goosebumps.ui;

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

        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, new MainFragment())
                    .commit();
        }
    }
}
