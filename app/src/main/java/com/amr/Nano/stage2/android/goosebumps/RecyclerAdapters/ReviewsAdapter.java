package com.amr.Nano.stage2.android.goosebumps.RecyclerAdapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amr.Nano.stage2.android.goosebumps.R;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by amro on 4/19/16.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder>
{
    private Context mContext;
    private ArrayList<Review> mReviewsSet;

    public ReviewsAdapter(Context context, ArrayList<Review> reviewsSet)
    {
        mContext = context;
        mReviewsSet = reviewsSet;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_card_item, parent, false);

        ReviewHolder reviewHolder = new ReviewHolder(cardView);
        return reviewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position)
    {
        holder.userName.setText(mReviewsSet.get(position).getUserName());
        holder.userReview.setText(mReviewsSet.get(position).getUserReview());
    }

    @Override
    public int getItemCount()
    {
        return mReviewsSet.size();
    }

    public void clear()
    {
        mReviewsSet.clear();
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends Review> reviews)
    {
        mReviewsSet.addAll(reviews);
        notifyDataSetChanged();
    }




    public class ReviewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.user_name_text)
        TextView userName;

        @Bind(R.id.user_review_text)
        TextView userReview;


        public ReviewHolder(CardView cardView)
        {
            super(cardView);

            ButterKnife.bind(this, itemView);
        }
    }
}
