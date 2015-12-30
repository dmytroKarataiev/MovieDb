package karataiev.dmytro.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import karataiev.dmytro.popularmovies.AsyncTask.FetchJSON;
import karataiev.dmytro.popularmovies.AsyncTask.FetchMovies;
import karataiev.dmytro.popularmovies.database.MoviesContract;

/**
 * Detailed Movie Fragment with poster, rating, description.
 * Created by karataev on 12/15/15.
 */
public class DetailFragment extends Fragment implements YouTubePlayer.OnInitializedListener {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    // String which is used in share intent
    private String mMovie;
    private MovieObject mMovieObject;

    // YouTube variables
    private YouTubePlayer YPlayer;

    // save current video and position
    private int currentVideoMillis;
    private int currentVideo;
    private final String VIDEO_TAG = "youtube";
    private final String VIDEO_NUM = "video_num";

    // list of videos
    private List<String> trailersList;

    private YouTubePlayerSupportFragment youTubePlayerSupportFragment;

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

        if (savedInstanceState != null && savedInstanceState.containsKey(VIDEO_TAG)) {
            // Get video progress
            currentVideoMillis = savedInstanceState.getInt(VIDEO_TAG);
            currentVideo = savedInstanceState.getInt(VIDEO_NUM);
            Log.v(LOG_TAG, "restore sec: " + currentVideoMillis + " video num " + currentVideo);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Intent intent;

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieObject = arguments.getParcelable("movie");
        } else {
            // Gets data from intent (using parcelable) and populates views
            intent = this.getActivity().getIntent();
            mMovieObject = intent.getParcelableExtra("movie");
            if (mMovieObject == null) {
                try {
                    FetchMovies fetchFirstMovie = new FetchMovies(getContext(), null, false, 1);

                    ArrayList<MovieObject> temporary = fetchFirstMovie.execute(Utility.getUrl(1, getContext()).toString()).get();
                    if (temporary != null) {
                        mMovieObject = temporary.get(0);
                    }
                } catch (ExecutionException e) {
                    Log.e(LOG_TAG, "error");
                } catch (InterruptedException e2) {
                    Log.e(LOG_TAG, "error" + e2);
                }
            }
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        final ViewHolder viewHolder = new ViewHolder(rootView);

        if (mMovieObject != null) {
            // Maybe it increases chance of OOM
            // Files from db to load into ImageViews
//        byte[] posterLoad = intent.getByteArrayExtra("poster");
//        byte[] backdropLoad = intent.getByteArrayExtra("backdrop");
//        File posterFile = Utility.makeFile(getContext(), posterLoad, mMovieObject.getId() + "poster");
//        File backdropFile = Utility.makeFile(getContext(), backdropLoad, mMovieObject.getId() + "backdrop");

            // ActionBar title and image adding
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

            if (actionBar != null) {
                actionBar.setTitle(mMovieObject.getTitle());
            }

            ImageView tempForBackdrop;
            if (rootView.findViewById(R.id.backdrop) == null) {
                tempForBackdrop = (ImageView) getActivity().findViewById(R.id.backdrop);
            } else {
                tempForBackdrop = (ImageView) rootView.findViewById(R.id.backdrop);
            }
            final ImageView backdrop = tempForBackdrop;

            viewHolder.movieName.setText(mMovieObject.getTitle());
            viewHolder.movieDescription.setText(mMovieObject.getOverview());
            viewHolder.movieRating.setText(mMovieObject.getVoteAverage());
            viewHolder.movieReleaseDate.setText(mMovieObject.getReleaseDate());
            viewHolder.movieVotes.setText(String.format(getActivity().getString(R.string.votes_text), mMovieObject.getVoteCount()));

            if (Utility.isFavorite(getContext(), mMovieObject)) {
                Picasso.with(getContext()).load(R.drawable.bookmark_fav).into(viewHolder.favorite);
            } else {
                Picasso.with(getContext()).load(R.drawable.bookmark).into(viewHolder.favorite);
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

                            ContentValues favValue = Utility.makeContentValues(mMovieObject);

                            Toast.makeText(getContext(), mMovieObject.getTitle(), Toast.LENGTH_LONG).show();

                            if (!Utility.isFavorite(getContext(), mMovieObject)) {

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

            // Not sure if it increases risk of OOM error
//        if (posterFile != null && backdropFile != null) {
//            Picasso.with(getContext()).load(backdropFile).into(backdrop);
//            Picasso.with(getContext()).load(posterFile).into(viewHolder.posterView, callback);
//        } else if (backdropFile == null && posterFile != null) {
//            Picasso.with(getContext()).load(posterFile).into(viewHolder.posterView, callback);
//            Picasso.with(getContext()).load(mMovieObject.getBackdropPath()).into(backdrop);
//        } else {
            Picasso.with(getContext()).load(mMovieObject.getBackdropPath()).into(backdrop);
            Picasso.with(getContext()).load(mMovieObject.getPosterPath()).into(viewHolder.posterView, callback);
            //}

            // Initializes mMovie with info about a movie
            mMovie = mMovieObject.getTitle() + "\n" + mMovieObject.getReleaseDate() + "\n" + mMovieObject.getVoteAverage() + "\n" + mMovieObject.getOverview();

            try {
                // get from AsyncTask trailers
                FetchJSON fetchJSON = new FetchJSON();

                ArrayList<String> keys = fetchJSON.execute(mMovieObject.getTrailerPath()).get();
                mMovieObject.setKeys(keys);

                if (mMovieObject.getTrailers() != null && mMovieObject.getTrailers().size() > 0) {
                    trailersList = mMovieObject.getTrailers();

                    // If there are trailers - add their links to the share Intent
                    mMovie += "\nAlso check out the Trailers:\n";
                    for (String each : mMovieObject.getTrailers()) {
                        mMovie += "https://www.youtube.com/watch?v=" + each + "\n";
                    }
                }

                // Get Reviews from AsyncTask and put them in a simple TextView
                FetchJSON fetchJSONReviews = new FetchJSON();
                TextView reviews = (TextView) rootView.findViewById(R.id.detail_reviews_textview);

                ArrayList<String> reviewsArrayList = fetchJSONReviews
                        .execute(Utility.getReviewsURL(mMovieObject.getId()).toString())
                        .get();

                if (reviewsArrayList != null) {
                    reviews.setText(TextUtils.join("\n", reviewsArrayList));
                }

            } catch (ExecutionException e) {
                Log.e(LOG_TAG, "error");
            } catch (InterruptedException e2) {
                Log.e(LOG_TAG, "error" + e2);
            }
        }

        return rootView;

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // YouTube view initialization
        youTubePlayerSupportFragment = new YouTubePlayerSupportFragment();
        youTubePlayerSupportFragment.initialize(BuildConfig.YOUTUBE_API_KEY, this);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerSupportFragment).commit();

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
            Log.e(LOG_TAG, "fail");
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

        YPlayer = player;
        // Detect if display is in landscape mode and set YouTube layout height accordingly
        TypedValue tv = new TypedValue();

        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true) && (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE))
        {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());

