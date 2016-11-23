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

package com.adkdevelopment.moviesdb.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adkdevelopment.moviesdb.R;
import com.adkdevelopment.moviesdb.data.model.Consts;
import com.adkdevelopment.moviesdb.data.model.MovieCast;
import com.adkdevelopment.moviesdb.data.model.MovieCredits;
import com.adkdevelopment.moviesdb.ui.interfaces.ItemClickListener;
import com.adkdevelopment.moviesdb.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter which shows actors and their names in a recyclerview
 * Created by karataev on 5/8/16.
 */
public class ActorsAdapter extends RecyclerView.Adapter<ActorsAdapter.ViewHolder> {

    private final MovieCredits mMovieCredits;
    private final Context mContext;
    private ItemClickListener<MovieCast, View> mListener;

    public void setData(ItemClickListener<MovieCast, View> listener) {
        mListener = listener;
    }

    public ActorsAdapter(Context context, MovieCredits movieCredits) {
        super();
        mContext = context;
        mMovieCredits = movieCredits;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.actor_photo)
        ImageView mImageActor;
        @BindView(R.id.actor_name)
        TextView mTextActor;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.actors_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        MovieCast movieCast = mMovieCredits.getCast().get(position);

        Picasso.with(mContext)
                .load(Consts.IMAGE_URL + Consts.ACTOR_THUMB + movieCast.getProfilePath())
                .error(R.drawable.ic_people_white)
                .noFade()
                .transform(new CircleTransform())
                .into(holder.mImageActor);
        holder.mTextActor.setText(movieCast.getName().replace(" ", "\n"));
        holder.mImageActor.setContentDescription(movieCast.getName());

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClicked(movieCast, holder.itemView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovieCredits != null ? mMovieCredits.getCast().size() : 0;
    }

}
