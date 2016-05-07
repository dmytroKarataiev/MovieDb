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

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import karataiev.dmytro.popularmovies.utils.Utility;

/**
 * Class to retreive JSON on background thread
 * Created by karataev on 12/23/15.
 */
public class FetchJSON extends AsyncTask<String, Void, ArrayList<String>> {

    private final String LOG_TAG = FetchJSON.class.getSimpleName();

    public FetchJSON() {}

    /**
     * AsyncTask to fetch data on background thread
     *
     * @param params receives request link from Utility class
     * @return ArrayList of YouTube id's for trailers
     */
    protected ArrayList<String> doInBackground(String... params) {
        // Network Client
        OkHttpClient client = new OkHttpClient();

        // Will contain the raw JSON response as a string.
        String movieJsonStr = "";

        if (params[0] != null) {
            try {
                URL url = new URL(params[0]);
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

            if (params[0].contains("reviews")) {
                return Utility.getReviews(movieJsonStr);
            } else {
                return Utility.getTrailers(movieJsonStr);
            }
        }
        return null;
    }
}
