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


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieObjectAdapter(getActivity(), movieList);
        GridView gridView = (GridView) rootview.findViewById(R.id.movies_grid);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                MovieObject currentPoster = (MovieObject) adapterView.getItemAtPosition(position);
                Log.v(LOG_TAG, currentPoster.pathToImage + " " + currentPoster.name);

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

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {

            FetchMovie fetchMovie = new FetchMovie(getContext());
            try
            {
                movies = fetchMovie.execute().get();
            }
            catch (ExecutionException e) {
                Log.v(LOG_TAG, "error");
            }
            catch (InterruptedException e2) {
                Log.v(LOG_TAG, "error" + e2);
            }
            movieList = new ArrayList<>(Arrays.asList(movies));
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
}
