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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import karataiev.dmytro.popularmovies.adapters.TvAdapter;
import karataiev.dmytro.popularmovies.interfaces.ItemClickListener;
import karataiev.dmytro.popularmovies.model.TvObject;
import karataiev.dmytro.popularmovies.model.TvResults;
import karataiev.dmytro.popularmovies.utils.Utility;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
// TODO: 6/14/16 add TV Activity 
/**
 * Created by karataev on 6/13/16.
 */
public class TvFragment extends Fragment implements ItemClickListener<TvObject, View> {

    private static final String TAG = TvFragment.class.getSimpleName();

    private CompositeSubscription _subscription;
    private TvAdapter mTvAdapter;
    private GridLayoutManager mGridLayoutManager;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_SECTION_STATE = "section_state";

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
    public static TvFragment newInstance(int sectionNumber, String sectionState) {
        TvFragment fragment = new TvFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_SECTION_STATE, sectionState);
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

        mTvAdapter = new TvAdapter(getContext(), null);

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
                Log.d(TAG, "mGridLayoutManager.findLastVisibleItemPosition():" + mGridLayoutManager.findLastVisibleItemPosition() + " " + mTvAdapter.getItemCount());
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
        Log.d(TAG, "page:" + page);
        isUpdating = true;

        _subscription.add(App.getApiManager().getMoviesService().getTvPopular(page)
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
                            if (mTvAdapter == null) {
                                mTvAdapter = new TvAdapter(getContext(), tvResults.getResults());
                                mRecyclerView.swapAdapter(mTvAdapter, false);
                            } else {
                                mTvAdapter.setData(TvFragment.this, tvResults.getResults());
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
    public void onItemClicked(TvObject item, View view) {
        Toast.makeText(getContext(), item.getName(), Toast.LENGTH_SHORT).show();
    }
}
