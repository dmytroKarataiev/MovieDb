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

package karataiev.dmytro.popularmovies.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import karataiev.dmytro.popularmovies.model.MovieObject;
import karataiev.dmytro.popularmovies.utils.Utility;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class to retrieve MovieObjects from JSON on background thread
 * Created by karataev on 12/23/15.
 */
public class FetchMovies extends AsyncTask<String, Void, ArrayList<MovieObject>> {

    private final String LOG_TAG = FetchMovies.class.getSimpleName();
    private final Context mContext;
    private final TaskCompleted listener;
    private final boolean isSearch;
    private final int currentPage;

    public FetchMovies(Context context, TaskCompleted listener, boolean isSearch, int currentPage) {
        mContext = context;
        this.listener = listener;
        this.isSearch = isSearch;
        this.currentPage = currentPage;
    }

    /**
     * AsyncTask to fetch data on background thread with listener to pass status of downloading
     *
     * @param params receives request link from Utility class
     * @return ArrayList of YouTube id's for trailers
     */
    protected ArrayList<MovieObject> doInBackground(String... params) {

        URL url;
        if (isSearch && params[0].length() > 0) {
            url = Utility.getSearchURL(params[0], currentPage);
        } else {
            url = Utility.getUrl(currentPage, mContext);
        }

        // Network Client
        OkHttpClient client = new OkHttpClient();

        // Will contain the raw JSON response as a string.
        String movieJsonStr = "";

        if (listener != null) {
            listener.onAsyncProgress(true);
        }

        if (params[0] != null) {
            try {
                //URL url = new URL(params[0]);
                // Create the request to movide db, and open the connection
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response responses = client.newCall(request).execute();
                movieJsonStr = responses.body().string();
                responses.body().close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Null ", e);
            }

            return Utility.getMoviesGSON(mContext, movieJsonStr);

        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieObject> movieObjects) {
        // Check the flag that activity is over
        if (listener != null) {
            listener.onAsyncProgress(false);
        }
    }
}
