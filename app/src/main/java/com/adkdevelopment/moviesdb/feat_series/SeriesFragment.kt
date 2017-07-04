/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2017. Dmytro Karataiev
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

package com.adkdevelopment.moviesdb.feat_series

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.adkdevelopment.moviesdb.R
import com.adkdevelopment.moviesdb.data.model.Consts
import com.adkdevelopment.moviesdb.data.model.TvObject
import com.adkdevelopment.moviesdb.feat_series.contracts.SeriesContract
import com.adkdevelopment.moviesdb.feat_series.presenters.SeriesPresenter
import com.adkdevelopment.moviesdb.ui.adapters.TvAdapter
import com.adkdevelopment.moviesdb.ui.base.BaseFragment
import com.adkdevelopment.moviesdb.ui.interfaces.ItemClickListener
import com.adkdevelopment.moviesdb.ui.interfaces.ScrollableFragment
import com.adkdevelopment.moviesdb.utils.Utility

/**
 * Fragment which shows a list of TV Series.
 * Created by karataev on 6/13/16.
 */
class SeriesFragment : BaseFragment(),
        SeriesContract.View,
        ItemClickListener<TvObject, View>,
        ScrollableFragment, PopupMenu.OnMenuItemClickListener {

    private var mSeries: List<TvObject>? = null

    private lateinit var mTvAdapter: TvAdapter
    private lateinit var mPresenter: SeriesPresenter
    private lateinit var mGridLayoutManager: GridLayoutManager

    @BindView(R.id.swipe_refresh_layout)
    internal lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    @BindView(R.id.recyclerview)
    internal lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.list_empty_text)
    internal lateinit var mListEmpty: TextView
    @BindView(R.id.progress_bar)
    internal lateinit var mProgressBar: ProgressBar

    private lateinit var mUnbinder: Unbinder

    private var mCurrentPosition: Int = 0
    private var mCurrentPage = 1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_main, container, false)
        setHasOptionsMenu(true)

        mUnbinder = ButterKnife.bind(this, rootView)

        // Scale GridView according to the screen size
        mGridLayoutManager = GridLayoutManager(mRecyclerView.context,
                Utility.screenSize(context)[3])

        mTvAdapter = TvAdapter(context, null, this@SeriesFragment)
        mRecyclerView.layoutManager = mGridLayoutManager
        mRecyclerView.adapter = mTvAdapter

        mPresenter = SeriesPresenter(context)
        mPresenter.attachView(this)

        // If movies were fetched - re-uses data
        if (savedInstanceState == null || !savedInstanceState.containsKey(BaseFragment.SAVE_RESULTS)) {
            mPresenter.requestData(mCurrentPage)
        } else {
            mSeries = savedInstanceState.getParcelableArrayList<TvObject>(BaseFragment.SAVE_RESULTS)
            mCurrentPosition = savedInstanceState.getInt(BaseFragment.SAVE_POS)
            mCurrentPage = savedInstanceState.getInt(BaseFragment.SAVE_PAGE)
            showData(((mSeries as ArrayList<TvObject>?)!!), mCurrentPage)

            mRecyclerView.scrollToPosition(mCurrentPosition)
        }

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                mCurrentPosition = mGridLayoutManager!!.findFirstVisibleItemPosition()
                if (mGridLayoutManager!!.findLastVisibleItemPosition() >= mTvAdapter!!.itemCount - 5) {
                    mPresenter.requestData(++mCurrentPage)
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener {
            // on force refresh downloads all data
            mCurrentPage = 1
            mPresenter.requestData(mCurrentPage)
        }

        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        // Saves movies so we don't need to re-download them
        outState!!.putParcelableArrayList(BaseFragment.SAVE_RESULTS, mSeries as ArrayList<TvObject>?)
        outState.putInt(BaseFragment.SAVE_POS, mCurrentPosition)
        outState.putInt(BaseFragment.SAVE_PAGE, mCurrentPage)
    }

    override fun onDetach() {
        super.onDetach()
        mPresenter.detachView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
    }

    override fun onItemClicked(item: TvObject, view: View) {
        val intent = Intent(context, TvDetailActivity::class.java)
        intent.putExtra(Consts.TV_EXTRA, item)
        startActivity(intent)
    }

    override fun scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_series, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            R.id.action_filter_series -> {
                showSortMenu(activity.findViewById(R.id.action_filter_series))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        when (item.itemId) {
            R.id.popup_filter_airing -> sharedPreferences.edit().putString(getString(R.string.pref_sort_series_key),
                    getString(R.string.pref_sort_series_airing)).apply()
            R.id.popup_filter_on_the_air -> sharedPreferences.edit().putString(getString(R.string.pref_sort_series_key),
                    getString(R.string.pref_sort_series_ontheair)).apply()
            R.id.popup_filter_popular -> sharedPreferences.edit().putString(getString(R.string.pref_sort_series_key),
                    getString(R.string.pref_sort_series_popular)).apply()
            R.id.popup_filter_top -> sharedPreferences.edit().putString(getString(R.string.pref_sort_series_key),
                    getString(R.string.pref_sort_series_top)).apply()
        }

        return false
    }

    /**
     * Shows PopupMenu on Filter button click in ActionBar
     * @param view of the button itself
     */
    fun showSortMenu(view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_series_filter_popup, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.show()
    }

    override fun showData(series: List<TvObject>, page: Int) {
        mListEmpty.visibility = View.INVISIBLE
        if (page == 1) {
            mTvAdapter = TvAdapter(context, series, this@SeriesFragment)
            mRecyclerView.swapAdapter(mTvAdapter, false)
        } else {
            mTvAdapter.setData(series)
        }
        mSeries = mTvAdapter.series
    }

    override fun showEmpty() {
        mListEmpty.text = getString(R.string.recyclerview_empty_text)
        mListEmpty.visibility = View.VISIBLE
        mTvAdapter.setData(null)
    }

    override fun showError() {
        mListEmpty.visibility = View.VISIBLE
        mListEmpty.setText(R.string.fragment_error)
    }

    override fun showProgress() {
        mSwipeRefreshLayout.isRefreshing = false
        if (mProgressBar.visibility == View.VISIBLE) {
            mProgressBar.visibility = View.INVISIBLE
        } else {
            mProgressBar.visibility = View.VISIBLE
        }
    }

    companion object {

        private val TAG = SeriesFragment::class.java.simpleName

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(): SeriesFragment {
            val fragment = SeriesFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
