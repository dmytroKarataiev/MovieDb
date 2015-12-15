package karataiev.dmytro.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieObjectAdapter movieAdapter;
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        FetchMovie fetchMovie = new FetchMovie(getContext());
        MovieObject[] movies = null;

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

        if (movies != null) {
            movieAdapter = new MovieObjectAdapter(getActivity(), Arrays.asList(movies));
            GridView gridView = (GridView) rootview.findViewById(R.id.movies_grid);
            gridView.setAdapter(movieAdapter);
        }

        return rootview;
    }
}
