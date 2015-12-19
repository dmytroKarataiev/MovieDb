package karataiev.dmytro.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter with MovieObjects
 * Created by karataev on 12/14/15.
 */
class MovieObjectAdapter extends ArrayAdapter<MovieObject> {

    private final String LOG_TAG = MovieObjectAdapter.class.getSimpleName();

    public MovieObjectAdapter(Activity context, List<MovieObject> movieObjects) {
        super(context, 0, movieObjects);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final MovieObject movieObject = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }

        final ProgressBar spinner = (ProgressBar) view.findViewById(R.id.movie_item_spinner);
        final ImageView poster = (ImageView) view.findViewById(R.id.movie_poster);

        // Scale posters correctly
        poster.getLayoutParams().height = Utility.screenSize(getContext())[5];

        spinner.setVisibility(View.VISIBLE);

        Picasso.with(getContext()).load(movieObject.poster_path).into(poster, new Callback() {
            @Override
            public void onSuccess() {
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                poster.setBackgroundResource(R.color.white);
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
