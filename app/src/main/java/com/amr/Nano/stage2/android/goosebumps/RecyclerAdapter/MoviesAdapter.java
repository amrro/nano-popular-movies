package com.amr.Nano.stage2.android.goosebumps.RecyclerAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amr.Nano.stage2.android.goosebumps.R;
import com.amr.Nano.stage2.android.goosebumps.ui.Movie;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by amro on 4/16/16.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder>
{
    private ArrayList<Movie> mMoviesSet;
    private Context mContext;


    // provide the adapter with data


    public MoviesAdapter(Context context, ArrayList<Movie> moviesSet)
    {
        mContext = context;
        mMoviesSet = moviesSet;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);

        MovieHolder movieHolder = new MovieHolder((ImageView) v);
        return movieHolder;
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position)
    {
        Glide
            .with(mContext)
            .load(mMoviesSet.get(position).getPosterURL())
            .into(holder.posterView);
    }

    @Override
    public int getItemCount()
    {
        return mMoviesSet.size();
    }

    public void clear()
    {
        mMoviesSet.clear();
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends Movie> movies)
    {
        mMoviesSet.addAll(movies);
    }

    public class MovieHolder extends RecyclerView.ViewHolder
    {
        public ImageView posterView;
        public MovieHolder(ImageView image)
        {
            super(image);
            posterView = image;
        }
    }
}
