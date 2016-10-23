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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adkdevelopment.moviesdb.R;
import com.adkdevelopment.moviesdb.data.model.Consts;
import com.adkdevelopment.moviesdb.data.model.TvObject;
import com.adkdevelopment.moviesdb.ui.adapters.TvAdapter;
import com.adkdevelopment.moviesdb.ui.base.BaseFragment;
import com.adkdevelopment.moviesdb.ui.contracts.SeriesContract;
import com.adkdevelopment.moviesdb.ui.interfaces.ItemClickListener;
import com.adkdevelopment.moviesdb.ui.interfaces.ScrollableFragment;
import com.adkdevelopment.moviesdb.ui.presenters.SeriesPresenter;
import com.adkdevelopment.moviesdb.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment which shows a list of TV Series.
 * Created by karataev on 6/13/16.
 */
public class SeriesFragment extends BaseFragment
        implements SeriesContract.View,
        ItemClickListener<TvObject, View>, ScrollableFragment,
        PopupMenu.OnMenuItemClickListener {

    private static final String TAG = SeriesFragment.class.getSimpleName();

    private List<TvObject> mSeries;

    private TvAdapter mTvAdapter;
    private SeriesPresenter mPresenter;
    private GridLayoutManager mGridLayoutManager;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.list_empty_text)
    TextView mListEmpty;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private Unbinder mUnbinder;

    private int mCurrentPosition;
    private int mCurrentPage = 1;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SeriesFragment newInstance() {
        SeriesFragment fragment = new SeriesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        mUnbinder = ButterKnife.bind(this, rootView);

        // Scale GridView according to the screen size
        mGridLayoutManager = new GridLayoutManager(mRecyclerView.getContext(),
                Utility.screenSize(getContext())[3]);

        mTvAdapter = new TvAdapter(getContext(), null, SeriesFragment.this);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mTvAdapter);

        mPresenter = new SeriesPresenter(getContext());
        mPresenter.attachView(this);

        // If movies were fetched - re-uses data
        if (savedInstanceState == null || !savedInstanceState.containsKey(SAVE_RESULTS)) {
            mPresenter.requestData(mCurrentPage);
        } else {
            mSeries = savedInstanceState.getParcelableArrayList(SAVE_RESULTS);
            mCurrentPosition = savedInstanceState.getInt(SAVE_POS);
            mCurrentPage = savedInstanceState.getInt(SAVE_PAGE);
            showData(mSeries, mCurrentPage);
            mRecyclerView.scrollToPosition(mCurrentPosition);
        }

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mCurrentPosition = mGridLayoutManager.findFirstVisibleItemPosition();
                if (mGridLayoutManager.findLastVisibleItemPosition() >= mTvAdapter.getItemCount() - 5) {
                    mPresenter.requestData(++mCurrentPage);
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            // on force refresh downloads all data
            mCurrentPage = 1;
            mPresenter.requestData(mCurrentPage);
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Saves movies so we don't need to re-download them
        outState.putParcelableArrayList(SAVE_RESULTS, (ArrayList<TvObject>) mSeries);
        outState.putInt(SAVE_POS, mCurrentPosition);
        outState.putInt(SAVE_PAGE, mCurrentPage);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.detachView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onItemClicked(TvObject item, View view) {
        Intent intent = new Intent(getContext(), TvDetailActivity.class);
        intent.putExtra(Consts.TV_EXTRA, item);
        startActivity(intent);
    }

    @Override
    public void scrollToTop() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_series, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_filter_series:
                showSortMenu(getActivity().findViewById(R.id.action_filter_series));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getContext());

        switch (item.getItemId()) {
            case R.id.popup_filter_airing:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_series_key),
                        getString(R.string.pref_sort_series_airing)).apply();
                break;
            case R.id.popup_filter_on_the_air:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_series_key),
                        getString(R.string.pref_sort_series_ontheair)).apply();
                break;
            case R.id.popup_filter_popular:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_series_key),
                        getString(R.string.pref_sort_series_popular)).apply();
                break;
            case R.id.popup_filter_top:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_series_key),
                        getString(R.string.pref_sort_series_top)).apply();
                break;
        }

        return false;
    }

    /**
     * Shows PopupMenu on Filter button click in ActionBar
     * @param view of the button itself
     */
    public void showSortMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_series_filter_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public void showData(List<TvObject> series, int page) {
        mListEmpty.setVisibility(View.INVISIBLE);
        if (mTvAdapter == null || page == 1) {
            mTvAdapter = new TvAdapter(getContext(), series, SeriesFragment.this);
            mRecyclerView.swapAdapter(mTvAdapter, false);
        } else {
            mTvAdapter.setData(series);
        }
        mSeries = mTvAdapter.getSeries();
    }

    @Override
    public void showEmpty() {
        mListEmpty.setText(getString(R.string.recyclerview_empty_text));
        mListEmpty.setVisibility(View.VISIBLE);
        mTvAdapter.setData(null);
    }

    @Override
    public void showError() {
        mListEmpty.setVisibility(View.VISIBLE);
        mListEmpty.setText(R.string.fragment_error);
    }

    @Override
    public void showProgress() {
        mSwipeRefreshLayout.setRefreshing(false);
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }
}
