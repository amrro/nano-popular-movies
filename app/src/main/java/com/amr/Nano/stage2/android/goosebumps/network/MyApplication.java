package com.amr.Nano.stage2.android.goosebumps.network;

import android.app.Application;
import android.content.Context;

/**
 * Created by amro on 4/19/16.
 */
public class MyApplication extends Application
{
    private static MyApplication mInstance;
    private static Context mContext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance = this;
        this.setMyAppContext(getApplicationContext());
    }

    public static MyApplication getInstance()
    {
        return mInstance;
    }

    public static Context getMyAppContext()
    {
        return mContext;
    }

    public void setMyAppContext(Context appContext)
    {
        mContext = appContext;
    }

}
