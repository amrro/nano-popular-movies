package com.amr.Nano.stage2.android.goosebumps.network;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by amro on 4/19/16.
 */
public class VolleySingleton
{
    private static VolleySingleton ourInstance = new VolleySingleton();
    private RequestQueue mRequestQueue;

    public static VolleySingleton getInstance()
    {
        if (ourInstance == null)
            ourInstance = new VolleySingleton();
        return ourInstance;
    }


    /***
     * cons: to instantiate the RequestQueue
     */
    private VolleySingleton()
    {
        mRequestQueue = Volley.newRequestQueue(MyApplication.getMyAppContext());
    }

    /**
     * get the RequestQueue that all Requests will be enqueued all over the app.
     * @return single RequestQueue instance all over the app.
     */

    public RequestQueue getRequestQueue()
    {
        return this.mRequestQueue;
    }
}
