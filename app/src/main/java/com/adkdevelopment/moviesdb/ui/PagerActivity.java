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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.adkdevelopment.moviesdb.R;
import com.adkdevelopment.moviesdb.ui.adapters.PagerAdapter;
import com.adkdevelopment.moviesdb.ui.base.BaseActivity;
import com.adkdevelopment.moviesdb.ui.behavior.ZoomOutPageTransformer;
import com.adkdevelopment.moviesdb.ui.interfaces.ScrollableFragment;
import com.adkdevelopment.moviesdb.ui.interfaces.SearchableFragment;
import com.adkdevelopment.moviesdb.utils.Utility;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.concurrent.TimeUnit;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Future main activity screen with tabs.
 * Created by karataev on 6/6/16.
 */
public class PagerActivity extends BaseActivity {

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
    @BindView(R.id.searchBar)
    EditText mEditText;

    @BindColor(R.color.tab_item_selected)
    int mColorSelected;
    @BindColor(R.color.tab_item_unselected)
    int mColorUnSelected;

    private PagerAdapter mPagerAdapter;

    // RxAndroid EditText Subscription
    private Subscription mSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        initPager();

        registerSearchPanel();
    }

    /**
     * Registers text watcher for the EditText.
     */
    private void registerSearchPanel() {
        mSubscription = RxTextView.textChangeEvents(mEditText)
                .debounce(400, TimeUnit.MILLISECONDS)
                // filters onCreate event when there was nothing in the EditText,
                // so the list of movies won't be updated
                .filter(textViewTextChangeEvent -> !(textViewTextChangeEvent.count() == 0 &&
                        textViewTextChangeEvent.before() == 0))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSearchObserver());
    }

    /**
     * Creates an observer for the RxTextView listener.
     * Checks if the fragment is of a Searchable type and sends a search request.
     *
     * @return text view change event.
     */
    private Observer<TextViewTextChangeEvent> getSearchObserver() {
        return new Observer<TextViewTextChangeEvent>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "--------- onComplete");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "--------- Woops on error!");
            }

            @Override
            public void onNext(TextViewTextChangeEvent onTextChangeEvent) {
                Fragment fragment = mPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
                if (fragment instanceof SearchableFragment) {
                    ((SearchableFragment) fragment)
                            .searchRequest(onTextChangeEvent.view().getText().toString());
                }
            }
        };
    }

    /**
     * Initialises a ViewPager and a TabLayout with a custom indicator
     * sets listeners to them
     */
    private void initPager() {
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), getBaseContext());

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        // zoom effect on swipe
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        mTab.setupWithViewPager(mViewPager);

        setTabImages();
        setScrollToTop();
        setActionbarItems();

        // TODO: 7/28/16 add parallax sroll to the tablayout viw setTranslation
    }

    /**
     * Depending on the position - shows correct menu item for filtering
     */
    private void setActionbarItems() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment fragment = mPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
                if (!(fragment instanceof SearchableFragment)) {
                    mEditText.setVisibility(View.GONE);
                } else {
                    mEditText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * Scrolls the fragment to the top of a list.
     */
    private void setScrollToTop() {
        mToolbar.setOnClickListener(v -> {
            Fragment fragment = mPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
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
                    R.drawable.ic_movie_unselected,
                    R.drawable.ic_star_unselected,
                    R.drawable.ic_subscriptions_unselected,
                    R.drawable.ic_people_unselected,
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
                    Fragment fragment = mPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
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
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
