/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2017. Dmytro Karataiev
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

package com.adkdevelopment.moviesdb.feat_series.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adkdevelopment.moviesdb.R
import com.adkdevelopment.moviesdb.data.model.Consts
import com.adkdevelopment.moviesdb.data.model.TvObject
import com.adkdevelopment.moviesdb.feat_series.viewholders.TvSeriesViewHolder
import com.adkdevelopment.moviesdb.ui.interfaces.ItemClickListener
import com.adkdevelopment.moviesdb.utils.Utility
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/**
 * Adapter which shows actors and their names in a recyclerview
 * Created by karataev on 5/8/16.
 */
class TvAdapter(private val mContext: Context,
                private var mTvResults: MutableList<TvObject>?,
                private val mListener: ItemClickListener<TvObject, View>?) :
        RecyclerView.Adapter<TvSeriesViewHolder>() {

    fun setData(tvResults: MutableList<TvObject>?) {
        if (tvResults?.size!! > 0) {
            mTvResults?.addAll(tvResults)
        } else {
            mTvResults = tvResults
        }
        notifyDataSetChanged()
    }

    // TODO(Dmytro Karataiev): 10/22/16  temp
    val series: MutableList<TvObject>? get() = mTvResults

    init {
        Log.d("TvAdapter", "tvResults.getResults().size():" + mTvResults?.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvSeriesViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_item, parent, false)
        return TvSeriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: TvSeriesViewHolder, position: Int) {
        val tvObject = mTvResults!![position]
        holder.mImageFavorite!!.visibility = View.GONE

        // TODO: 6/13/16 cache link
        Picasso.with(mContext)
                .load(Consts.IMAGE_URL
                        + Utility.posterSize(mContext)[1]
                        + tvObject.backdropPath)
                .into(holder.mImageActor!!, object : Callback {
                    override fun onSuccess() {
                        holder.mProgressSpinner!!.visibility = View.GONE
                    }

                    override fun onError() {
                        holder.mProgressSpinner!!.visibility = View.GONE
                    }
                })
        holder.mTextActor!!.text = tvObject.name

        holder.itemView.setOnClickListener {
            mListener?.onItemClicked(tvObject, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return if (mTvResults != null) mTvResults!!.size else 0
    }

    companion object {
        private val TAG = TvAdapter::class.java.simpleName
    }

}
