package karataiev.dmytro.popularmovies.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import karataiev.dmytro.popularmovies.Utility;

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
                Log.v(LOG_TAG, url.toString());
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