            if (getView() != null) {
                FrameLayout youtubeFrame = (FrameLayout) getView().findViewById(R.id.youtube_fragment);
                ViewGroup.LayoutParams layoutParams = youtubeFrame.getLayoutParams();

                layoutParams.height = Utility.screenSize(getContext())[1] - (3 * actionBarHeight);
                layoutParams.width = Utility.screenSize(getContext())[0];
                youtubeFrame.setLayoutParams(layoutParams);
            }
        }

        player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {
                Log.v(LOG_TAG, "current video " + s + " trailers " + trailersList.size() + " curr video " + currentVideo + " pos " + trailersList.indexOf(s));
                currentVideo = trailersList.indexOf(s);
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
            if (trailersList != null) {
                YPlayer.cueVideos(trailersList, currentVideo, currentVideoMillis);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        // Save YouTube progress
        if (YPlayer != null) {
            saveInstanceState.putInt(VIDEO_TAG, YPlayer.getCurrentTimeMillis());
            saveInstanceState.putInt(VIDEO_NUM, currentVideo);
            //Log.v(LOG_TAG, "save sec: " + currentVideoMillis + " current video " + currentVideo);
        }

    }

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(getActivity(), RECOVERY_DIALOG_REQUEST).show();
        } else {
            Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(BuildConfig.YOUTUBE_API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerSupportFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
    }

}
