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

package com.adkdevelopment.moviesdb.ui.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.adkdevelopment.moviesdb.R;
import com.adkdevelopment.moviesdb.data.database.MoviesContract;
import com.adkdevelopment.moviesdb.utils.DatabaseTasks;

/**
 * Adapter to the DB
 * Created by karataev on 12/22/15.
 */
public class FavoritesAdapter extends CursorAdapter {

    private final Context mContext;

    public static class ViewHolder {
        @BindView(R.id.movie_poster) ImageView mPosterImage;
        @BindView(R.id.movie_poster_text) TextView mPosterText;
        @BindView(R.id.movie_item_spinner) ProgressBar mProgressSpinner;
        @BindView(R.id.movie_poster_favorite) ImageView mFavImage;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public FavoritesAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor){

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        int versionIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE);
        final String versionName = cursor.getString(versionIndex);

        //viewHolder.mFavImage.setImageResource(R.drawable.ic_bookmark_fav);
        Picasso.with(context).load(R.drawable.ic_bookmark_fav).into(viewHolder.mFavImage);

        // On mImageFavorite icon click
        viewHolder.mFavImage.setOnClickListener(v -> {

            Toast.makeText(context, "Not Favorite anymore", Toast.LENGTH_LONG).show();

            //viewHolder.mFavImage.setImageResource(R.drawable.ic_bookmark);
            Picasso.with(context).load(R.drawable.ic_bookmark).into(viewHolder.mFavImage);
            // Temp way to delete data from the db
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, versionName);

            // Deletion on background thread
            DatabaseTasks databaseTasks = new DatabaseTasks(mContext);
            databaseTasks.execute(DatabaseTasks.DELETE, contentValues);
        });

        viewHolder.mProgressSpinner.setVisibility(View.GONE);

        // gets image from Picasso cache, instead of db
        int imageIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);
        //byte[] image = cursor.getBlob(imageIndex);
        String image = cursor.getString(imageIndex);
        if (image != null) {
            Picasso.with(context).load(image).into(viewHolder.mPosterImage);
        //    viewHolder.mPosterImage.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }

    }
}
