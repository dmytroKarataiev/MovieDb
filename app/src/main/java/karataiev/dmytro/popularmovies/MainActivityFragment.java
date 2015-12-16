package karataiev.dmytro.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;
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
    private MovieObject[] movies;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        // Main object on the screen - grid with posters
        gridView = (GridView) rootview.findViewById(R.id.movies_grid);

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
    private void updateSort() {
        String sort = Utility.getSort(getActivity());

        // Checks if settings were changed
        if (!sort.equals(mSort)) {

            // fetches new data
            fetchMovies(sort);

            // clears adapter, updates data, notifies and sets to grid view
            movieAdapter.clear();
            movieAdapter.addAll(movieList);
            movieAdapter.notifyDataSetChanged();
            gridView.invalidateViews();
            gridView.setAdapter(movieAdapter);

            // updates global settings variable
            mSort = sort;

        } else if (movieList == null) {
            // fetches new data
            fetchMovies(sort);
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

        try {
            FetchMovie fetchMovie = new FetchMovie(getContext());

            movies = fetchMovie.execute(sort).get();
            if (movies == null) {
                movieList = new ArrayList<>();
            } else {
                movieList = new ArrayList<>(Arrays.asList(movies));
            }
        } catch (ExecutionException e) {
            Log.v(LOG_TAG, "error");
        } catch (InterruptedException e2) {
            Log.v(LOG_TAG, "error" + e2);
        }

    }
}
