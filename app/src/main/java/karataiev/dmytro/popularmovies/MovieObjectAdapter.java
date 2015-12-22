package karataiev.dmytro.popularmovies;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import karataiev.dmytro.popularmovies.database.MoviesContract;

/**
 * Adapter with MovieObjects
 * Created by karataev on 12/14/15.
 */
class MovieObjectAdapter extends ArrayAdapter<MovieObject> {

    private final String LOG_TAG = MovieObjectAdapter.class.getSimpleName();
    private ContentResolver contentResolver = getContext().getContentResolver();



    public MovieObjectAdapter(Activity context, List<MovieObject> movieObjects) {
        super(context, 0, movieObjects);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final MovieObject movieObject = getItem(position);
        Log.v(LOG_TAG, movieObject.title + " pos " + position + " isFav " + movieObject.isFavorited);

        //contentResolver.acquireContentProviderClient(MoviesContract.BASE_CONTENT_URI);
        final ContentValues favValue = new ContentValues();
        favValue.put(MoviesContract.MovieEntry.COLUMN_TITLE, movieObject.title);
        favValue.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movieObject.overview);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }

        final ProgressBar spinner = (ProgressBar) view.findViewById(R.id.movie_item_spinner);
        final ImageView poster = (ImageView) view.findViewById(R.id.movie_poster);
        final ImageView favorite = (ImageView) view.findViewById(R.id.movie_poster_favorite);

        // set favorites icon
        if (movieObject.isFavorited == 1) {
            favorite.setImageResource(R.drawable.bookmark_fav);
        } else {
            favorite.setImageResource(R.drawable.bookmark);
        }

        // Scale posters correctly
        poster.getLayoutParams().height = Utility.screenSize(getContext())[5];
        spinner.getLayoutParams().height = Utility.screenSize(getContext())[5];
        favorite.getLayoutParams().height = (int) Math.round(Utility.screenSize(getContext())[5] * 0.2);

        spinner.setVisibility(View.VISIBLE);
        favorite.setVisibility(View.GONE);

        Picasso.with(getContext()).load(movieObject.poster_path).into(poster, new Callback() {
            @Override
            public void onSuccess() {
                spinner.setVisibility(View.GONE);
                favorite.setVisibility(View.VISIBLE);

                // On favorite icon click
                favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(getContext(), movieObject.title, Toast.LENGTH_LONG).show();

                        if (movieObject.isFavorited == 0) {
                            favorite.setImageResource(R.drawable.bookmark_fav);
                            movieObject.isFavorited = 1;
                            contentResolver.insert(MoviesContract.MovieEntry.CONTENT_URI, favValue);
                        } else {
                            favorite.setImageResource(R.drawable.bookmark);
                            movieObject.isFavorited = 0;
                            contentResolver.delete(MoviesContract.MovieEntry.CONTENT_URI,
                                    MoviesContract.MovieEntry.COLUMN_TITLE + " = ?",
                                    new String[] { favValue.getAsString(MoviesContract.MovieEntry.COLUMN_TITLE) }
                            );
                        }
                    }
                });
            }

            @Override
            public void onError() {
                poster.setBackgroundResource(R.color.white);
                spinner.setVisibility(View.GONE);
                favorite.setVisibility(View.GONE);
            }
        });

        // If movie doesn't have an image - uses text instead
        if (movieObject.poster_path.contains("null"))
        {
            TextView imageText = (TextView) view.findViewById(R.id.movie_poster_text);
            imageText.setText(movieObject.title);
        }
        poster.setContentDescription(movieObject.title);

        return view;
    }


}
