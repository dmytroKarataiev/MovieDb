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
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import karataiev.dmytro.popularmovies.adapters.PagerAdapter;
import karataiev.dmytro.popularmovies.interfaces.ScrollableFragment;
import karataiev.dmytro.popularmovies.ui.ZoomOutPageTransformer;
import karataiev.dmytro.popularmovies.utils.Utility;

/**
 * Future main activity screen with tabs.
 * Created by karataev on 6/6/16.
 */
public class PagerActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = PagerActivity.class.getSimpleName();

    public static final int FRAGMENT_MOVIES = 0;
    public static final int FRAGMENT_FAVORITES = 1;
    public static final int FRAGMENT_SERIES = 2;
    public static final int FRAGMENT_ACTORS = 3;

    @BindView(R.id.sliding_tabs)
    TabLayout mTab;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindColor(R.color.tab_item_selected)
    int mColorSelected;
    @BindColor(R.color.tab_item_unselected)
    int mColorUnSelected;

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

        mPagerAdapter.addFragment(MoviesFragment.newInstance(), getString(R.string.title_movies));
        mPagerAdapter.addFragment(FavoritesFragment.newInstance(), getString(R.string.title_favorites));
        mPagerAdapter.addFragment(TvFragment.newInstance(), getString(R.string.title_tv));
        mPagerAdapter.addFragment(PersonFragment.newInstance(), getString(R.string.title_actors));

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount());

        // zoom effect on swipe
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        mTab.setupWithViewPager(mViewPager);

        setTabImages();

        setScrollToTop();

        // TODO: 7/28/16 add parallax sroll to the tablayout viw setTranslation 

    }

    /**
     * Scrolls the fragment to the top of a list.
     */
    private void setScrollToTop() {
        mToolbar.setOnClickListener(v -> {
            Fragment fragment = mPagerAdapter.getItem(mViewPager.getCurrentItem());
            if (fragment instanceof ScrollableFragment) {
                ((ScrollableFragment) fragment).scrollToTop();
            }
        });
    }

    /**
     * Method to set tab images and highlights on tab switching
     */
    private void setTabImages() {
        if (mTab != null) {

            int[] iconSet = {
                    R.drawable.ic_movie_white,
                    R.drawable.ic_star_white,
                    R.drawable.ic_subscriptions_white,
                    R.drawable.ic_people_white,
                    R.drawable.ic_movie_white,
                    R.drawable.ic_star_white,
                    R.drawable.ic_subscriptions_white,
                    R.drawable.ic_people_white
            };

            // Set custom layouts for each Tab
            for (int i = 0, n = mTab.getTabCount(); i < n; i++) {
                TabLayout.Tab tabLayout = mTab.getTabAt(i);

                if (tabLayout != null) {
                    tabLayout.setCustomView(R.layout.pager_tab_layout);
                    View customView = tabLayout.getCustomView();

                    if (customView != null) {
                        ImageView imageView = ButterKnife.findById(customView, R.id.tab_item_image);
                        TextView textView = ButterKnife.findById(customView, R.id.tab_item_text);
                        textView.setText(tabLayout.getText());

                        if (i == 0) {
                            textView.setTextColor(mColorSelected);
                            imageView.setImageResource(iconSet[i + iconSet.length / 2]);
                        } else {
                            textView.setTextColor(mColorUnSelected);
                            imageView.setImageResource(iconSet[i]);
                        }
                    }
                }
            }

            // Highlight image and text on selection
            mTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    setTitle();
                    View customView = tab.getCustomView();
                    if (customView != null) {
                        ImageView imageView = ButterKnife.findById(customView, R.id.tab_item_image);
                        imageView.setImageResource(iconSet[tab.getPosition() + iconSet.length / 2]);

                        TextView textView = ButterKnife.findById(customView, R.id.tab_item_text);
                        textView.setTextColor(mColorSelected);
                    }
                    mViewPager.setCurrentItem(tab.getPosition());
                    setTitle();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    View customView = tab.getCustomView();
                    if (customView != null) {
                        ImageView imageView = ButterKnife.findById(customView, R.id.tab_item_image);
                        imageView.setImageResource(iconSet[tab.getPosition()]);

                        TextView textView = ButterKnife.findById(customView, R.id.tab_item_text);
                        textView.setTextColor(mColorUnSelected);
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    Fragment fragment = mPagerAdapter.getItem(mViewPager.getCurrentItem());
                    if (fragment instanceof ScrollableFragment) {
                        ((ScrollableFragment) fragment).scrollToTop();
                    }
                }
            });

        }
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
                case FRAGMENT_MOVIES:
                    mToolbar.setTitle(Utility.getSortReadable(this));
                    break;
                case FRAGMENT_FAVORITES:
                    mToolbar.setTitle(getString(R.string.title_favorites));
                    break;
                case FRAGMENT_SERIES:
                    mToolbar.setTitle(getString(R.string.title_tv));
                    break;
                case FRAGMENT_ACTORS:
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

        MoviesFragment moviesFragment = null;

        if (mViewPager.getCurrentItem() == 0) {
            moviesFragment = (MoviesFragment) mPagerAdapter.getItem(0);
        }

        switch (item.getItemId()) {
            case R.id.popup_filter_popular:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_popular)).apply();
                break;
            case R.id.popup_filter_votes:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_vote_average)).apply();
                break;
            case R.id.popup_filter_release:
                sharedPreferences.edit().putString(getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_release_date)).apply();
                break;
        }

        if (moviesFragment != null) {
            moviesFragment.setPosition(0);
            moviesFragment.updateMovieList();
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
