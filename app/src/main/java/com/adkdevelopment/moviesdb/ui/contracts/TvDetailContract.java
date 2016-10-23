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

package com.adkdevelopment.moviesdb.ui.contracts;

import android.content.Intent;

import com.adkdevelopment.moviesdb.data.model.Backdrops;
import com.adkdevelopment.moviesdb.data.model.MovieCredits;
import com.adkdevelopment.moviesdb.data.model.TvSeries;
import com.adkdevelopment.moviesdb.ui.base.MvpPresenter;
import com.adkdevelopment.moviesdb.ui.base.MvpView;

/**
 * MVP Contract for the TvDetailFragment.
 * Created by Dmytro Karataiev on 10/22/16.
 */

public class TvDetailContract {

    public interface Presenter extends MvpPresenter<View> {
        void requestData(Intent intent);
    }

    public interface View extends MvpView {
        void showData(TvSeries tvObject);

        void showActors(MovieCredits movieCredits);

        void showPosters(Backdrops backdrops);

        void showError();
    }
}
