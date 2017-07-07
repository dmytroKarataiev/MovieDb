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

package com.adkdevelopment.moviesdb.feat_series.contracts

import com.adkdevelopment.moviesdb.data.model.TvObject
import com.adkdevelopment.moviesdb.ui.base.MvpPresenter
import com.adkdevelopment.moviesdb.ui.base.MvpView

/**
 * MVP Contract for the SeriesFragment.
 * Created by Dmytro Karataiev on 10/22/16.
 */

class SeriesContract {

    interface Presenter : MvpPresenter<View> {

        fun requestData(page: Int)

    }

    interface View : MvpView {

        fun showData(series: MutableList<TvObject>, page: Int)

        fun showEmpty()

        fun showError()

        fun showProgress()
    }
}
