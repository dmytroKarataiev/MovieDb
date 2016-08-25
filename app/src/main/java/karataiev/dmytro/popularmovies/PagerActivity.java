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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import karataiev.dmytro.popularmovies.adapters.PagerAdapter;
import karataiev.dmytro.popularmovies.ui.ZoomOutPageTransformer;
import karataiev.dmytro.popularmovies.utils.Utility;

/**
 * Future main activity screen with tabs.
 * Created by karataev on 6/6/16.
 */
public class PagerActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = PagerActivity.class.getSimpleName();

    @BindView(R.id.sliding_tabs)
    TabLayout mTab;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        initPager();
    }

    /**
     * Initialises a ViewPager and a TabLayout with a custom indicator
     * sets listeners to them
     */
    private void initPager() {
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        mPagerAdapter.addFragment(MainFragment.newInstance(), getString(R.string.title_movies));
        mPagerAdapter.addFragment(FavoritesFragment.newInstance(), getString(R.string.title_favorites));
        mPagerAdapter.addFragment(TvFragment.newInstance(), getString(R.string.title_tv));
        mPagerAdapter.addFragment(PersonFragment.newInstance(), getString(R.string.title_actors));

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount());

        // zoom effect on swipe
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        mTab.setupWithViewPager(mViewPager);

        // on second click on tab - scroll to the top if in TasksFragment
        mTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                setTitle();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // TODO: 6/13/16 scroll to the top
            }
        });

        // TODO: 7/28/16 add parallax sroll to the tablayout viw setTranslation 

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // copy the behavior of the hardware back button
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_filter:
                showSortMenu(findViewById(R.id.action_filter));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle();
        Log.d(TAG, "onResume: ");
    }

    /**
     * Sets ActionBar title to the corresponding view
     */
    private void setTitle() {
        if (mToolbar != null && mTab != null) {

            switch (mTab.getSelectedTabPosition()) {
                case 0:
                    mToolbar.setTitle(Utility.getSortReadable(this));
                    break;
                case 1:
                    mToolbar.setTitle(getString(R.string.title_favorites));
                    break;
                case 2:
                    mToolbar.setTitle(getString(R.string.title_tv));
                    break;
                case 3:
                    mToolbar.setTitle(getString(R.string.title_actors));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pager, menu);
        return true;
    }

    // TODO: 8/24/16 hide filter menu in other fragments 
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        MainFragment mainFragment = null;

        if (mViewPager.getCurrentItem() == 0) {
            mainFragment = (MainFragment) mPagerAdapter.getItem(0);
        }

        // TODO: 8/24/16 add constants 
        switch (item.getItemId()) {
            case R.id.popup_filter_popular:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_key), "popularity.desc").apply();
                break;
            case R.id.popup_filter_votes:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_key), "vote_average.desc").apply();
                break;
            case R.id.popup_filter_release:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_key), "release_date.desc").apply();
                break;
        }

        if (mainFragment != null) {
            mainFragment.setPosition(0);
            mainFragment.updateMovieList();
            return true;
        }

        return false;
    }

    /**
     * Shows PopupMenu on Filter button click in ActionBar
     * @param view of the button itself
     */
    public void showSortMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(PagerActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_filter_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

}
