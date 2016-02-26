package karataiev.dmytro.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import karataiev.dmytro.popularmovies.AsyncTask.FetchMovies;
import karataiev.dmytro.popularmovies.AsyncTask.TaskCompleted;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements TaskCompleted {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    // Couldn't find more efficient way to use following variable then to make them global
    private MovieObjectAdapter movieAdapter;
    private ArrayList<MovieObject> movieList;
    private String mSort;
    private RecyclerView rv;
    private GridLayoutManager gridLayoutManager;
    private BroadcastReceiver networkStateReceiver;
    private boolean networkRestored;

    // Continuous viewing and progress bar variables
    private boolean loadingMore;
    private int currentPage = 1;
    private int currentPosition;
    private boolean addMovies = false;
    private boolean isSearch = false;
    private boolean addSearchMovies = false;
    private boolean isClearedSearch = false;
    private String searchParameter = "";
    private String beforeChange;
    private String afterChange;

    public MainActivityFragment() {
    }

    // Network status variables and methods (to stop fetching the data if the phone is offline
    private boolean isOnline(Context context) {

        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }

        return false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionbarTitle();
        EditText editText = (EditText) (getActivity()).findViewById(R.id.searchBar);

        if (editText != null) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    beforeChange = s.toString();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    afterChange = s.toString();

                    // avoid page change when field if empty
                    if (!beforeChange.equals(afterChange)) {
                        currentPage = 1;
                        currentPosition = 1;
                    }

                    if (afterChange.length() > beforeChange.length() || afterChange.length() + 3 < searchParameter.length() && afterChange.length() != 0) {
                        searchParameter = s.toString();
                        isSearch = true;
                        updateMovieList();
                    } else if (afterChange.length() < 4 && searchParameter.length() > 0) {
                        searchParameter = "";
                        isSearch = false;
                        isClearedSearch = true;

                        updateMovieList();
                    }
                }
            });
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        rv = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        // Scale GridView according to the screen size
        int[] screenSize = Utility.screenSize(getContext());
        int columns = screenSize[3];

        gridLayoutManager = new GridLayoutManager(rv.getContext(), columns);

        rv.setLayoutManager(gridLayoutManager);

        movieAdapter = new MovieObjectAdapter(getActivity(), movieList);

        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (movieList != null) {
                    currentPosition = gridLayoutManager.findFirstVisibleItemPosition();

                    if (((gridLayoutManager.findFirstCompletelyVisibleItemPosition() >= movieList.size() - 8
                            || gridLayoutManager.findLastVisibleItemPosition() >= movieList.size() - 8)
                            && isOnline(getContext()))) {

                        if (searchParameter.length() > 0) {
                            currentPage++;
                            addSearchMovies = true;
                            updateMovieList();
                        } else {
                            currentPage++;
                            addMovies = true;
                            updateMovieList();
                        }
                    }
                }

            }
        });

        rv.setAdapter(movieAdapter);
        rv.scrollToPosition(currentPosition);

        // iPhone-like scroll to the first position in the view on toolbar click
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rv.smoothScrollToPosition(0);
                }
            });
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializes global mSort with SharedPreferences of sort
        mSort = Utility.getSort(getContext());

        // If movies were fetched - re-uses data
        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            updateMovieList();
        } else {

            movieList = savedInstanceState.getParcelableArrayList("movies");
            currentPosition = savedInstanceState.getInt("position");
            currentPage = savedInstanceState.getInt("page");
        }

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
        outState.putParcelableArrayList("movies", movieList);
        outState.putInt("position", currentPosition);
        outState.putInt("page", currentPage);
    }

    /**
     * Method to update UI when settings changed
     */
    public void updateMovieList() {

        String sort = Utility.getSort(getContext());
        if (isOnline(getContext())) {
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
        updateMovieList();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(networkStateReceiver);
    }

    /**
     * Method to fetch movies and if there is no network to provide empty MovieObject ArrayList
     * so the App won't crash
     *
     * @param sort to fetch data sorted with the parameter
     */
    private void fetchMovies(String sort) {

        FetchMoviesFragment fetchMovie = new FetchMoviesFragment(getContext(), new TaskCompleted() {
            @Override
            public void onAsyncProgress(boolean progress) {
                loadingMore = progress;
            }
        }, isSearch, currentPage);

        fetchMovie.execute(sort);
    }

    /**
     * Method to set ActionBar title according to the sort criteria
     */
    private void setActionbarTitle() {

        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(Utility.getSortReadable(getContext()));
        }



    }

    @Override
    public void onAsyncProgress(boolean progress) {
        loadingMore = progress;
    }


    /**
     * Class to retrieve MovieObjects from JSON on background thread
     */
    public class FetchMoviesFragment extends AsyncTask<String, Void, ArrayList<MovieObject>> {

        private final String LOG_TAG = FetchMovies.class.getSimpleName();
        private Context mContext;
        private TaskCompleted listener;
        private boolean isSearch;
        private int currentPage;
        private String searchParams;

        public FetchMoviesFragment(Context context, TaskCompleted listener, boolean isSearch, int currentPage) {
            mContext = context;
            this.listener = listener;
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
                url = Utility.getUrl(currentPage, mContext);
            }

            // Network Client
            OkHttpClient client = new OkHttpClient();

            // Will contain the raw JSON response as a string.
            String movieJsonStr = "";

            if (listener != null) {
                listener.onAsyncProgress(true);
            }

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

                return Utility.getMoviesGSON(mContext, movieJsonStr);

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieObject> movieObjects) {
            // Check the flag that activity is over
            if (listener != null) {
                listener.onAsyncProgress(false);
            }

            if (searchParams.equals(mSort)) {
                movieList = movieObjects;
            } else if (movieObjects == null) {
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
            } else if (isSearch || movieList == null) {
                movieList = movieObjects;
            } else if (searchParams.equals("") && isClearedSearch) {
                movieList = movieObjects;
                isClearedSearch = false;
            }

            movieAdapter = new MovieObjectAdapter(getActivity(), movieList);

            if (rv != null) {
                rv.swapAdapter(movieAdapter, false);
                rv.smoothScrollToPosition(currentPosition);
            }
        }
    }

}
