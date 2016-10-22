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
import com.adkdevelopment.moviesdb.data.model.ActorCast;
import com.adkdevelopment.moviesdb.data.model.ActorCredits;
import com.adkdevelopment.moviesdb.data.model.Consts;
import com.adkdevelopment.moviesdb.ui.interfaces.ItemClickListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter which shows actors and their names in a recyclerview
 * Created by karataev on 5/8/16.
 */
public class ThumbnailsAdapter extends RecyclerView.Adapter<ThumbnailsAdapter.ViewHolder> {

    private ActorCredits mActorsCredits;
    private final Context mContext;
    private ItemClickListener<String, View> mListener;

    public void setData(ItemClickListener<String, View> listener, ActorCredits actorCredits) {
        mListener = listener;
        mActorsCredits = actorCredits;
    }

    public ThumbnailsAdapter(Context context) {
        super();
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_image) ImageView mImageMovie;
        @BindView(R.id.movie_title) TextView mTextTitle;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item_small, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ActorCast actorCast = mActorsCredits.getCast().get(position);

        Picasso.with(mContext)
                .load(Consts.IMAGE_URL + Consts.ACTOR_THUMB + actorCast.getPosterPath())
                .into(holder.mImageMovie);
        holder.mTextTitle.setText(actorCast.getTitle());

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClicked(String.valueOf(actorCast.getId()), holder.itemView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mActorsCredits != null ? mActorsCredits.getCast().size() : 0;
    }

}
