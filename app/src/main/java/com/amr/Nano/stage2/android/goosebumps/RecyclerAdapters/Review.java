package com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters;

/**
 * Created by amro on 4/19/16.
 */
public class Review
{
    private String userName;
    private String userReview;

    public Review(String userName, String userReview)
    {
        this.userName = userName;
        this.userReview = userReview;
    }



    public String getUserName()
    {
        return userName;
    }

    public String getUserReview()
    {
        return userReview;
    }
}
