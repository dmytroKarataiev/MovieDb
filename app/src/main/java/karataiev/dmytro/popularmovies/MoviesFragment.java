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
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import karataiev.dmytro.popularmovies.adapters.MoviesAdapter;
import karataiev.dmytro.popularmovies.interfaces.ItemClickListener;
import karataiev.dmytro.popularmovies.interfaces.ScrollableFragment;
import karataiev.dmytro.popularmovies.model.MovieObject;
import karataiev.dmytro.popularmovies.utils.Utility;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment
        implements ItemClickListener<MovieObject, View>, ScrollableFragment,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MoviesFragment.class.getSimpleName();

    // COnstants for SaveInst
    public static final String SAVE_MOVIES = "movies";
    public static final String SAVE_POS = "position";
    public static final String SAVE_SEARCH = "page";
    public static final String SAVE_PAGE = "search";

    // Couldn't find more efficient way to use following variable then to make them global
    private MoviesAdapter movieAdapter;
    private List<MovieObject> movieList;
    private String mSort;

    @BindView(R.id.recyclerview) RecyclerView mRecyclerView;
    Unbinder mUnbinder;

    private GridLayoutManager gridLayoutManager;

    private BroadcastReceiver networkStateReceiver;
    private boolean networkRestored;

    // Continuous viewing and progress bar variables
    private int currentPage = 1;
    private int currentPosition;
    private boolean addMovies;
    private boolean isSearch;
    private boolean addSearchMovies;
    private boolean isClearedSearch;
    private String searchParameter = "";

    // RxAndroid EditText Subscription
    private Subscription _subscription;

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

        EditText editText = ButterKnife.findById(getActivity(), R.id.searchBar);
        if (editText != null) {

            _subscription = RxTextView.textChangeEvents(editText)
                    .debounce(400, TimeUnit.MILLISECONDS)
                    // filters onCreate event when there was nothing in the EditText,
                    // so the list of movies won't be updated
                    .filter(textViewTextChangeEvent -> !(textViewTextChangeEvent.count() == 0 &&
                            textViewTextChangeEvent.before() == 0))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(_getSearchObserver());
        }

    }

    private Observer<TextViewTextChangeEvent> _getSearchObserver() {
        return new Observer<TextViewTextChangeEvent>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "--------- onComplete");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "--------- Woops on error!");
            }

            @Override
            public void onNext(TextViewTextChangeEvent onTextChangeEvent) {
                searchParameter = onTextChangeEvent.view().getText().toString();
                Log.d(TAG, "Searching for: " + searchParameter);
                isSearch = true;

                if (onTextChangeEvent.view().getText().length() < 4) {
                    searchParameter = "";
                    isSearch = false;
                    isClearedSearch = true;
                }
                currentPage = 1;
                currentPosition = 1;
                updateMovieList();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Initializes global mSort with SharedPreferences of sort
        mSort = Utility.getSort(getContext());

        // If movies were fetched - re-uses data
        if (savedInstanceState == null || !savedInstanceState.containsKey(SAVE_MOVIES)) {
            updateMovieList();
        } else {
            movieList = savedInstanceState.getParcelableArrayList(SAVE_MOVIES);
            currentPosition = savedInstanceState.getInt(SAVE_POS);
            currentPage = savedInstanceState.getInt(SAVE_PAGE);
            searchParameter = savedInstanceState.getString(SAVE_SEARCH);
        }

        mUnbinder = ButterKnife.bind(this, rootView);

        // Scale GridView according to the screen size
        gridLayoutManager = new GridLayoutManager(mRecyclerView.getContext(),
                Utility.screenSize(getContext())[3]);

        mRecyclerView.setLayoutManager(gridLayoutManager);

        movieAdapter = new MoviesAdapter(getActivity(), movieList);
        movieAdapter.setData(this);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (movieList != null) {
                    currentPosition = gridLayoutManager.findFirstVisibleItemPosition();

                    if (((gridLayoutManager.findFirstCompletelyVisibleItemPosition() >= movieList.size() - 8
                            || gridLayoutManager.findLastVisibleItemPosition() >= movieList.size() - 8)
                            && Utility.isOnline(getContext()))) {

                        if (searchParameter.length() > 0) {
                            addSearchMovies = true;
                        } else {
                            addMovies = true;
                        }
                        currentPage++;
                        updateMovieList();
                    }
                }
            }
        });

        mRecyclerView.setAdapter(movieAdapter);
        mRecyclerView.smoothScrollToPosition(currentPosition);

        // TODO: 8/28/16 move to activity 
        // BroadcastReceiver to get info about network connection
        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

                // Updates screen on network connection if nothing was on the screen
                if (activeNetInfo != null && movieList == null) {
                    networkRestored = true;
                    updateMovieList();
                }
            }
        };
        // Starts receiver
        startListening();

        return rootView;
    }

    /**
     * Method to register BroadcastReceiver
     */
    private void startListening() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkStateReceiver, filter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Saves movies so we don't need to re-download them
        outState.putParcelableArrayList(SAVE_MOVIES, (ArrayList<MovieObject>) movieList);
        outState.putInt(SAVE_POS, currentPosition);
        outState.putInt(SAVE_PAGE, currentPage);
        outState.putString(SAVE_SEARCH, searchParameter);
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
                currentPage = 1;
                fetchMovies(sort);
                // updates global settings variable
                setActionbarTitle();

            } else if (movieList == null || movieList.isEmpty()) {
                currentPage = 1;
                // fetches new data
                fetchMovies(sort);
            } else if (addMovies || isClearedSearch) {
                fetchMovies("");
            } else if (isSearch) {
                fetchMovies(searchParameter);
            } else if (networkRestored) {
                fetchMovies(sort);
                networkRestored = false;
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
        getActivity().unregisterReceiver(networkStateReceiver);
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
        FetchMovies fetchMovie = new FetchMovies(isSearch, currentPage);
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
        mRecyclerView.smoothScrollToPosition(0);
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
        private int currentPage;
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
                movieList = movieObjects;
            } else if (movieObjects == null || movieList == null) {
                movieList = new ArrayList<>();
            } else if (addMovies) {
                movieList.addAll(movieObjects);
                addMovies = false;
            } else if (addSearchMovies) {
                if (movieObjects.size() == 0) {
                    isSearch = false;
                } else {
                    movieList.addAll(movieObjects);
                    addSearchMovies = false;
                }
            } else if (isSearch) {
                movieList = movieObjects;
            } else if (isClearedSearch) {
                isClearedSearch = false;
                movieList = movieObjects;
            }

            if (mRecyclerView != null) {
                movieAdapter = new MoviesAdapter(getActivity(), movieList);
                movieAdapter.setData(MoviesFragment.this);
                mRecyclerView.swapAdapter(movieAdapter, false);
            }

        }
    }

    // sets a zero position when click on search method from actionbar
    public void setPosition(int position) {
        currentPosition = position;
        mRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (_subscription != null && !_subscription.isUnsubscribed()) {
            _subscription.unsubscribe();
        }
        mUnbinder.unbind();
    }
}
