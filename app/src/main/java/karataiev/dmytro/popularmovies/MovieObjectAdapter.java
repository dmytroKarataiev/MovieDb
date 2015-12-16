package karataiev.dmytro.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
    public View getView(int position, View view, ViewGroup parent) {

        MovieObject movieObject = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }

        ImageView poster = (ImageView) view.findViewById(R.id.movie_poster);
        Picasso.with(getContext()).load(movieObject.pathToImage).into(poster);

        // If movie doesn't have an image - uses text instead
        if (movieObject.pathToImage.contains("null"))
        {
            TextView imageText = (TextView) view.findViewById(R.id.movie_poster_text);
            imageText.setText(movieObject.name);
        }
        poster.setContentDescription(movieObject.name);

        return view;
    }

}
