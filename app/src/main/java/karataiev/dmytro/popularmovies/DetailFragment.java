package karataiev.dmytro.popularmovies;

import android.content.Intent;
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
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by karataev on 12/15/15.
 */

public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private ViewHolder viewHolder;
    private ShareActionProvider mShareActionProvider;
    private String mMovie;


    /**
     * Cache of the children views for a forecast list item.
     */

    public static class ViewHolder {

        public final ImageView posterView;
        public final TextView movieName;
        public final TextView movieReleaseDate;
        public final TextView movieRating;
        public final TextView movieDescription;
        public final TextView movieVotes;


        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.poster_imageview);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        viewHolder = new ViewHolder(rootView);

        Intent intent = this.getActivity().getIntent();
        MovieObject fromIntent = intent.getParcelableExtra("movie");

        viewHolder.movieName.setText(fromIntent.name);
        viewHolder.movieDescription.setText(fromIntent.description);
        viewHolder.movieRating.setText(fromIntent.rating);
        viewHolder.movieReleaseDate.setText(fromIntent.year);
        viewHolder.movieVotes.setText(String.format(getActivity().getString(R.string.votes_text), fromIntent.voteCount));

        Picasso.with(getContext()).load(fromIntent.pathToDetailImage).into(viewHolder.posterView);

        mMovie = fromIntent.name + "\n" + fromIntent.year + "\n" + fromIntent.rating + "\n" + fromIntent.description;

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem item = menu.findItem(R.id.share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(movieIntent());
        }
        else
        {
            Log.v(LOG_TAG, "fail");
        }
    }

    private Intent movieIntent() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, mMovie + "\n#Pop Movie App");

        return sendIntent;
    }


}
