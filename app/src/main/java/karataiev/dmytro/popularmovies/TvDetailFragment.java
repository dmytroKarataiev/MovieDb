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

package karataiev.dmytro.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import karataiev.dmytro.popularmovies.model.Consts;
import karataiev.dmytro.popularmovies.model.MovieObject;
import karataiev.dmytro.popularmovies.model.TvObject;
import karataiev.dmytro.popularmovies.utils.Utility;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A placeholder fragment containing a simple view.
 */
public class TvDetailFragment extends Fragment {

    @Nullable
    @BindView(R.id.backdrop)
    ImageView mImageBackdrop;
    @BindView(R.id.tv_title)
    TextView mTextTitle;
    private Unbinder mUnbinder;

    private CompositeSubscription mSubscriptions;

    public TvDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tv_detail, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);
        mSubscriptions = new CompositeSubscription();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity().getIntent().hasExtra(Consts.TV_EXTRA)) {
            loadData(getActivity().getIntent()
                    .getParcelableExtra(Consts.TV_EXTRA));

        }
    }

    /**
     * Method to load TV details to the activity
     * @param tvObject to get Id from
     */
    private void loadData(TvObject tvObject) {
        mTextTitle.setText(tvObject.getName());
        // TODO: 7/27/16 refactor & make random
        if (mImageBackdrop == null) {
            mImageBackdrop = ButterKnife.findById(getActivity(), R.id.backdrop);
            // TODO: 7/27/16 fix and add to the object
            String path = MovieObject.BASE_URL + Utility.posterSize(getContext())[1] + tvObject.getBackdropPath();
            Picasso.with(getContext()).load(path).into(mImageBackdrop);

        }

        mSubscriptions.add(App.getApiManager().getMoviesService()
                .getTvId(String.valueOf(tvObject.getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TvObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TvDetailFragment", "e:" + e);
                    }

                    @Override
                    public void onNext(TvObject tvObject) {
                        Log.d("TvDetailFragment", tvObject.getOverview());
                    }
                }));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        if (mSubscriptions != null && !mSubscriptions.isUnsubscribed()) {
            mSubscriptions.unsubscribe();
        }
    }
}
