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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import karataiev.dmytro.popularmovies.adapters.PagerAdapter;
import karataiev.dmytro.popularmovies.ui.ZoomOutPageTransformer;

/**
 * Future main activity screen with tabs.
 * Created by karataev on 6/6/16.
 */
public class PagerActivity extends AppCompatActivity {

    @BindView(R.id.sliding_tabs)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        ButterKnife.bind(this);

        initPager();
    }

    /**
     * Initialises a ViewPager and a TabLayout with a custom indicator
     * sets listeners to them
     */
    private void initPager() {
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(MainFragment.newInstance(), getString(R.string.title_movies));
        pagerAdapter.addFragment(FavoritesFragment.newInstance(), getString(R.string.title_favorites));
        pagerAdapter.addFragment(TvFragment.newInstance(), getString(R.string.title_tv));
        pagerAdapter.addFragment(PersonFragment.newInstance(), getString(R.string.title_actors));

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(pagerAdapter.getCount());

        // zoom effect on swipe
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        mTabLayout.setupWithViewPager(mViewPager);

        // on second click on tab - scroll to the top if in TasksFragment
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
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
        }

        return super.onOptionsItemSelected(item);
    }
}
