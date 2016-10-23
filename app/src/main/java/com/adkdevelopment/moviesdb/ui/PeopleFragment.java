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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adkdevelopment.moviesdb.R;
import com.adkdevelopment.moviesdb.data.model.Consts;
import com.adkdevelopment.moviesdb.data.model.person.PersonPopularResult;
import com.adkdevelopment.moviesdb.ui.adapters.PeopleAdapter;
import com.adkdevelopment.moviesdb.ui.base.BaseFragment;
import com.adkdevelopment.moviesdb.ui.contracts.PeopleContract;
import com.adkdevelopment.moviesdb.ui.interfaces.ItemClickListener;
import com.adkdevelopment.moviesdb.ui.interfaces.ScrollableFragment;
import com.adkdevelopment.moviesdb.ui.presenters.PeoplePresenter;
import com.adkdevelopment.moviesdb.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
// TODO: 6/14/16 add TV Activity 

/**
 * Fragment to show popular persons of the day with continuous scrolling.
 * On click opens Deatil activity for an actor.
 * Created by karataev on 6/13/16.
 */
public class PeopleFragment extends BaseFragment
        implements PeopleContract.View,
        ItemClickListener<PersonPopularResult, View>, ScrollableFragment {

    private static final String TAG = PeopleFragment.class.getSimpleName();

    private PeoplePresenter mPresenter;
    private PeopleAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private List<PersonPopularResult> mPeople;

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
    private boolean isUpdating;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PeopleFragment newInstance() {
        PeopleFragment fragment = new PeopleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);

        // Scale GridView according to the screen size
        mGridLayoutManager = new GridLayoutManager(mRecyclerView.getContext(),
                Utility.screenSize(getContext())[3]);
        mAdapter = new PeopleAdapter(this, getContext(), null);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mPresenter = new PeoplePresenter(getContext());
        mPresenter.attachView(this);

        // If movies were fetched - re-uses data
        if (savedInstanceState == null || !savedInstanceState.containsKey(SAVE_RESULTS)) {
            mPresenter.requestData(mCurrentPage);
        } else {
            mPeople = savedInstanceState.getParcelableArrayList(SAVE_RESULTS);
            mCurrentPosition = savedInstanceState.getInt(SAVE_POS);
            mCurrentPage = savedInstanceState.getInt(SAVE_PAGE);
            showData(mPeople, mCurrentPage);
            mRecyclerView.scrollToPosition(mCurrentPosition);
        }

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mGridLayoutManager.findLastVisibleItemPosition() >= mAdapter.getItemCount() - 5) {
                    if (!isUpdating) {
                        mPresenter.requestData(++mCurrentPage);
                    }
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
        outState.putParcelableArrayList(SAVE_RESULTS, (ArrayList<PersonPopularResult>) mPeople);
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
    public void onItemClicked(PersonPopularResult item, View view) {
        Intent intent = new Intent(getContext(), ActorActivity.class);
        intent.putExtra(Consts.ACTOR_EXTRA, item);
        startActivity(intent);
    }

    @Override
    public void scrollToTop() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void showData(List<PersonPopularResult> series, int page) {
        mListEmpty.setVisibility(View.INVISIBLE);
        if (mAdapter == null || page == 1) {
            mAdapter = new PeopleAdapter(this, getContext(), series);
            mRecyclerView.swapAdapter(mAdapter, false);
        } else {
            mAdapter.setData(series);
        }

        mPeople = mAdapter.getPeople();
    }

    @Override
    public void showEmpty() {
        mListEmpty.setText(getString(R.string.recyclerview_empty_text));
        mListEmpty.setVisibility(View.VISIBLE);
        mAdapter.setData(null);
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
