package karataiev.dmytro.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import karataiev.dmytro.popularmovies.AsyncTask.FetchJSON;
import karataiev.dmytro.popularmovies.database.MoviesContract;

/**
 * Detailed Movie Fragment with poster, rating, description.
 * Created by karataev on 12/15/15.
 */
public class DetailFragment extends Fragment implements YouTubePlayer.OnInitializedListener {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    // String which is used in share intent
    private String mMovie;

    // YouTube variables
    private YouTubePlayer YPlayer;

    // save current video and position
    private int currentVideoMillis;
    private int currentVideo;

    // list of videos
    private List<String> videoID;

    YouTubePlayerSupportFragment youTubePlayerSupportFragment;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            // Get video progress
            currentVideoMillis = savedInstanceState.getInt("time");
            currentVideo = savedInstanceState.getInt("video");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        final ViewHolder viewHolder = new ViewHolder(rootView);

        // Gets data from intent (using parcelable) and populates views
        Intent intent = this.getActivity().getIntent();
        final MovieObject fromIntent = intent.getParcelableExtra("movie");

        // Files from db to load into ImageViews
        byte[] posterLoad = intent.getByteArrayExtra("poster");
        byte[] backdropLoad = intent.getByteArrayExtra("backdrop");
        File posterFile = Utility.makeFile(getContext(), posterLoad, fromIntent.getId() + "poster");
        File backdropFile = Utility.makeFile(getContext(), backdropLoad, fromIntent.getId() + "backdrop");

