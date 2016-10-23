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

package com.adkdevelopment.moviesdb.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adkdevelopment.moviesdb.R;
import com.adkdevelopment.moviesdb.data.model.Backdrops;
import com.adkdevelopment.moviesdb.data.model.Consts;
import com.adkdevelopment.moviesdb.data.model.MovieCast;
import com.adkdevelopment.moviesdb.data.model.MovieCredits;
import com.adkdevelopment.moviesdb.data.model.MovieObject;
import com.adkdevelopment.moviesdb.data.model.TvSeries;
import com.adkdevelopment.moviesdb.ui.adapters.ActorsAdapter;
import com.adkdevelopment.moviesdb.ui.base.BaseFragment;
import com.adkdevelopment.moviesdb.ui.contracts.TvDetailContract;
import com.adkdevelopment.moviesdb.ui.interfaces.ItemClickListener;
import com.adkdevelopment.moviesdb.ui.presenters.TvDetailPresenter;
import com.adkdevelopment.moviesdb.utils.Utility;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class TvDetailFragment extends BaseFragment
        implements TvDetailContract.View, ItemClickListener<MovieCast, View> {

    @Nullable
    @BindView(R.id.backdrop)
    ImageView mImageBackdrop;
    @BindView(R.id.tv_title)
    TextView mTextTitle;
    @BindView(R.id.tv_status)
    TextView mTextStatus;
    @BindView(R.id.tv_network)
    TextView mTextNetwork;
    @BindView(R.id.tv_homepage)
    TextView mTextHomepage;
    @BindView(R.id.tv_runtime)
    TextView mTextRuntime;
    @BindView(R.id.tv_language)
    TextView mTextLanguage;
    @BindView(R.id.tv_overview)
    TextView mTextOverview;
    @BindView(R.id.tv_genres)
    TextView mTextGenres;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerActors;
    private Unbinder mUnbinder;

    private TvDetailPresenter mPresenter;
    private ActorsAdapter mActorsAdapter;

    public TvDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tv_detail, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);

        mPresenter = new TvDetailPresenter(getContext());
        mPresenter.attachView(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.requestData(getActivity().getIntent());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onItemClicked(MovieCast movieCast, View view) {
        Intent intent = new Intent(getContext(), ActorActivity.class);
        intent.putExtra(Consts.ACTOR_EXTRA, movieCast);
        startActivity(intent);
    }

    @Override
    public void showData(TvSeries tvSeries) {
        mTextTitle.setText(tvSeries.getName());
        mTextOverview.setText(tvSeries.getOverview());
        mTextLanguage.setText(tvSeries.getOriginalLanguage());
        mTextLanguage.setText(tvSeries.getOriginalLanguage());

        String networks = TextUtils.join(", ", tvSeries.getNetworks());
        mTextNetwork.setText(networks);
        mTextStatus.setText(tvSeries.getStatus());
        mTextHomepage.setText(tvSeries.getHomepage());
        mTextRuntime.setText(String.format(Locale.getDefault(), "%d", tvSeries.getEpisodeRunTime().get(0)));

        String genres = TextUtils.join(", ", tvSeries.getGenres());
        mTextGenres.setText(genres);
    }

    @Override
    public void showPosters(Backdrops backdrops) {
        if (mImageBackdrop == null) {
            mImageBackdrop = ButterKnife.findById(getActivity(), R.id.backdrop);
            int rand = new Random().nextInt(backdrops.getBackdrops().size());
            String path = MovieObject.BASE_URL + Utility.posterSize(getContext())[1]
                    + backdrops.getBackdrops().get(rand).getFilePath();
            Picasso.with(getContext()).load(path).into(mImageBackdrop);
        }
    }

    @Override
    public void showActors(MovieCredits movieCredits) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                RecyclerView.HORIZONTAL, false);
        mRecyclerActors.setLayoutManager(layoutManager);
        mActorsAdapter = new ActorsAdapter(getContext(), movieCredits);
        mRecyclerActors.setAdapter(mActorsAdapter);
        mActorsAdapter.setData(TvDetailFragment.this);
        mRecyclerActors.setNestedScrollingEnabled(true);
    }

    @Override
    public void showError() {

    }
}
