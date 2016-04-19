package com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters;

/**
 * Created by amro on 4/19/16.
 */
public class Review
{
    private final String userName;
    private final String userProfileImageURL;
    private final String userReview;

    public Review(String userName, String userProfileImage, String userReview)
    {
        this.userName = userName;
        this.userProfileImageURL = userProfileImage;
        this.userReview = userReview;
    }


    public String getUserName()
    {
        return userName;
    }

    public String getUserProfileImageURL()
    {
        return userProfileImageURL;
    }

    public String getUserReview()
    {
        return userReview;
    }
}
