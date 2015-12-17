package karataiev.dmytro.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
    BroadcastReceiver networkStateReceiver;
    private ProgressBar linlaProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        // Main object on the screen - grid with posters
        gridView = (GridView) rootview.findViewById(R.id.movies_grid);

        linlaProgressBar = (ProgressBar) rootview.findViewById(R.id.linlaProgressBar);
        linlaProgressBar.setVisibility(View.VISIBLE);

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

        // ONSCROLLLISTENER
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                Log.v(LOG_TAG, " " + lastInScreen);

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
            updateSort();
        } else {
            movieList = savedInstanceState.getParcelableArrayList("movies");
        }

        // BroadcastReceiver to get info about network connection
        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

                // Updates screen on network connection if nothing was on the screen
                if (activeNetInfo != null) {
                    Toast.makeText(context, "Active Network Type : " + activeNetInfo.getTypeName(), Toast.LENGTH_SHORT).show();
                    updateSort();
                }
            }
        };
        // Starts receiver
        startListening();
    }

    /**
     * Method to register BroadcastReceiver
     */
    public void startListening() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkStateReceiver, filter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Saves movies so we don't need to re-download them
        outState.putParcelableArrayList("movies", movieList);
        super.onSaveInstanceState(outState);
    }

    /**
     * Method to update UI when settings changed
     */
    public void updateSort() {
        String sort = Utility.getSort(getActivity());

        // Checks if settings were changed
        if (!sort.equals(mSort)) {
            // fetches new data
            fetchMovies(sort);

            // clears adapter, updates data, notifies and sets to grid view
            redraw();

            // updates global settings variable
            mSort = sort;

        } else if (movieList == null) {
            // fetches new data
            fetchMovies(sort);
        } else if (movieList.isEmpty()) {
            fetchMovies(sort);
            redraw();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSort();
    }

    /**
     * Method to fetch movies and if there is no network to provide empty MovieObject[] list
     * so the App won't crash
     * @param sort to fetch data sorted with the parameter
     */
    private void fetchMovies(String sort) {

        ArrayList<MovieObject> movies;

        try {
            FetchMovie fetchMovie = new FetchMovie(getContext());

            movies = fetchMovie.execute(sort).get();
            if (movies == null) {
                movieList = new ArrayList<>();
            } else {
                movieList = movies;
            }
        } catch (ExecutionException e) {
            Log.v(LOG_TAG, "error");
        } catch (InterruptedException e2) {
            Log.v(LOG_TAG, "error" + e2);
        }

    }

    /**
     * Method to redraw GridView, invoked from updateSort() when movieList is empty or settings were changed
     */
    private void redraw() {
        movieAdapter.clear();
        movieAdapter.addAll(movieList);
        movieAdapter.notifyDataSetChanged();
        gridView.invalidateViews();
        gridView.setAdapter(movieAdapter);
    }

    public class FetchMovie extends AsyncTask<String, Void, ArrayList<MovieObject>> {

        private final String LOG_TAG = FetchMovie.class.getSimpleName();

        private final Context mContext;

        public FetchMovie(Context context) {
            mContext = context;
        }

        /**
         * AsyncTask to fetch data on background thread
         * @param params doesn't take any parameters yet, gets sort from SharedPreferences
         * @return array of MovieObjects
         */
        protected ArrayList<MovieObject> doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.

            Log.v(LOG_TAG, "PARAMS " + params.length + " ");

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at Movie DB API page, at
                // http://docs.themoviedb.apiary.io/
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String QUERY_PARAM = "sort_by";
                final String PAGE_QUERY = "page";
                String PAGE = "1";

                // Gets preferred sort, by default: popularity.desc
                final String SORT = Utility.getSort(mContext);

                final String VOTERS = "vote_count.gte";
                final String VOTERS_MIN = "100";

                // Don't forget to add API key to the gradle.properties file
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, SORT)
                        .appendQueryParameter(PAGE_QUERY, PAGE)
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                // When sort on vote_average - gets movies with at least VOTERS_MIN votes
                if (SORT.contains("vote_average")) {
                    builtUri = builtUri.buildUpon()
                            .appendQueryParameter(VOTERS, VOTERS_MIN)
                            .build();
                }

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    line += "\n";
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

                return Utility.getMovieDataFromJSON(mContext, movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MovieObject> movieObjects) {
            // SHOW THE BOTTOM PROGRESS BAR (SPINNER) WHILE LOADING MORE PHOTOS
            linlaProgressBar.setVisibility(View.GONE);
        }
    }

}
