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
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    // Couldn't find more efficient way to use following variable then to make them global
    private MovieObjectAdapter movieAdapter;
    private ArrayList<MovieObject> movieList;
    private String mSort;
    private RecyclerView rv;
    private GridLayoutManager gridLayoutManager;
    private BroadcastReceiver networkStateReceiver;

    // Continuous viewing and progress bar variables
    private boolean loadingMore;
    private int currentPage = 1;
    private int currentPosition;
    private boolean addMovies = false;
    private boolean isSearch = false;
    private boolean addSearchMovies = false;
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

        EditText editText = (EditText) ((AppCompatActivity) getActivity()).findViewById(R.id.searchBar);

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
                    currentPage = 1;
                    if (afterChange.length() > beforeChange.length() || afterChange.length() + 3 < searchParameter.length()) {
                        searchParameter = s.toString();
                        isSearch = true;
                        updateMovieList();
                    } else if (afterChange.length() < 4) {
                        searchParameter = "";
                        isSearch = false;
                        if (!loadingMore) {
                            fetchMovies("");
                        }
                    }
                }
            });
        }

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("position");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        rv = (RecyclerView) rootview.findViewById(R.id.recyclerview);

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
                currentPosition = gridLayoutManager.findFirstVisibleItemPosition();

                if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() >= movieList.size() - 8 && isOnline(getContext())) {

                    if (isSearch) {
                        currentPage++;
                        addSearchMovies = true;
                        updateMovieList();
                    } else if (searchParameter.length() == 0) {
                        currentPage++;
                        addMovies = true;
                        updateMovieList();
                    }
                }
            }
        });

        rv.setAdapter(movieAdapter);

        return rootview;
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
                if (activeNetInfo != null) {
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
    private void updateMovieList() {
        String sort = Utility.getSort(getActivity());

        // Checks if settings were changed
        if (!sort.equals(mSort)) {
            // fetches new data
            currentPage = 1;
            fetchMovies(sort);

            // updates global settings variable
            mSort = sort;
            setActionbarTitle();

        } else if (movieList == null) {
            currentPage = 1;
            // fetches new data
            fetchMovies(sort);
        } else if (movieList.isEmpty()) {
            currentPage = 1;
            fetchMovies(sort);
        } else if (addMovies) {
            fetchMovies(sort);
        } else if (isSearch) {
            fetchMovies(searchParameter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        rv.scrollToPosition(currentPosition);
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

        ArrayList<MovieObject> movies;

        try {
            FetchMovie fetchMovie = new FetchMovie(getContext());

            movies = fetchMovie.execute(searchParameter).get();
            if (movies == null) {
                movieList = new ArrayList<>();
            } else if (addMovies) {
                movieList.addAll(movies);
                addMovies = false;
            } else if (addSearchMovies) {
                if (movies.size() == 0) {
                    isSearch = false;
                } else {
                    movieList.addAll(movies);
                    addSearchMovies = false;
                }
            } else if (isSearch || movieList == null || sort.equals("")) {
                movieList = movies;
            }
        } catch (ExecutionException e) {
            Log.e(LOG_TAG, "error");
        } catch (InterruptedException e2) {
            Log.e(LOG_TAG, "error" + e2);
        }

        movieAdapter = new MovieObjectAdapter(getActivity(), movieList);
        if (rv != null) {
            rv.swapAdapter(movieAdapter, false);
        }
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

    public class FetchMovie extends AsyncTask<String, Void, ArrayList<MovieObject>> {

        private final String LOG_TAG = FetchMovie.class.getSimpleName();

        private final Context mContext;

        public FetchMovie(Context context) {
            mContext = context;
        }

        /**
         * AsyncTask to fetch data on background thread
         *
         * @param params doesn't take any parameters yet, gets sort from SharedPreferences
         * @return array of MovieObjects
         */
        protected ArrayList<MovieObject> doInBackground(String... params) {

            URL url;

            if (isSearch) {
                url = Utility.getSearchURL(searchParameter, currentPage);
            } else {
                url = Utility.getUrl(currentPage, mContext);
            }

            loadingMore = true;

            // Network Client
            OkHttpClient client = new OkHttpClient();

            // Will contain the raw JSON response as a string.
            String movieJsonStr = "";

            try {
                // Create the request to OpenWeatherMap, and open the connection
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

        @Override
        protected void onPostExecute(ArrayList<MovieObject> movieObjects) {
            // Check the flag that activity is over
            loadingMore = false;
        }
    }

}
