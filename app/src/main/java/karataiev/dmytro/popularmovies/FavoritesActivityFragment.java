package karataiev.dmytro.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import karataiev.dmytro.popularmovies.database.MoviesContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavoritesActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = FavoritesActivityFragment.class.getSimpleName();

    // Cursor loader variables
    private FavoritesAdapter movieAdapter;
    private static final int CURSOR_LOADER_ID = 0;

    // Couldn't find more efficient way to use following variable then to make them global
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
    private int currentPosition = 0;
    private boolean addMovies = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        Cursor c =
                getActivity().getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                        new String[]{MoviesContract.MovieEntry._ID},
                        null,
                        null,
                        null);
        if (c.getCount() == 0){
            //insertData();
            Log.v(LOG_TAG, "ZERO " + c.getCount());
        } else {
            Log.v(LOG_TAG, "Cursor " + c.getCount());
        }
        // initialize loader
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

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

        super.onActivityCreated(savedInstanceState);
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
        movieAdapter = new FavoritesAdapter(getActivity(), null, 0, CURSOR_LOADER_ID);

        // onClick activity which launches detailed view
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor currentPoster = (Cursor) gridView.getItemAtPosition(position);

                MovieObject movie = new MovieObject();

                int title = currentPoster.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE);

                movie.title = currentPoster.getString(title);

                if (currentPoster != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra("movie", movie);
                    startActivity(intent);
                }
            }
        });

        gridView.setAdapter(movieAdapter);

        // Listenes to your scroll activity and adds posters if you've reached the end of the screen
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen == totalItemCount && !loadingMore && isOnline(getContext())) {
                    currentPage++;
                    addMovies = true;
                }

            }
        });


        return rootview;
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
        // Saves movies so we don't need to re-download them
        //outState.putParcelableArrayList("movies", movieList);
        //outState.putInt("position", currentPosition);
        //outState.putInt("page", currentPage);

        super.onSaveInstanceState(outState);
    }

    /**
     * Method to update UI when settings changed
     */
    private void updateMovieList() {
        String sort = Utility.getSort(getActivity());
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


    // Attach loader to our flavors database query
    // run when loader is initialized
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
    }

    // Set the cursor in our CursorAdapter once the Cursor is loaded
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
    }

    // reset CursorAdapter on Loader Reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        movieAdapter.swapCursor(null);
    }
}
