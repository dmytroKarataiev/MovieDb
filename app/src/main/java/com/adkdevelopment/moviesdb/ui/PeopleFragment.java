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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adkdevelopment.moviesdb.App;
import com.adkdevelopment.moviesdb.R;
import com.adkdevelopment.moviesdb.data.model.Consts;
import com.adkdevelopment.moviesdb.data.model.person.PersonPopular;
import com.adkdevelopment.moviesdb.data.model.person.PersonPopularResult;
import com.adkdevelopment.moviesdb.ui.adapters.PersonAdapter;
import com.adkdevelopment.moviesdb.ui.base.BaseFragment;
import com.adkdevelopment.moviesdb.ui.interfaces.ItemClickListener;
import com.adkdevelopment.moviesdb.ui.interfaces.ScrollableFragment;
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
 * Fragment to show popular persons of the day with continuous scrolling.
 * On click opens Deatil activity for an actor.
 * Created by karataev on 6/13/16.
 */
public class PeopleFragment extends BaseFragment
        implements ItemClickListener<PersonPopularResult, View>, ScrollableFragment {

    private static final String TAG = PeopleFragment.class.getSimpleName();

    private CompositeSubscription _subscription;
    private PersonAdapter mPersonAdapter;
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
    public static PeopleFragment newInstance() {
        PeopleFragment fragment = new PeopleFragment();
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

        mUnbinder = ButterKnife.bind(this, rootView);

        mPersonAdapter = new PersonAdapter(getContext(), null);

        requestUpdate(mCurrentPage);

        // Scale GridView according to the screen size
        mGridLayoutManager = new GridLayoutManager(mRecyclerView.getContext(),
                Utility.screenSize(getContext())[3]);

        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mRecyclerView.setAdapter(mPersonAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mGridLayoutManager.findLastVisibleItemPosition() >= mPersonAdapter.getItemCount() - 5) {
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
        Log.d(TAG, "page:" + page);
        isUpdating = true;

        _subscription.add(App.getApiManager().getMoviesService().getActorPopular(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PersonPopular>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onNext(PersonPopular tvResults) {
                        if (tvResults.getResults() != null && tvResults.getResults().size() > 0) {
                            if (mPersonAdapter == null) {
                                mPersonAdapter = new PersonAdapter(getContext(), tvResults.getResults());
                                mRecyclerView.swapAdapter(mPersonAdapter, false);
                            } else {
                                mPersonAdapter.setData(PeopleFragment.this, tvResults.getResults());
                            }
                            Log.d(TAG, "onNext: " + tvResults.getTotalResults() + " " + tvResults.getResults().size());
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
}
