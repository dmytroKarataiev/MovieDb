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
import android.util.Log;

import com.adkdevelopment.moviesdb.App;
import com.adkdevelopment.moviesdb.data.model.person.PersonPopular;
import com.adkdevelopment.moviesdb.ui.base.BaseMvpPresenter;
import com.adkdevelopment.moviesdb.ui.contracts.PeopleContract;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Presenter for the PeopleFragment.
 * Created by Dmytro Karataiev on 10/22/16.
 */
public class PeoplePresenter
        extends BaseMvpPresenter<PeopleContract.View>
        implements PeopleContract.Presenter {

    private static final String TAG = PeoplePresenter.class.getSimpleName();

    private CompositeSubscription mSubscription;
    private final Context mContext;
    private boolean isUpdating;

    public PeoplePresenter(Context context) {
        mSubscription = new CompositeSubscription();
        mContext = context;
    }

    @Override
    public void requestData(int page) {
        checkViewAttached();
        if (!isUpdating) {
            isUpdating = true;
            getMvpView().showProgress();

            if (mSubscription != null && !mSubscription.isUnsubscribed() && page == 1) {
                mSubscription.unsubscribe();
                mSubscription = new CompositeSubscription();
            }

            mSubscription.add(App.getApiManager().getMoviesService().getActorPopular(page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<PersonPopular>() {
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
                        public void onNext(PersonPopular tvResults) {
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
    public void detachView() {
        super.detachView();
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
