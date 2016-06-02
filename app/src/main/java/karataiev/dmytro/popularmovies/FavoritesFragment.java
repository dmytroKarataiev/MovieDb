/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016. Dmytro Karataiev
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

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
import android.widget.GridView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import karataiev.dmytro.popularmovies.adapters.FavoritesAdapter;
import karataiev.dmytro.popularmovies.database.MoviesContract;
import karataiev.dmytro.popularmovies.model.MovieObject;
import karataiev.dmytro.popularmovies.utils.Utility;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavoritesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Cursor loader variables
    private FavoritesAdapter mFavoritesAdapter;
    private static final int CURSOR_LOADER_ID = 0;

    // Couldn't find more efficient way to use following variable then to make them global
    @BindView(R.id.movies_grid) GridView mGridView;
    Unbinder mUnbinder;

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

    public FavoritesFragment() { }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_favorites, container, false);

        mUnbinder = ButterKnife.bind(this, rootview);

        // Scale GridView according to the screen size
        int[] screenSize = Utility.screenSize(getContext());
        int columns = screenSize[3];
        int posterWidth = screenSize[4];

        mGridView.setNumColumns(columns);
        mGridView.setColumnWidth(posterWidth);
        mGridView.setMinimumHeight((int) (posterWidth * 1.5));

        // Adapter which adds movies to the grid
        mFavoritesAdapter = new FavoritesAdapter(getActivity(), null, 0);

        // onClick activity which launches detailed view
        mGridView.setOnItemClickListener((parent, view, position, id) -> {

            Cursor cursor = (Cursor) mGridView.getItemAtPosition(position);

            MovieObject movie = Utility.makeMovieFromCursor(cursor);

            //  int posterIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_IMAGE);
            //  int backdropIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_FULL_IMAGE);
            //  byte[] posterBytes = cursor.getBlob(posterIndex);
            //  byte[] backdropBytes = cursor.getBlob(backdropIndex);

            ((CallbackFromFavorites) getContext()).onItemSelected(movie);

            //  Intent intent = new Intent(getActivity(), DetailActivity.class)
            //      .putExtra("movie", movie)
            //      .putExtra("poster", posterBytes)
            //      .putExtra("backdrop", backdropBytes);
            //  startActivity(intent);

        });

        mGridView.setAdapter(mFavoritesAdapter);

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
        mFavoritesAdapter.swapCursor(data);
    }

    // reset CursorAdapter on Loader Reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mFavoritesAdapter.swapCursor(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
