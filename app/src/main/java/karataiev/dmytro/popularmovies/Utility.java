package karataiev.dmytro.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import karataiev.dmytro.popularmovies.database.MoviesContract;

/**
 * Class with additional helper functions
 * Created by karataev on 12/15/15.
 */
public class Utility {

    private static String LOG_TAG = Utility.class.getSimpleName();

    /**
     * Method to provide correct path to image, depending on the dpi metrics of the phone screen
     *
     * @param context to get metrics data
     * @return String name which should be used in path to image
     */
    public static String[] posterSize(Context context) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        String POSTER_SIZE;
        String POSTER_SIZE_DETAIL;

        switch (metrics.densityDpi) {
            case 120:
                POSTER_SIZE = "w92";
                POSTER_SIZE_DETAIL = "w154";
                break;
            case 160:
                POSTER_SIZE = "w154";
                POSTER_SIZE_DETAIL = "w185";
                break;
            case 213:
            case 240:
            case 320:
                POSTER_SIZE = "w185";
                POSTER_SIZE_DETAIL = "w342";
                break;
            case 480:
                POSTER_SIZE = "w342";
                POSTER_SIZE_DETAIL = "w500";
                break;
            case 640:
                POSTER_SIZE = "w500";
                POSTER_SIZE_DETAIL = "w780";
                break;
            default:
                POSTER_SIZE = "w185";
                POSTER_SIZE_DETAIL = "w342";
                break;
        }

