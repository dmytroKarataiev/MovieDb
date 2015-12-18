package karataiev.dmytro.popularmovies;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Adapter with MovieObjects
 * Created by karataev on 12/14/15.
 */
class MovieObjectAdapter extends ArrayAdapter<MovieObject> {

    public MovieObjectAdapter(Activity context, List<MovieObject> movieObjects) {
        super(context, 0, movieObjects);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        MovieObject movieObject = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }

        final ProgressBar spinner = (ProgressBar) view.findViewById(R.id.movie_item_spinner);
        final ImageView poster = (ImageView) view.findViewById(R.id.movie_poster);

        // Target to show/hide ProgressBar on ImageView
        final Target target = new Target() {

            @Override
            public void onPrepareLoad(Drawable drawable) {
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onBitmapLoaded(Bitmap photo, Picasso.LoadedFrom from) {
                poster.setBackgroundDrawable(new BitmapDrawable(photo));
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onBitmapFailed(Drawable arg0) { }
        };

        // Save strong reference to be able to show pictures without sliding the screen
        poster.setTag(target);

        Picasso.with(getContext()).load(movieObject.poster_path).into((Target) poster.getTag());

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
