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

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                    // CursorAdapter returns a cursor at the correct position for getItem(), or null
                    // if it cannot seek to that position.
                    MovieObject cursor = (MovieObject) adapterView.getItemAtPosition(position);
                    Log.v(LOG_TAG, cursor.pathToImage + " " + cursor.name);

                    if (cursor != null) {

                        Intent intent = new Intent(getActivity(), DetailActivity.class)
                                .putExtra("path", cursor.pathToImage)
                                .putExtra("name", cursor.name);
                        startActivity(intent);
                    }
                }
            });

            gridView.setAdapter(movieAdapter);
        }

        return rootview;
    }
}
