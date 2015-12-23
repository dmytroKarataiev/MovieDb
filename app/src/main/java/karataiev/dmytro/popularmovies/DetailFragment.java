package karataiev.dmytro.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import karataiev.dmytro.popularmovies.database.MoviesContract;

/**
 * Detailed Movie Fragment with poster, rating, description.
 * Created by karataev on 12/15/15.
 */
public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    // String which is used in share intent
    private String mMovie;

    /**
     * Cache of the children views
     * Little optimization to access views faster (not sure if it's applicable in this particular case)
     */
    public static class ViewHolder {

        public final ImageView posterView;
        public final ProgressBar spinner;
        public final ImageView favorite;
        public final TextView movieName;
        public final TextView movieReleaseDate;
        public final TextView movieRating;
        public final TextView movieDescription;
        public final TextView movieVotes;

        public ViewHolder(View view) {
            favorite = (ImageView) view.findViewById(R.id.movie_poster_favorite);
            posterView = (ImageView) view.findViewById(R.id.movie_poster);
            spinner = (ProgressBar) view.findViewById(R.id.movie_item_spinner);
            movieName = (TextView) view.findViewById(R.id.movie_name);
            movieReleaseDate = (TextView) view.findViewById(R.id.detail_releasedate_textview);
            movieRating = (TextView) view.findViewById(R.id.detail_rating_textview);
            movieDescription = (TextView) view.findViewById(R.id.detail_description_textview);
            movieVotes = (TextView) view.findViewById(R.id.detail_votecount_textview);
        }
    }

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        final ViewHolder viewHolder = new ViewHolder(rootView);

        // Gets data from intent (using parcelable) and populates views
        Intent intent = this.getActivity().getIntent();
        final MovieObject fromIntent = intent.getParcelableExtra("movie");

        viewHolder.movieName.setText(fromIntent.title);
        viewHolder.movieDescription.setText(fromIntent.overview);
        viewHolder.movieRating.setText(fromIntent.vote_average);
        viewHolder.movieReleaseDate.setText(fromIntent.release_date);
        viewHolder.movieVotes.setText(String.format(getActivity().getString(R.string.votes_text), fromIntent.vote_count));

        if (Utility.isFavorite(getContext(), fromIntent)) {
            viewHolder.favorite.setImageResource(R.drawable.bookmark_fav);
        } else {
            viewHolder.favorite.setImageResource(R.drawable.bookmark);
        }

        Picasso.with(getContext()).load(fromIntent.poster_path).into(viewHolder.posterView, new Callback() {
            @Override
            public void onSuccess() {
                viewHolder.spinner.setVisibility(View.GONE);
                viewHolder.favorite.setVisibility(View.VISIBLE);

                // On favorite icon click
                viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ContentValues favValue = new ContentValues();
                        favValue.put(MoviesContract.MovieEntry.COLUMN_TITLE, fromIntent.title);
                        favValue.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, fromIntent.overview);
                        favValue.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, fromIntent.release_date);
                        favValue.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, fromIntent.vote_average);
                        favValue.put(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT, fromIntent.vote_count);

                        Toast.makeText(getContext(), fromIntent.title, Toast.LENGTH_LONG).show();

                        if (!Utility.isFavorite(getContext(), fromIntent)) {

                            // Save drawable for later usage
                            Drawable loadedPoster = viewHolder.posterView.getDrawable();
                            Bitmap bitmap = ((BitmapDrawable) loadedPoster).getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] bitmapData = stream.toByteArray();

                            // save byte array of an image to the database
                            favValue.put(MoviesContract.MovieEntry.COLUMN_IMAGE, bitmapData);

                            viewHolder.favorite.setImageResource(R.drawable.bookmark_fav);

                            Utility.insertToDb(getContext(), favValue);

                        } else {
                            viewHolder.favorite.setImageResource(R.drawable.bookmark);
                            Utility.deleteFromDb(getContext(), favValue);
                        }
                    }
                });
            }

            @Override
            public void onError() {
                viewHolder.posterView.setBackgroundResource(R.color.white);
                viewHolder.spinner.setVisibility(View.GONE);
                viewHolder.favorite.setVisibility(View.GONE);
            }
        });

        // Initializes mMovie with info about a movie
        mMovie = fromIntent.title + "\n" + fromIntent.release_date + "\n" + fromIntent.vote_average + "\n" + fromIntent.overview;

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem item = menu.findItem(R.id.share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(movieIntent());
        }
        else {
            Log.v(LOG_TAG, "fail");
        }
    }

    /**
     * Method to populate Intent with data
     * @return Intent with data to external apps
     */
    private Intent movieIntent() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, mMovie + "\n#Pop Movie App");

        return sendIntent;
    }
}
