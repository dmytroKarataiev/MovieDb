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

package com.adkdevelopment.moviesdb.feat_series

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.adkdevelopment.moviesdb.R
import com.adkdevelopment.moviesdb.data.model.*
import com.adkdevelopment.moviesdb.feat_series.presenters.TvDetailPresenter
import com.adkdevelopment.moviesdb.ui.ActorActivity
import com.adkdevelopment.moviesdb.ui.adapters.ActorsAdapter
import com.adkdevelopment.moviesdb.ui.base.BaseFragment
import com.adkdevelopment.moviesdb.ui.contracts.TvDetailContract
import com.adkdevelopment.moviesdb.ui.interfaces.ItemClickListener
import com.adkdevelopment.moviesdb.utils.Utility
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_tv_detail.view.*
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class SeriesDetailedFragment : BaseFragment(), TvDetailContract.View, ItemClickListener<MovieCast, View> {

    @BindView(R.id.tv_title)
    internal lateinit var mTextTitle: TextView
    @BindView(R.id.tv_status)
    internal lateinit var mTextStatus: TextView
    @BindView(R.id.tv_network)
    internal lateinit var mTextNetwork: TextView
    @BindView(R.id.tv_homepage)
    internal lateinit var mTextHomepage: TextView
    @BindView(R.id.tv_runtime)
    internal lateinit var mTextRuntime: TextView
    @BindView(R.id.tv_language)
    internal lateinit var mTextLanguage: TextView
    @BindView(R.id.tv_overview)
    internal lateinit var mTextOverview: TextView
    @BindView(R.id.tv_genres)
    internal lateinit var mTextGenres: TextView
    @BindView(R.id.recyclerview)
    internal lateinit var mRecyclerActors: RecyclerView

    private var mUnbinder: Unbinder? = null
    private lateinit var mPresenter: TvDetailPresenter
    private var mActorsAdapter: ActorsAdapter? = null
    private var mTvSeries : TvSeries? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_tv_detail, container, false)

        mUnbinder = ButterKnife.bind(this, rootView)

        mPresenter = TvDetailPresenter()
        mPresenter.attachView(this)

        rootView.tv_seasons.setOnClickListener {
            var intent = Intent(context, SeriesSeasonsActivity::class.java)
            intent.putExtra("tvId", mTvSeries)
            startActivity(intent)
        }

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mPresenter.requestData(activity.intent)
    }

    override fun onDetach() {
        super.onDetach()
        mPresenter.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mUnbinder?.unbind()
    }

    override fun onItemClicked(movieCast: MovieCast, view: View) {
        val intent = Intent(context, ActorActivity::class.java)
        intent.putExtra(Consts.ACTOR_EXTRA, movieCast)
        startActivity(intent)
    }

    override fun showData(tvSeries: TvSeries) {
        mTvSeries = tvSeries
        mTextTitle.text = tvSeries.name
        mTextOverview.text = tvSeries.overview
        mTextLanguage.text = tvSeries.originalLanguage
        mTextLanguage.text = tvSeries.originalLanguage

        val networks = TextUtils.join(", ", tvSeries.networks)
        mTextNetwork.text = networks
        mTextStatus.text = tvSeries.status
        mTextHomepage.text = tvSeries.homepage
        mTextRuntime.text = String.format(Locale.getDefault(), "%d", tvSeries.episodeRunTime[0])

        val genres = TextUtils.join(", ", tvSeries.genres)
        mTextGenres.text = genres
    }

    override fun showPosters(backdrops: Backdrops) {
        val rand = Random().nextInt(backdrops.backdrops.size)
        val path = MovieObject.BASE_URL + Utility.posterSize(context)[1] + backdrops.backdrops[rand].filePath

        val backdrop = activity.findViewById(R.id.backdrop) as ImageView
        Picasso.with(context).load(path).into(backdrop)
    }

    override fun showActors(movieCredits: MovieCredits) {
        val layoutManager = LinearLayoutManager(context,
                RecyclerView.HORIZONTAL, false)
        mRecyclerActors.layoutManager = layoutManager
        mActorsAdapter = ActorsAdapter(context, movieCredits)
        mRecyclerActors.adapter = mActorsAdapter
        mActorsAdapter!!.setData(this@SeriesDetailedFragment)
        mRecyclerActors.isNestedScrollingEnabled = true
    }

    override fun showError() {

    }
}

