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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adkdevelopment.moviesdb.R;
import com.adkdevelopment.moviesdb.data.model.MovieObject;
import com.adkdevelopment.moviesdb.ui.adapters.MoviesAdapter;
import com.adkdevelopment.moviesdb.ui.base.BaseFragment;
import com.adkdevelopment.moviesdb.ui.interfaces.ItemClickListener;
import com.adkdevelopment.moviesdb.ui.interfaces.ScrollableFragment;
import com.adkdevelopment.moviesdb.ui.interfaces.SearchableFragment;
import com.adkdevelopment.moviesdb.utils.Utility;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends BaseFragment
        implements ItemClickListener<MovieObject, View>,
        ScrollableFragment, SearchableFragment,
        SharedPreferences.OnSharedPreferenceChangeListener,
        PopupMenu.OnMenuItemClickListener {

    private static final String TAG = MoviesFragment.class.getSimpleName();

    // Couldn't find more efficient way to use following variable then to make them global
    private MoviesAdapter mMovieAdapter;
    private List<MovieObject> mMovies;
    private String mSort;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.list_empty_text)
    TextView mListEmpty;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private Unbinder mUnbinder;

    private GridLayoutManager mGridLayoutManager;

    private BroadcastReceiver mNetworkStateReceiver;
    private boolean mIsNetworkRestored;

    // Continuous viewing and progress bar variables
    private int mCurrentPage = 1;
    private int mCurrentPosition;
    private boolean mAddMovies;
    private boolean mIsSearch;
    private boolean mAddSearchMovies;
    private boolean mIsClearedSearch;
    private String mSearchParameter = "";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MoviesFragment newInstance() {
        MoviesFragment fragment = new MoviesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionbarTitle();
    }

    @Override
    public void searchRequest(String searchParameter) {
        mSearchParameter = searchParameter;
        Log.d(TAG, "Searching for: " + mSearchParameter);
        mIsSearch = true;

        if (mSearchParameter.length() < 4) {
            mSearchParameter = "";
            mIsSearch = false;
            mIsClearedSearch = true;
        }
        mCurrentPage = 1;
        mCurrentPosition = 1;
        updateMovieList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        // Initializes global mSort with SharedPreferences of sort
        mSort = Utility.getSort(getContext());

        // If movies were fetched - re-uses data
        if (savedInstanceState == null || !savedInstanceState.containsKey(SAVE_RESULTS)) {
            updateMovieList();
        } else {
            mMovies = savedInstanceState.getParcelableArrayList(SAVE_RESULTS);
            mCurrentPosition = savedInstanceState.getInt(SAVE_POS);
            mCurrentPage = savedInstanceState.getInt(SAVE_PAGE);
            mSearchParameter = savedInstanceState.getString(SAVE_SEARCH);
        }

        mUnbinder = ButterKnife.bind(this, rootView);

        // Scale GridView according to the screen size
        mGridLayoutManager = new GridLayoutManager(mRecyclerView.getContext(),
                Utility.screenSize(getContext())[3]);

        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mMovieAdapter = new MoviesAdapter(getActivity(), mMovies);
        mMovieAdapter.setData(this);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mMovies != null) {
                    mCurrentPosition = mGridLayoutManager.findFirstVisibleItemPosition();

                    if (((mGridLayoutManager.findFirstCompletelyVisibleItemPosition() >= mMovies.size() - 8
                            || mGridLayoutManager.findLastVisibleItemPosition() >= mMovies.size() - 8)
                            && Utility.isOnline(getContext()))) {

                        if (mSearchParameter.length() > 0) {
                            mAddSearchMovies = true;
                        } else {
                            mAddMovies = true;
                        }
                        mCurrentPage++;
                        updateMovieList();
                    }
                }
            }
        });

        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.smoothScrollToPosition(mCurrentPosition);

        // TODO: 8/28/16 move to activity 
        // BroadcastReceiver to get info about network connection
        mNetworkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

                // Updates screen on network connection if nothing was on the screen
                if (activeNetInfo != null && mMovies == null) {
                    mIsNetworkRestored = true;
                    updateMovieList();
                }
            }
        };
        // Starts receiver
        startListening();

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            // on force refresh downloads all data
            mCurrentPage = 1;
            updateMovieList();
            mSwipeRefreshLayout.setRefreshing(false);
        });


        return rootView;
    }

    /**
     * Method to register BroadcastReceiver
     */
    private void startListening() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mNetworkStateReceiver, filter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Saves movies so we don't need to re-download them
        outState.putParcelableArrayList(SAVE_RESULTS, (ArrayList<MovieObject>) mMovies);
        outState.putInt(SAVE_POS, mCurrentPosition);
        outState.putInt(SAVE_PAGE, mCurrentPage);
        outState.putString(SAVE_SEARCH, mSearchParameter);
    }

    /**
     * Method to update UI when settings changed
     */
    public void updateMovieList() {
        String sort = Utility.getSort(getContext());
        if (Utility.isOnline(getContext())) {
            // Checks if settings were changed
            if (!sort.equals(mSort)) {
                mSort = sort;
                // fetches new data
                mCurrentPage = 1;
                fetchMovies(sort);
                // updates global settings variable
                setActionbarTitle();

            } else if (mMovies == null || mMovies.isEmpty()) {
                mCurrentPage = 1;
                // fetches new data
                fetchMovies(sort);
            } else if (mAddMovies || mIsClearedSearch) {
                fetchMovies("");
            } else if (mIsSearch) {
                fetchMovies(mSearchParameter);
            } else if (mIsNetworkRestored) {
                fetchMovies(sort);
                mIsNetworkRestored = false;
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        startListening();
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mNetworkStateReceiver);
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Method to fetch movies and if there is no network to provide empty MovieObject ArrayList
     * so the App won't crash
     *
     * @param sort to fetch data sorted with the parameter
     */
    private void fetchMovies(String sort) {
        FetchMovies fetchMovie = new FetchMovies(mIsSearch, mCurrentPage);
        fetchMovie.execute(sort);
    }

    /**
     * Method to set ActionBar title according to the sort criteria
     */
    private void setActionbarTitle() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(Utility.getSortReadable(getContext()));
        }
    }

    @Override
    public void onItemClicked(MovieObject item, View view) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(MovieObject.MOVIE_OBJECT, item);
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
        if (key.equals(getString(R.string.pref_sort_key))) {
            setPosition(0);
            updateMovieList();
        }
    }

    // TODO: 6/2/16 change to Rx 

    /**
     * Class to retrieve MovieObjects from JSON on background thread
     */
    public class FetchMovies extends AsyncTask<String, Void, ArrayList<MovieObject>> {

        private final String LOG_TAG = FetchMovies.class.getSimpleName();
        private boolean isSearch;
        private final int currentPage;
        private String searchParams;

        public FetchMovies(boolean isSearch, int currentPage) {
            this.isSearch = isSearch;
            this.currentPage = currentPage;
        }

        /**
         * AsyncTask to fetch data on background thread with listener to pass status of downloading
         *
         * @param params receives request link from Utility class
         * @return ArrayList of YouTube id's for trailers
         */
        protected ArrayList<MovieObject> doInBackground(String... params) {

            URL url;
            if (isSearch && params[0].length() > 0) {
                url = Utility.getSearchURL(params[0], currentPage);
            } else {
                url = Utility.getUrl(currentPage, getContext());
            }

            // Network Client
            OkHttpClient client = new OkHttpClient();

            // Will contain the raw JSON response as a string.
            String movieJsonStr = "";

            if (params[0] != null) {
                searchParams = params[0];

                try {
                    //URL url = new URL(params[0]);
                    // Create the request to movide db, and open the connection
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response responses = client.newCall(request).execute();
                    movieJsonStr = responses.body().string();
                    responses.body().close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                } catch (NullPointerException e) {
                    Log.e(LOG_TAG, "Null ", e);
                }

                return Utility.getMoviesGSON(getContext(), movieJsonStr);

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieObject> movieObjects) {
            if (searchParams.equals(mSort)) {
                mMovies = movieObjects;
            } else if (movieObjects == null || mMovies == null) {
                mMovies = new ArrayList<>();
            } else if (mAddMovies) {
                mMovies.addAll(movieObjects);
                mAddMovies = false;
            } else if (mAddSearchMovies) {
                if (movieObjects.size() == 0) {
                    isSearch = false;
                } else {
                    mMovies.addAll(movieObjects);
                    mAddSearchMovies = false;
                }
            } else if (isSearch) {
                mMovies = movieObjects;
            } else if (mIsClearedSearch) {
                mIsClearedSearch = false;
                mMovies = movieObjects;
            }

            if (mRecyclerView != null) {
                mMovieAdapter = new MoviesAdapter(getActivity(), mMovies);
                mMovieAdapter.setData(MoviesFragment.this);
                mRecyclerView.swapAdapter(mMovieAdapter, false);
            }

        }
    }

    // sets a zero position when click on search method from actionbar
    public void setPosition(int position) {
        mCurrentPosition = position;
        mRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_movies, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_filter_movies:
                showSortMenu(getActivity().findViewById(R.id.action_filter_movies));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        switch (item.getItemId()) {
            case R.id.popup_filter_popular:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_popular)).apply();
                break;
            case R.id.popup_filter_top:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_vote_average)).apply();
                break;
            case R.id.popup_filter_release:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_release_date)).apply();
                break;
        }

        return false;
    }

    /**
     * Shows PopupMenu on Filter button click in ActionBar
     *
     * @param view of the button itself
     */
    public void showSortMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_filter_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
