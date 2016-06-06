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

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import karataiev.dmytro.popularmovies.adapters.MoviesAdapter;
import karataiev.dmytro.popularmovies.model.MovieObject;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.CallbackFromAdapter,
        FavoritesFragment.CallbackFromFavorites, PopupMenu.OnMenuItemClickListener {

    private MainFragment mContent;
    private DetailFragment mDetailFragment;
    private final String FRAGMENT_TAG = "FFTAG";
    private final String DETAILFRAGMENT_TAG = "DFTAG";
    private final String FAVFRAGMENT_TAG = "FAVFR";

    // Two Pane variable
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            mContent = new MainFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, mContent, FRAGMENT_TAG).commit();
        } else {
            mContent = (MainFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        }

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                mDetailFragment = new DetailFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container, mDetailFragment, DETAILFRAGMENT_TAG)
                        .commit();
            }

        } else {
            mTwoPane = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_favorites:
                if (mTwoPane) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new FavoritesFragment(), FAVFRAGMENT_TAG)
                            .commit();

                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    }

                } else {
                    startActivity(new Intent(this, FavoritesActivity.class));
                    return true;
                }
                break;
            case android.R.id.home:
                // Add to Favorites Fragment Back button
                if (mTwoPane) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, mContent, FRAGMENT_TAG)
                            .commit();

                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                }
                break;
            case R.id.action_filter:
                showSortMenu(findViewById(R.id.action_filter));
                return true;
            case R.id.action_test:
                startActivity(new Intent(this, PagerActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows PopupMenu on Filter button click in ActionBar
     * @param view of the button itself
     */
    public void showSortMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_filter_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
    }

    @Override
    public void onItemSelected(MovieObject movieObject) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(MovieObject.MOVIE_OBJECT, movieObject);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();

        }  else {

            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(MovieObject.MOVIE_OBJECT, movieObject);

            // Check if a phone supports shared transitions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                // TODO: 5/6/16 fix sometimes incorrectly appearing poster
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(
                        this,
                        new Pair<>(mContent.getView().findViewWithTag(movieObject.getId()),
                                mContent.getView().findViewById(R.id.movie_poster).getTransitionName()),
                        new Pair<>(mContent.getView().findViewWithTag(movieObject.getId() + "1"),
                                mContent.getView().findViewById(R.id.movie_poster_favorite).getTransitionName()))
                        .toBundle();

                startActivity(intent, bundle);
            } else {
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentByTag(FAVFRAGMENT_TAG) != null) {
            fm.beginTransaction()
                    .replace(R.id.container, new MainFragment(), FRAGMENT_TAG)
                    .commit();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);

        switch (item.getItemId()) {
            case R.id.popup_filter_popular:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_key), "popularity.desc").apply();
                if (mainFragment != null) {
                    mainFragment.setPosition(0);
                    mainFragment.updateMovieList();
                }
                return true;
            case R.id.popup_filter_votes:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_key), "vote_average.desc").apply();
                if (mainFragment != null) {
                    mainFragment.setPosition(0);
                    mainFragment.updateMovieList();
                }
                return true;
            case R.id.popup_filter_release:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_key), "release_date.desc").apply();
                if (mainFragment != null) {
                    mainFragment.setPosition(0);
                    mainFragment.updateMovieList();
                }
                return true;
            default:
                return false;
        }
    }
}
