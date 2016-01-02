package karataiev.dmytro.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import karataiev.dmytro.popularmovies.database.MoviesContract;

/**
 * Additional class to manipulate database on background thread
 * Takes Objects as parameters
 * Created by karataev on 12/23/15.
 */
class UtilityAsyncTask extends AsyncTask<Object, Void, Void> {

    private final String LOG_TAG = UtilityAsyncTask.class.getSimpleName();
    private final Context mContext;
    public static final int INSERT = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;


    public UtilityAsyncTask(Context context) {
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
