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
import android.content.Intent;
import android.util.Log;

import com.adkdevelopment.moviesdb.App;
import com.adkdevelopment.moviesdb.data.model.Backdrops;
import com.adkdevelopment.moviesdb.data.model.Consts;
import com.adkdevelopment.moviesdb.data.model.MovieCredits;
import com.adkdevelopment.moviesdb.data.model.TvObject;
import com.adkdevelopment.moviesdb.data.model.TvSeries;
import com.adkdevelopment.moviesdb.ui.base.BaseMvpPresenter;
import com.adkdevelopment.moviesdb.ui.contracts.TvDetailContract;

import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Presenter for the PeopleFragment.
 * Created by Dmytro Karataiev on 10/22/16.
 */
public class TvDetailPresenter
        extends BaseMvpPresenter<TvDetailContract.View>
        implements TvDetailContract.Presenter {

    private static final String TAG = TvDetailPresenter.class.getSimpleName();

    private CompositeSubscription mSubscriptions;
    private final Context mContext;

    public TvDetailPresenter(Context context) {
        mSubscriptions = new CompositeSubscription();
        mContext = context;
    }



    @Override
    public void detachView() {
        super.detachView();
        if (!mSubscriptions.isUnsubscribed()) {
            mSubscriptions.unsubscribe();
        }
    }

    @Override
    public void requestData(Intent intent) {
        checkViewAttached();
        if (intent != null && intent.hasExtra(Consts.TV_EXTRA)) {
            TvObject tvObject = intent.getParcelableExtra(Consts.TV_EXTRA);

            mSubscriptions.add(App.getApiManager().getMoviesService()
                    .getTvId(String.valueOf(tvObject.getId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<TvSeries>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            getMvpView().showError();
                            Log.d("TvDetailFragment", "e:" + e);
                        }

                        @Override
                        public void onNext(TvSeries tvObject) {
                            getMvpView().showData(tvObject);
                        }
                    }));

            // Adds actors to the RecyclerView
            mSubscriptions.add(
                    App.getApiManager().getMoviesService().getTvCredits(String.valueOf(tvObject.getId()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<MovieCredits>() {
                                @Override
                                public void onCompleted() {
                                    Log.d(TAG, "onCompleted: ");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, "onError: ", e);
                                }

                                @Override
                                public void onNext(MovieCredits movieCredits) {
                                    getMvpView().showActors(movieCredits);
                                }
                            })
            );

            mSubscriptions.add(App.getApiManager().getMoviesService().getTvImages(String.valueOf(tvObject.getId()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<Backdrops>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Backdrops backdrops) {
                    getMvpView().showPosters(backdrops);
                }
            }));
        }
    }
}
