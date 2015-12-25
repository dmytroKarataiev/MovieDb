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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

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
    private GridView gridView;

    // Network status variables and methods (to stop fetching the data if the phone is offline
    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private BroadcastReceiver networkStateReceiver;

    // Continuous viewing and progress bar variables
    private boolean loadingMore;
    private int currentPage = 1;
    private int currentPosition;
    private boolean addMovies = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionbarTitle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        // Main object on the screen - grid with posters
        gridView = (GridView) rootview.findViewById(R.id.movies_grid);

        // Scale GridView according to the screen size
        int[] screenSize = Utility.screenSize(getContext());
        int columns = screenSize[3];
        int posterWidth = screenSize[4];

        gridView.setNumColumns(columns);
        gridView.setColumnWidth(posterWidth);

        // Adapter which adds movies to the grid
        movieAdapter = new MovieObjectAdapter(getActivity(), movieList);

        // onClick activity which launches detailed view
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {

                MovieObject currentPoster = (MovieObject) adapterView.getItemAtPosition(position);

                if (currentPoster != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra("movie", currentPoster);
                    startActivity(intent);
                }
            }
        });

        gridView.setAdapter(movieAdapter);

        // Listenes to your scroll activity and adds posters if you've reached the end of the screen
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
                    currentPosition = gridView.getFirstVisiblePosition();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;

                if (lastInScreen == totalItemCount && !loadingMore && isOnline(getContext())) {
                    currentPage++;
                    addMovies = true;
                    updateMovieList();
                }
            }
        });


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
            //Log.v(LOG_TAG, "Sort changes");
            // fetches new data
            currentPage = 1;
            fetchMovies(sort);

            // clears adapter, updates data, notifies and sets to grid view
            redraw();

            // updates global settings variable
            mSort = sort;

        } else if (movieList == null) {
            //Log.v(LOG_TAG, "Movie list null");
            currentPage = 1;
            // fetches new data
            fetchMovies(sort);
        } else if (movieList.isEmpty()) {
            //Log.v(LOG_TAG, "Movie list is empty");
            currentPage = 1;
            fetchMovies(sort);
            redraw();
        } else if (addMovies) {
            //Log.v(LOG_TAG, "Scroll to the end");
            fetchMovies(sort);
            redraw();
        } else {
            redraw();
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

        ArrayList<MovieObject> movies;

        try {
            FetchMovie fetchMovie = new FetchMovie(getContext());

            movies = fetchMovie.execute(sort).get();
            if (movies == null) {
                movieList = new ArrayList<>();
            } else if (addMovies) {
                movieList.addAll(movies);
                addMovies = false;
            } else {
                movieList = movies;
            }
        } catch (ExecutionException e) {
            Log.e(LOG_TAG, "error");
        } catch (InterruptedException e2) {
            Log.e(LOG_TAG, "error" + e2);
        }
    }

    /**
     * Method to redraw GridView, invoked from updateMovieList() when movieList is empty or settings were changed
     */
    private void redraw() {

        if (currentPosition == 0) {
            currentPosition = gridView.getFirstVisiblePosition();
        }

        movieAdapter = new MovieObjectAdapter(getActivity(), movieList);

        gridView.setAdapter(movieAdapter);
        gridView.setSelection(currentPosition);

        setActionbarTitle();
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

            loadingMore = true;

            // Network Client
            OkHttpClient client = new OkHttpClient();

            // Will contain the raw JSON response as a string.
            String movieJsonStr = "";

            try {
                URL url = Utility.getUrl(currentPage, mContext);
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
