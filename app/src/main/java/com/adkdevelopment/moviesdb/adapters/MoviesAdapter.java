/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016. Dmytro Karataiev
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.adkdevelopment.moviesdb.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.adkdevelopment.moviesdb.R;
import com.adkdevelopment.moviesdb.interfaces.ItemClickListener;
import com.adkdevelopment.moviesdb.model.MovieObject;
import com.adkdevelopment.moviesdb.utils.DatabaseTasks;
import com.adkdevelopment.moviesdb.utils.Utility;

/**
 * Adapter with MovieObjects
 * Created by karataev on 12/14/15.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private final List<MovieObject> mValues;
    private final Context mContext;
    private ItemClickListener<MovieObject, View> mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_poster) ImageView mImagePoster;
        @BindView(R.id.movie_poster_favorite) ImageView mImageFavorite;
        @BindView(R.id.movie_item_spinner) ProgressBar mProgressSpinner;
        @BindView(R.id.movie_poster_text) TextView mPosterText;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPosterText.getText();
        }
    }

    public void setData(ItemClickListener<MovieObject, View> listener) {
        mListener = listener;
    }

    public MoviesAdapter(Context context, List<MovieObject> items) {
        mValues = items;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final MovieObject movieObject = mValues.get(position);

        holder.itemView.setOnClickListener(v -> {
            // Set a tag for a shared transition
            holder.mImagePoster.setTag(movieObject.getId());
            holder.mImageFavorite.setTag(movieObject.getId() + "1");
            if (mListener != null) {
                mListener.onItemClicked(movieObject, holder.itemView);
            }
        });

        Picasso.with(holder.mImagePoster.getContext()).load(mValues.get(position).getPosterPath()).into(holder.mImagePoster);

        // set favorites icon
        if (Utility.isFavorite(holder.mImageFavorite.getContext(), movieObject)) {
            //holder.mImageFavorite.setImageResource(R.drawable.ic_bookmark_fav);
            Picasso.with(holder.mImageFavorite.getContext()).load(R.drawable.ic_bookmark_fav).into(holder.mImageFavorite);
        } else {
            //holder.mImageFavorite.setImageResource(R.drawable.ic_bookmark);
            Picasso.with(holder.mImageFavorite.getContext()).load(R.drawable.ic_bookmark).into(holder.mImageFavorite);
        }

        // Scale posters correctly
        holder.mImagePoster.getLayoutParams().height = Utility.screenSize(mContext)[5];
        holder.mProgressSpinner.getLayoutParams().height = Utility.screenSize(mContext)[5];
        holder.mImageFavorite.getLayoutParams().height = (int) Math.round(Utility.screenSize(mContext)[5] * 0.2);

        holder.mProgressSpinner.setVisibility(View.VISIBLE);
        holder.mImageFavorite.setVisibility(View.GONE);

        Picasso.with(mContext).load(movieObject.getPosterPath()).into(holder.mImagePoster, new Callback() {
            @Override
            public void onSuccess() {
                holder.mProgressSpinner.setVisibility(View.GONE);
                holder.mImageFavorite.setVisibility(View.VISIBLE);

                // On mImageFavorite icon click
                holder.mImageFavorite.setOnClickListener(v -> {
                    ContentValues favValue = Utility.makeContentValues(movieObject);

                    Toast.makeText(mContext, movieObject.getTitle(), Toast.LENGTH_LONG).show();

                    if (!Utility.isFavorite(mContext, movieObject)) {

                        // Save drawable for later usage
                        //byte[] bitmapData = Utility.makeByteArray(holder.mImagePoster.getDrawable());

                        // save byte array of an image to the database
                        //favValue.put(MoviesContract.MovieEntry.COLUMN_IMAGE, bitmapData);

                        //holder.mImageFavorite.setImageResource(R.drawable.ic_bookmark_fav);
                        Picasso.with(holder.mImageFavorite.getContext())
                                .load(R.drawable.ic_bookmark_fav)
                                .into(holder.mImageFavorite);

                        // Insert on background thread
                        DatabaseTasks databaseTasks = new DatabaseTasks(mContext);
                        databaseTasks.execute(DatabaseTasks.INSERT, favValue);
                    } else {
                        //holder.mImageFavorite.setImageResource(R.drawable.ic_bookmark);
                        Picasso.with(holder.mImageFavorite.getContext())
                                .load(R.drawable.ic_bookmark)
                                .into(holder.mImageFavorite);

                        // Delete on background thread
                        DatabaseTasks databaseTasks = new DatabaseTasks(mContext);
                        databaseTasks.execute(DatabaseTasks.DELETE, favValue);
                    }
                });
            }

            @Override
            public void onError() {
                holder.mImagePoster.setBackgroundResource(R.color.white);
                holder.mProgressSpinner.setVisibility(View.GONE);
                holder.mImageFavorite.setVisibility(View.GONE);
            }
        });

        // If movie doesn't have an image - uses text instead
        if (movieObject.getPosterPath().contains("null")) {
            holder.mPosterText.setText(movieObject.getTitle());
        } else {
            holder.mPosterText.setText("");
        }
        holder.mImagePoster.setContentDescription(movieObject.getTitle());

    }

    @Override
    public int getItemCount() {
        return mValues != null ? mValues.size() : 0;
    }



}
