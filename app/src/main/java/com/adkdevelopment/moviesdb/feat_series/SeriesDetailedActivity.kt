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

import android.os.Bundle
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import com.adkdevelopment.moviesdb.R
import com.adkdevelopment.moviesdb.data.model.Consts
import com.adkdevelopment.moviesdb.data.model.TvObject
import com.adkdevelopment.moviesdb.ui.base.BaseActivity

/**
 * Activity which shows detailed information about a TV Series.
 */
class SeriesDetailedActivity : BaseActivity() {

    @BindView(R.id.toolbar)
    internal lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_detail)

        ButterKnife.bind(this)

        // Initialize a custom toolbar
        setSupportActionBar(mToolbar)
        val ab = supportActionBar

        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true)

            if (intent.hasExtra(Consts.TV_EXTRA)) {
                val tvObject = intent.getParcelableExtra<TvObject>(Consts.TV_EXTRA)
                ab.title = tvObject.name
            }
        }

    }

}