        // ActionBar title and image adding
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(fromIntent.getTitle());
        }

        final ImageView backdrop = (ImageView) getActivity().findViewById(R.id.backdrop);

        viewHolder.movieName.setText(fromIntent.getTitle());
        viewHolder.movieDescription.setText(fromIntent.getOverview());
        viewHolder.movieRating.setText(fromIntent.getVoteAverage());
        viewHolder.movieReleaseDate.setText(fromIntent.getReleaseDate());
        viewHolder.movieVotes.setText(String.format(getActivity().getString(R.string.votes_text), fromIntent.getVoteCount()));

        if (Utility.isFavorite(getContext(), fromIntent)) {
            viewHolder.favorite.setImageResource(R.drawable.bookmark_fav);
        } else {
            viewHolder.favorite.setImageResource(R.drawable.bookmark);
        }

        // Callback inside of Picasso Call
        Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                viewHolder.spinner.setVisibility(View.GONE);
                viewHolder.favorite.setVisibility(View.VISIBLE);

                // On favorite icon click
                viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ContentValues favValue = Utility.makeContentValues(fromIntent);

                        Toast.makeText(getContext(), fromIntent.getTitle(), Toast.LENGTH_LONG).show();

                        if (!Utility.isFavorite(getContext(), fromIntent)) {

                            // Save drawable for later usage
                            byte[] bitmapData = Utility.makeByteArray(viewHolder.posterView.getDrawable());
                            byte[] backdropBitmap = Utility.makeByteArray(backdrop.getDrawable());

                            // save byte array of an image to the database
                            favValue.put(MoviesContract.MovieEntry.COLUMN_IMAGE, bitmapData);
                            favValue.put(MoviesContract.MovieEntry.COLUMN_FULL_IMAGE, backdropBitmap);

                            viewHolder.favorite.setImageResource(R.drawable.bookmark_fav);

                            // Insert on background thread
                            UtilityAsyncTask utilityAsyncTask = new UtilityAsyncTask(getContext());
                            utilityAsyncTask.execute(UtilityAsyncTask.INSERT, favValue);
                        } else {
                            viewHolder.favorite.setImageResource(R.drawable.bookmark);

                            // Delete on background thread
                            UtilityAsyncTask utilityAsyncTask = new UtilityAsyncTask(getContext());
                            utilityAsyncTask.execute(UtilityAsyncTask.DELETE, favValue);
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
        };

        if (posterFile != null && backdropFile != null) {
            Log.v(LOG_TAG, "first");
            Picasso.with(getContext()).load(backdropFile).into(backdrop);
            Picasso.with(getContext()).load(posterFile).into(viewHolder.posterView, callback);
        } else if (backdropFile == null && posterFile != null) {
            Log.v(LOG_TAG, "second");
            Picasso.with(getContext()).load(posterFile).into(viewHolder.posterView, callback);
            Picasso.with(getContext()).load(fromIntent.getBackdropPath()).into(backdrop);
        } else {
            Log.v(LOG_TAG, "third");
            Picasso.with(getContext()).load(fromIntent.getBackdropPath()).into(backdrop);
            Picasso.with(getContext()).load(fromIntent.getPosterPath()).into(viewHolder.posterView, callback);
        }

        // Initializes mMovie with info about a movie
        mMovie = fromIntent.getTitle() + "\n" + fromIntent.getReleaseDate() + "\n" + fromIntent.getVoteAverage() + "\n" + fromIntent.getOverview();

        // YouTube view initialization
        youTubePlayerSupportFragment = new YouTubePlayerSupportFragment();
        youTubePlayerSupportFragment.initialize(BuildConfig.YOUTUBE_API_KEY, this);
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerSupportFragment).commit();

        // Detect if display is in landscape mode and set YouTube layout height accordingly
        TypedValue tv = new TypedValue();

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();

        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true) && (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270))
        {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            FrameLayout youtubeFrame = (FrameLayout) rootView.findViewById(R.id.youtube_fragment);
            ViewGroup.LayoutParams layoutParams = youtubeFrame.getLayoutParams();

            layoutParams.height = Utility.screenSize(getContext())[1] - (actionBarHeight + actionBarHeight / 2);
            youtubeFrame.setLayoutParams(layoutParams);
        }

        try {
            // get from AsyncTask trailers
            FetchJSON fetchJSON = new FetchJSON();
            fromIntent.setKeys(fetchJSON.execute(fromIntent.getTrailerPath()).get());

            if (fromIntent.getTrailers() != null && fromIntent.getTrailers().size() > 0) {
                videoID = fromIntent.getTrailers();

                // If there are trailers - add their links to the share Intent
                mMovie += "\nAlso check out the Trailers:\n";
                for (String each : fromIntent.getTrailers()) {
                    mMovie += "https://www.youtube.com/watch?v=" + each + "\n";
                }
            }

            // Get Reviews from AsyncTask and put them in a simple TextView
            FetchJSON fetchJSONReviews = new FetchJSON();
            TextView reviews = (TextView) rootView.findViewById(R.id.detail_reviews_textview);

            ArrayList<String> reviewsArrayList = fetchJSONReviews
                    .execute(Utility.getReviewsURL(fromIntent.getId()).toString())
                    .get();

            if (reviewsArrayList != null) {
                reviews.setText(TextUtils.join("\n", reviewsArrayList));
            }

        } catch (ExecutionException e) {
            Log.e(LOG_TAG, "error");
        } catch (InterruptedException e2) {
            Log.e(LOG_TAG, "error" + e2);
        }

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

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {

        this.YPlayer = player;

        YPlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {
                currentVideo = videoID.indexOf(s);
            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {

            }

            @Override
            public void onVideoEnded() {

            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        });
        if(!wasRestored){
            if (videoID != null) {
                YPlayer.cueVideos(videoID, currentVideo, currentVideoMillis);
            } else {
                // hide youtube player if there is no video
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().hide(youTubePlayerSupportFragment).commit();
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        if (result.isUserRecoverableError()) {
            result.getErrorDialog(this.getActivity(),1).show();
        } else {
            Toast.makeText(this.getActivity(), "YouTubePlayer.onInitializationFailure(): " + result.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        // Save YouTube progress
        saveInstanceState.putInt("time", YPlayer.getCurrentTimeMillis());
        saveInstanceState.putInt("video", currentVideo);
    }


}
