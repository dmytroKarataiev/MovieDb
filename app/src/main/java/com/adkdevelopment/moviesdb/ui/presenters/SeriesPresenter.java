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

package com.adkdevelopment.moviesdb.ui.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.adkdevelopment.moviesdb.App;
import com.adkdevelopment.moviesdb.R;
import com.adkdevelopment.moviesdb.data.model.TvResults;
import com.adkdevelopment.moviesdb.ui.base.BaseMvpPresenter;
import com.adkdevelopment.moviesdb.ui.contracts.SeriesContract;
import com.adkdevelopment.moviesdb.utils.Utility;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Presenter for the SeriesFragment.
 * Created by Dmytro Karataiev on 10/22/16.
 */
public class SeriesPresenter
        extends BaseMvpPresenter<SeriesContract.View>
        implements SeriesContract.Presenter,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SeriesPresenter.class.getSimpleName();

    private CompositeSubscription mSubscription;
    private final Context mContext;
    private boolean isUpdating;

    public SeriesPresenter(Context context) {
        mSubscription = new CompositeSubscription();
        mContext = context;
    }

    @Override
    public void requestData(int page) {
        checkViewAttached();
        if (!isUpdating) {
            isUpdating = true;
            getMvpView().showProgress();

            String sort = Utility.getSeriesSort(mContext);

            if (mSubscription != null && !mSubscription.isUnsubscribed() && page == 1) {
                mSubscription.unsubscribe();
                mSubscription = new CompositeSubscription();
            }

            mSubscription.add(App.getApiManager().getMoviesService().getSeries(sort, page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<TvResults>() {
                        @Override
                        public void onCompleted() {
                            getMvpView().showProgress();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "onError: ", e);
                            getMvpView().showProgress();
                            getMvpView().showError();
                        }

                        @Override
                        public void onNext(TvResults tvResults) {
                            if (tvResults.getResults() != null
                                    && tvResults.getResults().size() > 0) {
                                getMvpView().showData(tvResults.getResults(), page);
                            } else if (page == 1){
                                getMvpView().showEmpty();
                            }
                            isUpdating = false;
                        }
                    }));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(mContext.getString(R.string.pref_sort_series_key))) {
            requestData(1);
        }
    }

    @Override
    public void attachView(SeriesContract.View mvpView) {
        super.attachView(mvpView);
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void detachView() {
        super.detachView();
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .unregisterOnSharedPreferenceChangeListener(this);
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
