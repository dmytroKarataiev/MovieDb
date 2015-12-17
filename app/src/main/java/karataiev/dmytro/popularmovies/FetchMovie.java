/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package karataiev.dmytro.popularmovies;

// WORK IN PROGRESS - I'm trying to make progress bar work in MainActivityFragment

/*
public class FetchMovie2 extends AsyncTask<String, Void, MovieObject[]> {

    private final String LOG_TAG = FetchMovie.class.getSimpleName();

    private final Context mContext;

    public FetchMovie(Context context) {
        mContext = context;
    }

    */
/**
     * Take the String representing movie info in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *//*

    private MovieObject[] getMovieDataFromJSON(String movieJsonStr) {

        // Attributes to parse in JSON
        final String RESULTS = "results";
        final String MOVIE_POSTER = "poster_path";
        final String MOVIE_NAME = "title";
        final String MOVIE_DESCRIPTION = "overview";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_VOTE = "vote_count";

        // Depending on the dpi of the phone adds correct address to the link
        final String[] POSTER_SIZE = Utility.posterSize(mContext);

        // Creates to links to the posters: one for main window, one for the detailed view
        final String FULL_PATH = "http://image.tmdb.org/t/p/" + POSTER_SIZE[0] + "/";
        final String FULL_PATH_DETAIL = "http://image.tmdb.org/t/p/" + POSTER_SIZE[1] + "/";

        try {

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);
            MovieObject[] movieObjects = new MovieObject[movieArray.length()];

            for (int i = 0, n = movieArray.length(); i < n; i++)
            {
                JSONObject current = movieArray.getJSONObject(i);

                movieObjects[i] = new MovieObject(
                        current.getString(MOVIE_NAME),
                        FULL_PATH + current.getString(MOVIE_POSTER),
                        FULL_PATH_DETAIL + current.getString(MOVIE_POSTER),
                        current.getString(MOVIE_DESCRIPTION),
                        current.getString(MOVIE_RATING),
                        current.getString(MOVIE_RELEASE_DATE),
                        current.getString(MOVIE_VOTE)
                );
            }
            return movieObjects;

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    */
/**
     * AsyncTask to fetch data on background thread
     * @param params doesn't take any parameters yet, gets sort from SharedPreferences
     * @return array of MovieObjects
     *//*

    protected MovieObject[] doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at Movie DB API page, at
            // http://docs.themoviedb.apiary.io/
            final String FORECAST_BASE_URL =
                    "http://api.themoviedb.org/3/discover/movie?";
            final String QUERY_PARAM = "sort_by";

            // Gets preferred sort, by default: popularity.desc
            final String SORT = Utility.getSort(mContext);
            Log.v(LOG_TAG, "SORT" + SORT);

            final String VOTERS = "vote_count.gte";
            final String VOTERS_MIN = "100";
            // Don't forget to add API key to the gradle.properties file
            final String API_KEY = "api_key";

            Uri builtUri;

            // When sort on vote_average - gets movies with at least VOTERS_MIN votes
            if (SORT.contains("vote_average")) {
                builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, SORT)
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                        .appendQueryParameter(VOTERS, VOTERS_MIN)
                        .build();
                Log.v(LOG_TAG, "URL " + builtUri.toString());
            } else {
                builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, SORT)
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                        .build();
            }

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                line += "\n";
                buffer.append(line);
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();

            Log.v(LOG_TAG, movieJsonStr);

            return getMovieDataFromJSON(movieJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }
}*/
