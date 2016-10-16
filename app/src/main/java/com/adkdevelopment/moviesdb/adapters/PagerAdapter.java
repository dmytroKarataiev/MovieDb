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

package com.adkdevelopment.moviesdb.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.adkdevelopment.moviesdb.FavoritesFragment;
import com.adkdevelopment.moviesdb.MoviesFragment;
import com.adkdevelopment.moviesdb.PagerActivity;
import com.adkdevelopment.moviesdb.PersonFragment;
import com.adkdevelopment.moviesdb.R;
import com.adkdevelopment.moviesdb.SeriesFragment;

import java.lang.ref.WeakReference;

/**
 * Simple Pager Adapter for fragments with Movies, Actors, TV Series, etc.
 * Created by karataev on 6/13/16.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private SparseArray<WeakReference<Fragment>> mFragments = new SparseArray<>();
    private Context mContext;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case PagerActivity.FRAGMENT_MOVIES:
                return MoviesFragment.newInstance();
            case PagerActivity.FRAGMENT_FAVORITES:
                return FavoritesFragment.newInstance();
            case PagerActivity.FRAGMENT_SERIES:
                return SeriesFragment.newInstance();
            case PagerActivity.FRAGMENT_ACTORS:
                return PersonFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return new int[]{
                PagerActivity.FRAGMENT_MOVIES,
                PagerActivity.FRAGMENT_FAVORITES,
                PagerActivity.FRAGMENT_SERIES,
                PagerActivity.FRAGMENT_ACTORS}.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case PagerActivity.FRAGMENT_MOVIES:
                return mContext.getString(R.string.title_movies);
            case PagerActivity.FRAGMENT_FAVORITES:
                return mContext.getString(R.string.title_favorites);
            case PagerActivity.FRAGMENT_SERIES:
                return mContext.getString(R.string.title_tv);
            case PagerActivity.FRAGMENT_ACTORS:
                return mContext.getString(R.string.title_actors);
            default:
                return mContext.getString(R.string.title_movies);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mFragments.put(position, new WeakReference<>(fragment));
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        if (mFragments.valueAt(position) != null) {
            return mFragments.valueAt(position).get();
        } else {
            return getItem(position);
        }
    }
}
