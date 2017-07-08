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

package com.adkdevelopment.moviesdb.feat_series.presenters

import android.content.Intent
import android.util.Log
import com.adkdevelopment.moviesdb.App
import com.adkdevelopment.moviesdb.data.model.*
import com.adkdevelopment.moviesdb.ui.base.BaseMvpPresenter
import com.adkdevelopment.moviesdb.ui.contracts.TvDetailContract
import rx.Observer
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

/**
 * Presenter for the PeopleFragment.
 * Created by Dmytro Karataiev on 10/22/16.
 */
class TvDetailPresenter : BaseMvpPresenter<TvDetailContract.View>(), TvDetailContract.Presenter {

    private val mSubscriptions: CompositeSubscription = CompositeSubscription()

    override fun detachView() {
        super.detachView()
        if (!mSubscriptions.isUnsubscribed) {
            mSubscriptions.unsubscribe()
        }
    }

    override fun requestData(intent: Intent?) {
        checkViewAttached()
        if (intent != null && intent.hasExtra(Consts.TV_EXTRA)) {
            val tvObject = intent.getParcelableExtra<TvObject>(Consts.TV_EXTRA)

            mSubscriptions.add(App.getApiManager().moviesService
                    .getTvId(tvObject.id.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<TvSeries>() {
                        override fun onCompleted() {
                        }

                        override fun onError(e: Throwable) {
                            mvpView.showError()
                            Log.d("SeriesDetailedFragment", "e:" + e)
                        }

                        override fun onNext(tvSeries: TvSeries) {
                            mvpView.showData(tvSeries)
                        }
                    }))

            // Adds actors to the RecyclerView
            mSubscriptions.add(
                    App.getApiManager().moviesService.getTvCredits(tvObject.id.toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : Observer<MovieCredits> {
                                override fun onCompleted() {
                                    Log.d(TAG, "onCompleted: ")
                                }

                                override fun onError(e: Throwable) {
                                    Log.e(TAG, "onError: ", e)
                                }

                                override fun onNext(movieCredits: MovieCredits) {
                                    mvpView.showActors(movieCredits)
                                }
                            })
            )

            mSubscriptions.add(App.getApiManager().moviesService.getTvImages(tvObject.id.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<Backdrops>() {
                        override fun onCompleted() {

                        }

                        override fun onError(e: Throwable) {

                        }

                        override fun onNext(backdrops: Backdrops) {
                            mvpView.showPosters(backdrops)
                        }
                    }))
        }
    }

    companion object {
        private val TAG = TvDetailPresenter::class.java.simpleName
    }
}
