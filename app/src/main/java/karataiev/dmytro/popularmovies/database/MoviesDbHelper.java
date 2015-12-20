package karataiev.dmytro.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for movies karataiev.dmytro.popularmovies.database
 * Created by karataev on 12/19/15.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

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
                MoviesContract.MovieEntry.COLUMN_ID + " TEXT, " +
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
                MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_VOTE_COUNT + " TEXT, " +
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