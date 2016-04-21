package com.amr.Nano.stage2.android.goosebumps.CursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amr.Nano.stage2.android.goosebumps.R;
import com.amr.Nano.stage2.android.goosebumps.database.MovieContract;
import com.bumptech.glide.Glide;

/**
 * Created by amro on 4/21/16.
 */
public class MovieCursorAdapter extends CursorRecyclerViewAdapter<MovieCursorAdapter.MovieHolder>
{
    private Context mContext;
    public MovieCursorAdapter(Context context, Cursor cursor)
    {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(MovieHolder viewHolder, Cursor cursor)
    {
        int columnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COL_POSTER_URL);
        String posterUrl = cursor.getString(columnIndex);
        Glide
                .with(mContext)
                .load(posterUrl)
                .into(viewHolder.posterView);

    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);
        return new MovieHolder(itemView);
    }


    public class MovieHolder extends RecyclerView.ViewHolder
    {
        public ImageView posterView;

        public MovieHolder(View itemView)
        {
            super(itemView);
            posterView = (ImageView) itemView.findViewById(R.id.poster_item);
        }
    }
}
