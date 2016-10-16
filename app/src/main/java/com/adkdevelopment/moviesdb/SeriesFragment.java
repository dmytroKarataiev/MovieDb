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

package com.adkdevelopment.moviesdb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.adkdevelopment.moviesdb.adapters.TvAdapter;
import com.adkdevelopment.moviesdb.interfaces.ItemClickListener;
import com.adkdevelopment.moviesdb.interfaces.ScrollableFragment;
import com.adkdevelopment.moviesdb.model.Consts;
import com.adkdevelopment.moviesdb.model.TvObject;
import com.adkdevelopment.moviesdb.model.TvResults;
import com.adkdevelopment.moviesdb.utils.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
// TODO: 6/14/16 add TV Activity 
/**
 * Fragment which shows a list of TV Series.
 * Created by karataev on 6/13/16.
 */
public class SeriesFragment extends Fragment
        implements ItemClickListener<TvObject, View>, ScrollableFragment,
        SharedPreferences.OnSharedPreferenceChangeListener,
        PopupMenu.OnMenuItemClickListener {

    private static final String TAG = SeriesFragment.class.getSimpleName();

    private CompositeSubscription _subscription;
    private TvAdapter mTvAdapter;
    private GridLayoutManager mGridLayoutManager;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    /* todo
    @BindView(R.id.list_empty_text)
    TextView mListEmpty;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    */
    private Unbinder mUnbinder;

    private int mCurrentPosition;
    private int mCurrentPage = 1;
    private boolean isUpdating;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _subscription = new CompositeSubscription();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        mUnbinder = ButterKnife.bind(this, rootView);

        mTvAdapter = new TvAdapter(getContext(), null, SeriesFragment.this);

        requestUpdate(mCurrentPage);

        // Scale GridView according to the screen size
        mGridLayoutManager = new GridLayoutManager(mRecyclerView.getContext(),
                Utility.screenSize(getContext())[3]);

        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mRecyclerView.setAdapter(mTvAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mGridLayoutManager.findLastVisibleItemPosition() >= mTvAdapter.getItemCount() - 5) {
                    if (!isUpdating) {
                        requestUpdate(++mCurrentPage);
                    }
                }
            }
        });

        return rootView;
    }

    /**
     * todo
     */
    private void requestUpdate(int page) {
        isUpdating = true;

        String sort = Utility.getSeriesSort(getContext());

        if (_subscription != null && !_subscription.isUnsubscribed() && page == 1) {
            _subscription.unsubscribe();
            _subscription = new CompositeSubscription();
        }

        _subscription.add(App.getApiManager().getMoviesService().getSeries(sort, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TvResults>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onNext(TvResults tvResults) {
                        if (tvResults.getResults() != null && tvResults.getResults().size() > 0) {
                            if (mTvAdapter == null || page == 1) {
                                mTvAdapter = new TvAdapter(getContext(), tvResults.getResults(), SeriesFragment.this);
                                mRecyclerView.swapAdapter(mTvAdapter, false);
                            } else {
                                mTvAdapter.setData(tvResults.getResults());
                            }
                        }
                        isUpdating = false;
                    }
                }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        if (_subscription != null && !_subscription.isUnsubscribed()) {
            _subscription.unsubscribe();
        }
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_series_key))) {
            requestUpdate(1);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
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

}