        return new String[]{POSTER_SIZE, POSTER_SIZE_DETAIL};
    }

    /**
     * Method to get sort settings from SharedPreferences
     *
     * @param context from which call was made
     * @return current sort preference
     */
    public static String getSort(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }

    /**
     * Method to return sort parameter in a redable format
     * @param context of an activity
     * @return readable representation of a sort parameter
     */
    public static String getSortReadable(Context context) {

        String sort = getSort(context);

        String[] sortTypeEntries = context.getResources().getStringArray(R.array.pref_sort_entries);
        String[] sortTypeValues = context.getResources().getStringArray(R.array.pref_sort_values);

        // looks up index of the sort parameter and returns it in a readable format
        return sortTypeEntries[Arrays.asList(sortTypeValues).indexOf(sort)];
    }


    /**
     * Formats date (cuts everything except the year)
     *
     * @param date with month and day
     * @return year only
     */
    public static String formatDate(String date) {

        if (date.length() > 3) {
            return date.substring(0, 4);
        } else {
            return date;
        }
    }

    /**
     * Adds "/10" to the end of the fetched rating
     *
     * @param rating from JSON
     * @return String rating + "/10"
     */
    public static String formatRating(String rating) {

        if (rating.length() > 2) {
            return rating.substring(0, 3) + "/10";
        } else {
            return rating + "/10";
        }
    }

    /**
     * Formats votes -> adds "," thousands separator
     *
     * @param votes as a String
     * @return formatted String with thousands separator
     */
    public static String formatVotes(String votes) {

        int votesInt = Integer.parseInt(votes);

        return String.format("%,d", votesInt);
    }

    /**
     * Easier way to fetch data from json string, don't need to use each token.
     * GSON Google Library
     *
     * @param context      to be able to get screen density
     * @param movieJsonStr input string
     * @return MovieObject ArrayList with movies
     */
    public static ArrayList<MovieObject> getMoviesGSON(Context context, String movieJsonStr) {

        ArrayList<MovieObject> movieObjects = new ArrayList<>();

        JsonParser parser = new JsonParser();

        JsonElement element = parser.parse(movieJsonStr);

        if (element.isJsonObject()) {
            JsonObject results = element.getAsJsonObject();
            JsonArray movies = results.getAsJsonArray("results");

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            for (int i = 0; i < movies.size(); i++) {
                JsonObject movie = movies.get(i).getAsJsonObject();
                MovieObject current = gson.fromJson(movie, MovieObject.class);
                current.makeNice(context);
                current.setTrailerPath(getTrailersURL(current.getId()).toString());

                movieObjects.add(current);
            }
            return movieObjects;
        }

        return null;
    }

    /**
     * Method to get URL for a page from Movie DB
     * @param currentPage to fetch correct page
     * @param context from which a call is being made
     * @return URL to next 20 movies
     */
    public static URL getUrl(int currentPage, Context context) {
        // Construct the URL for the movie query
        // Possible parameters are available at Movie DB API page, at
        // http://docs.themoviedb.apiary.io/
        final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
        final String QUERY_PARAM = "sort_by";
        final String PAGE_QUERY = "page";
        String PAGE = Integer.toString(currentPage);

        // Gets preferred sort, by default: popularity.desc
        final String SORT = Utility.getSort(context);

        final String VOTERS = "vote_count.gte";
        final String VOTERS_MIN = "100";

        // Don't forget to add API key to the gradle.properties file
        final String API_KEY = "api_key";

        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, SORT)
                .appendQueryParameter(PAGE_QUERY, PAGE)
                .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        // When sort on vote_average - gets movies with at least VOTERS_MIN votes
        if (SORT.contains("vote_average")) {
            builtUri = builtUri.buildUpon()
                    .appendQueryParameter(VOTERS, VOTERS_MIN)
                    .build();
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e("URL", "error " + e);
        }

        return url;
    }

    /**
     * Method to get the screen details and in the app to set them accordingly
     * @param context to get screen metrics
     * @return different screen parameters
     */
    public static int[] screenSize(Context context) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int density = metrics.densityDpi;
        int columns = Math.round(metrics.widthPixels / metrics.densityDpi);
        int posterWidth = width / columns;
        int posterHeight = (int) (posterWidth * 1.5);

        return new int[] { width, height, density, columns, posterWidth, posterHeight };
    }

    /**
     * Method to check in the database if movie is present
     * @param context from which method is called
     * @param movie to check if it is in database
     * @return boolean true if present, false otherwise
     */
    public static boolean isFavorite(Context context, MovieObject movie) {
        // get access to db to check if a movie is in fav list
        ContentResolver contentResolver = context.getContentResolver();

        // if a movie is in the database - make it favorite
        Cursor currentPoster = contentResolver.query(MoviesContract.MovieEntry.CONTENT_URI,
                null,
                MoviesContract.MovieEntry.COLUMN_TITLE + " = ?",
                new String[]{movie.getTitle()},
                null);

        if (currentPoster != null) {
            currentPoster.moveToFirst();
            int index = currentPoster.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE);

            if (currentPoster.getCount() > 0 && currentPoster.getString(index).equals(movie.getTitle())) {
                currentPoster.close();
                return true;
            }
            currentPoster.close();
        }

        return false;
    }

    /**
     * Method to get trailer URL
     * @param movie_id from MovieObject
     * @return URL request for trailers
     */
    public static URL getTrailersURL(String movie_id) {
        // Construct the URL for the movie query
        // Possible parameters are available at Movie DB API page, at
        // http://docs.themoviedb.apiary.io/
        Log.v(LOG_TAG, "ger trailers urls");


        String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + movie_id + "/videos";


        // Don't forget to add API key to the gradle.properties file
        final String API_KEY = "api_key";

        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e("URL", "error " + e);
        }

        return url;
    }

    /**
     * Method to parse JSON string and return ArrayList of YouTube id's
     * @param movieJsonStr JSON string with trailers
     * @return ArrayList of Strings with id's for YouTube
     */
    public static ArrayList<String> getTrailers(String movieJsonStr) {

        Log.v(LOG_TAG, "getTrailers" + movieJsonStr);

        ArrayList<String> trailers = new ArrayList<>();

        JsonParser parser = new JsonParser();

        JsonElement element = parser.parse(movieJsonStr);

        if (element.isJsonObject()) {
            JsonObject results = element.getAsJsonObject();
            JsonArray trailersList = results.getAsJsonArray("results");

            if (trailersList != null) {
                for (int i = 0; i < trailersList.size(); i++) {
                    JsonObject movie = trailersList.get(i).getAsJsonObject();
                    JsonElement trailerKey = movie.getAsJsonPrimitive("key");
                    Log.v("Trailer", trailerKey.getAsString());
                    trailers.add(trailerKey.getAsString());
                }
            }

            return trailers;
        }

        return null;
    }

    /**
     * Method to create a MovieObject from a Cursor
     *
     * @param cursor with movie data
     * @return MovieObject filled with contents from the Cursor
     */
    public static MovieObject makeMovieFromCursor(Cursor cursor) {

        MovieObject movie = new MovieObject();

        int adult = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ADULT);
        int backdropPath = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH);
        int id = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID);
        int originalLanguage = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE);
        int originalTitle = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        int overview = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW);
        int releaseDate = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
        int posterPath = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);
        int popularity = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POPULARITY);
        int title = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE);
        int video = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VIDEO);
        int voteAverage = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int voteCount = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT);
        int fullPosterPath = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_FULL_POSTER_PATH);

        movie.setAdult(cursor.getString(adult));
        movie.setBackdropPath(cursor.getString(backdropPath));
        movie.setId(cursor.getString(id));
        movie.setOriginalLanguage(cursor.getString(originalLanguage));
        movie.setOriginalTitle(cursor.getString(originalTitle));
        movie.setOverview(cursor.getString(overview));
        movie.setReleaseDate(cursor.getString(releaseDate));
        movie.setPosterPath(cursor.getString(posterPath));
        movie.setPopularity(cursor.getString(popularity));
        movie.setTitle(cursor.getString(title));
        movie.setVideo(cursor.getString(video));
        movie.setVoteAverage(cursor.getString(voteAverage));
        movie.setVoteCount(cursor.getString(voteCount));
        movie.setFullPosterPath(cursor.getString(fullPosterPath));

        // Separate call
        movie.setTrailerPath(Utility.getTrailersURL(movie.getId()).toString());

        return movie;
    }

    /**
     * Method to create ContentValues from a MovieObject
     * Called from MovieObjectAdapter
     * @param movie with contents
     * @return ContentValues with contents from the MovieObject
     */
    public static ContentValues makeContentValues(MovieObject movie) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(MoviesContract.MovieEntry.COLUMN_ADULT, movie.getAdult());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_ID, movie.getId());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, movie.getOriginalLanguage());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_VIDEO, movie.getVideo());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_FULL_POSTER_PATH, movie.getFullPosterPath());

        return contentValues;
    }

    /**
     * Method to create byte[] from a Drawable to later put it into the database
     * Called from MovieObjectAdapter
     * @param drawable to convert
     * @return byte[] made from the Drawable
     */
    public static byte[] makeByteArray(Drawable drawable) {

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        return stream.toByteArray();

    }
}
