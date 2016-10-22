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

package com.adkdevelopment.moviesdb.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.adkdevelopment.moviesdb.data.database.MoviesContract;

/**
 * Additional class to manipulate database on background thread
 * Takes Objects as parameters
 * Created by karataev on 12/23/15.
 */
public class DatabaseTasks extends AsyncTask<Object, Void, Void> {

    private final String LOG_TAG = DatabaseTasks.class.getSimpleName();
    private final Context mContext;
    public static final int INSERT = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;


    public DatabaseTasks(Context context) {
        mContext = context;
    }

    /**
     * AsyncTask to manipulate the db on background thread
     *
     * @param params doesn't take any parameters yet, gets sort from SharedPreferences
     * @return array of MovieObjects
     */
    protected Void doInBackground(Object... params) {

        ContentResolver contentResolver = mContext.getContentResolver();
        ContentValues values;

        switch ((Integer) params[0]) {
            case INSERT:
                values = (ContentValues) params[1];
                contentResolver.insert(MoviesContract.MovieEntry.CONTENT_URI, values);
                break;
            case UPDATE:
                break;
            case DELETE:
                values = (ContentValues) params[1];
                contentResolver.delete(MoviesContract.MovieEntry.CONTENT_URI,
                        MoviesContract.MovieEntry.COLUMN_TITLE + " = ?",
                        new String[]{values.getAsString(MoviesContract.MovieEntry.COLUMN_TITLE)});
                break;
            default:
                break;
        }

        return null;
    }
}
