package karataiev.dmytro.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by karataev on 12/14/15.
 */
public class MovieObjectAdapter extends ArrayAdapter<MovieObject> {

    private static final String LOG_TAG = MovieObjectAdapter.class.getSimpleName();

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
        poster.setContentDescription(movieObject.name);


        return view;
    }

}
