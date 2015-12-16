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

    private MovieObject[] movies;
    private MovieObjectAdapter movieAdapter;
    private ArrayList<MovieObject> movieList;
    private String mSort;
    private GridView gridView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootview.findViewById(R.id.movies_grid);

        movieAdapter = new MovieObjectAdapter(getActivity(), movieList);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
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
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mSort = Utility.getSort(getContext());

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {

            updateSort();
        }
        else {
            movieList = savedInstanceState.getParcelableArrayList("movies");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieList);
        super.onSaveInstanceState(outState);
    }

    /**
     * Method to update UI when settings changed
     */
    public void updateSort()
    {
        String sort = Utility.getSort(getActivity());

        // Checks if settings were changed
        if (!sort.equals(mSort)) {
            try
            {
                // fetches new data
                FetchMovie fetchMovie = new FetchMovie(getContext());
                movies = fetchMovie.execute(sort).get();
                movieList = new ArrayList<>(Arrays.asList(movies));

                // clears adapter, updates data, notifies and sets to grid view
                movieAdapter.clear();
                movieAdapter.addAll(movieList);
                movieAdapter.notifyDataSetChanged();
                gridView.invalidateViews();
                gridView.setAdapter(movieAdapter);

                // updates global settings variable
                mSort = sort;
            } catch (ExecutionException e) {
                Log.v(LOG_TAG, "error");
            } catch (InterruptedException e2) {
                Log.v(LOG_TAG, "error" + e2);
            }
        }
        else if (movieList == null) {
            try {
                // fetches new data
                FetchMovie fetchMovie = new FetchMovie(getContext());
                movies = fetchMovie.execute(sort).get();
                movieList = new ArrayList<>(Arrays.asList(movies));
            } catch (ExecutionException e) {
                Log.v(LOG_TAG, "error");
            } catch (InterruptedException e2) {
                Log.v(LOG_TAG, "error" + e2);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSort();
    }





}
