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

package com.adkdevelopment.moviesdb.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for movies karataiev.dmytro.popularmovies.database
 * Created by karataev on 12/19/15.
 */
class MoviesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +

                // Unique keys will be auto-generated in either case.
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather com.example.android.sunshine.app.data
                MoviesContract.MovieEntry.COLUMN_ID + " INTEGER, " +
                MoviesContract.MovieEntry.COLUMN_ADULT + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT," +
                MoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT," +
                MoviesContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_FULL_POSTER_PATH + " TEXT," +
                MoviesContract.MovieEntry.COLUMN_POPULARITY + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_VIDEO + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                MoviesContract.MovieEntry.COLUMN_VOTE_COUNT + " INTEGER, " +
                MoviesContract.MovieEntry.COLUMN_IMAGE + " BLOB, " +
                MoviesContract.MovieEntry.COLUMN_FULL_IMAGE + " BLOB" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop db if exists on update
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}