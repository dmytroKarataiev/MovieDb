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

package com.adkdevelopment.moviesdb.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.adkdevelopment.moviesdb.R;
import com.adkdevelopment.moviesdb.data.database.MoviesContract;
import com.adkdevelopment.moviesdb.data.model.MovieObject;
import com.adkdevelopment.moviesdb.ui.adapters.FavoritesAdapter;
import com.adkdevelopment.moviesdb.ui.base.BaseFragment;
import com.adkdevelopment.moviesdb.ui.interfaces.ScrollableFragment;
import com.adkdevelopment.moviesdb.utils.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A fragment which show a list of favorite movies, tv series.
 */
public class FavoritesFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, ScrollableFragment {

    // Cursor loader variables
    private FavoritesAdapter mFavoritesAdapter;
    private static final int CURSOR_LOADER_ID = 0;

    // Couldn't find more efficient way to use following variable then to make them global
    @BindView(R.id.movies_grid) GridView mGridView;
    Unbinder mUnbinder;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
        mFavoritesAdapter = new FavoritesAdapter(getActivity());

        // onClick activity which launches detailed view
        mGridView.setOnItemClickListener((parent, view, position, id) -> {

            Cursor cursor = (Cursor) mGridView.getItemAtPosition(position);
            MovieObject movie = Utility.makeMovieFromCursor(cursor);

            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra(MovieObject.MOVIE_OBJECT, movie);
            startActivity(intent);
        });

        // gridview scroll effect on ActionBar
        ViewCompat.setNestedScrollingEnabled(mGridView, true);

        mGridView.setAdapter(mFavoritesAdapter);

        return rootview;
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

    @Override
    public void scrollToTop() {
        if (mGridView != null) {
            mGridView.smoothScrollToPosition(0);
        }
    }
}
