package karataiev.dmytro.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
    private GridView gridView;

//    // Network status variables and methods (to stop fetching the data if the phone is offline
//    private boolean isOnline(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
//    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface CallbackFromFavorites {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(MovieObject movieObject);
    }

    public FavoritesActivityFragment() { }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_favorites, container, false);

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

                Cursor cursor = (Cursor) gridView.getItemAtPosition(position);

                MovieObject movie = Utility.makeMovieFromCursor(cursor);

                //int posterIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_IMAGE);
                //int backdropIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_FULL_IMAGE);
                //byte[] posterBytes = cursor.getBlob(posterIndex);
                //byte[] backdropBytes = cursor.getBlob(backdropIndex);

                ((CallbackFromFavorites) getContext()).onItemSelected(movie);

//                Intent intent = new Intent(getActivity(), DetailActivity.class)
//                        .putExtra("movie", movie)
//                        .putExtra("poster", posterBytes)
//                        .putExtra("backdrop", backdropBytes);
//                startActivity(intent);

            }
        });

        gridView.setAdapter(movieAdapter);

        return rootview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
