package karataiev.dmytro.popularmovies.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import karataiev.dmytro.popularmovies.MovieObject;
import karataiev.dmytro.popularmovies.Utility;

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
        Log.v(LOG_TAG, "isSearch: " + Boolean.toString(isSearch));
        if (isSearch && params[0].length() > 0) {
            url = Utility.getSearchURL(params[0], currentPage);
        } else {
            url = Utility.getUrl(currentPage, mContext);
        }
        Log.v(LOG_TAG, "url: " + url.toString());

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
