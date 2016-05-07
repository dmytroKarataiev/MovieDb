package karataiev.dmytro.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import karataiev.dmytro.popularmovies.database.MoviesContract;
import karataiev.dmytro.popularmovies.model.MovieObject;
import karataiev.dmytro.popularmovies.remote.FetchJSON;
import karataiev.dmytro.popularmovies.remote.FetchMovies;
import karataiev.dmytro.popularmovies.utils.DatabaseTasks;
import karataiev.dmytro.popularmovies.utils.Utility;

/**
 * Detailed Movie Fragment with poster, rating, description.
 * Created by karataev on 12/15/15.
 */
public class DetailFragment extends Fragment implements YouTubePlayer.OnInitializedListener {

    private final String TAG = DetailFragment.class.getSimpleName();

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

    @BindView(R.id.movie_poster) ImageView mImagePoster;
    @BindView(R.id.movie_item_spinner) ProgressBar mProgressSpinner;
    @BindView(R.id.movie_poster_favorite) ImageView mImageFavorite;
    @BindView(R.id.detail_releasedate_textview) TextView mTextRelease;
    @BindView(R.id.detail_rating_textview) TextView mTextRating;
    @BindView(R.id.detail_description_textview) TextView mTextDescription;
    @BindView(R.id.detail_votecount_textview) TextView mTextVotes;
    @BindView(R.id.detail_background) LinearLayout mLinearBackground;
    @BindView(R.id.detail_reviews_textview) TextView mTextReviews;
    @Nullable @BindView(R.id.backdrop) ImageView mImageBackdrop;
    Unbinder mUnbinder;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Intent intent;

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieObject = arguments.getParcelable(MovieObject.MOVIE_OBJECT);
        } else {
            // Gets data from intent (using parcelable) and populates views
            intent = this.getActivity().getIntent();
            mMovieObject = intent.getParcelableExtra(MovieObject.MOVIE_OBJECT);
            if (mMovieObject == null) {
                try {
                    FetchMovies fetchFirstMovie = new FetchMovies(getContext(), null, false, 1);

                    ArrayList<MovieObject> temporary = fetchFirstMovie.execute(Utility.getUrl(1, getContext()).toString()).get();
                    if (temporary != null) {
                        mMovieObject = temporary.get(0);
                    }
                } catch (ExecutionException e) {
                    Log.e(TAG, "error");
                } catch (InterruptedException e2) {
                    Log.e(TAG, "error" + e2);
                }
            }
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);

        if (mMovieObject != null) {
            loadData();
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
        ShareActionProvider actionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (actionProvider != null) {
            actionProvider.setShareIntent(movieIntent());
        } else {
            Log.e(TAG, "fail to set a share intent");
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

        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true) &&
                (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE))
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        if (YPlayer != null) {
            YPlayer.release();
        }
    }

    /**
     * Loads data
     */
    private void loadData() {

        // Maybe it increases chance of OOM
        // Files from db to load into ImageViews
        // byte[] posterLoad = intent.getByteArrayExtra("poster");
        // byte[] backdropLoad = intent.getByteArrayExtra("backdrop");
        // File posterFile = Utility.makeFile(getContext(), posterLoad, mMovieObject.getId() + "poster");
        // File backdropFile = Utility.makeFile(getContext(), backdropLoad, mMovieObject.getId() + "backdrop");

        // ActionBar title and image adding
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(mMovieObject.getTitle());
        }

        ImageView tempForBackdrop;
        if (mImageBackdrop == null) {
            mImageBackdrop = ButterKnife.findById(getActivity(), R.id.backdrop);
        }

        mTextDescription.setText(mMovieObject.getOverview());
        mTextRating.setText(mMovieObject.getVoteAverage());
        mTextRelease.setText(mMovieObject.getReleaseDate());
        mTextVotes.setText(String.format(getActivity().getString(R.string.votes_text), mMovieObject.getVoteCount()));

        if (Utility.isFavorite(getContext(), mMovieObject)) {
            Picasso.with(getContext()).load(R.drawable.ic_bookmark_fav).into(mImageFavorite);
        } else {
            Picasso.with(getContext()).load(R.drawable.ic_bookmark).into(mImageFavorite);
        }

        // Callback inside of Picasso Call
        Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                mProgressSpinner.setVisibility(View.GONE);
                mImageFavorite.setVisibility(View.VISIBLE);

                Palette palette = Palette.from(((BitmapDrawable) mImagePoster.getDrawable()).getBitmap()).generate();
                mLinearBackground.setBackgroundColor(palette.getLightVibrantColor(0));
                CollapsingToolbarLayout toolbarLayout = ButterKnife.findById(getActivity(), R.id.collapsing_toolbar);
                toolbarLayout.setContentScrimColor(palette.getVibrantColor(0));

                if (Build.VERSION.SDK_INT >= 21) {
                    Window window = getActivity().getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(palette.getDarkVibrantColor(0));
                }

                // On mImageFavorite icon click
                mImageFavorite.setOnClickListener(v -> {

                    ContentValues favValue = Utility.makeContentValues(mMovieObject);

                    Toast.makeText(getContext(), mMovieObject.getTitle(), Toast.LENGTH_LONG).show();

                    if (!Utility.isFavorite(getContext(), mMovieObject)) {

                        // Save drawable for later usage
                        byte[] bitmapData = Utility.makeByteArray(mImagePoster.getDrawable());
                        byte[] backdropBitmap = Utility.makeByteArray(mImageBackdrop.getDrawable());

                        // save byte array of an image to the database
                        favValue.put(MoviesContract.MovieEntry.COLUMN_IMAGE, bitmapData);
                        favValue.put(MoviesContract.MovieEntry.COLUMN_FULL_IMAGE, backdropBitmap);

                        mImageFavorite.setImageResource(R.drawable.ic_bookmark_fav);

                        // Insert on background thread
                        DatabaseTasks databaseTasks = new DatabaseTasks(getContext());
                        databaseTasks.execute(DatabaseTasks.INSERT, favValue);
                    } else {
                        mImageFavorite.setImageResource(R.drawable.ic_bookmark);

                        // Delete on background thread
                        DatabaseTasks databaseTasks = new DatabaseTasks(getContext());
                        databaseTasks.execute(DatabaseTasks.DELETE, favValue);
                    }
                });
            }

            @Override
            public void onError() {
                mImagePoster.setBackgroundResource(R.color.white);
                mProgressSpinner.setVisibility(View.GONE);
                mImageFavorite.setVisibility(View.GONE);
            }
        };

        // Not sure if it increases risk of OOM error
        // if (posterFile != null && backdropFile != null) {
        //     Picasso.with(getContext()).load(backdropFile).into(backdrop);
        //     Picasso.with(getContext()).load(posterFile).into(viewHolder.mImagePoster, callback);
        // } else if (backdropFile == null && posterFile != null) {
        //     Picasso.with(getContext()).load(posterFile).into(viewHolder.mImagePoster, callback);
        //     Picasso.with(getContext()).load(mMovieObject.getBackdropPath()).into(backdrop);
        // } else {
        Picasso.with(getContext()).load(mMovieObject.getBackdropPath()).into(mImageBackdrop);
        Picasso.with(getContext()).load(mMovieObject.getPosterPath()).into(mImagePoster, callback);
        // }

        // Initializes mMovie with info about a movie
        mMovie = mMovieObject.getTitle() +
                "\n" + mMovieObject.getReleaseDate() +
                "\n" + mMovieObject.getVoteAverage() +
                "\n" + mMovieObject.getOverview();

        try {
            // get from AsyncTask trailers
            FetchJSON fetchJSON = new FetchJSON();

            ArrayList<String> keys = fetchJSON.execute(mMovieObject.getTrailerPath()).get();
            mMovieObject.setKeys(keys);

            if (mMovieObject.getTrailers() != null && mMovieObject.getTrailers().size() > 0) {
                trailersList = mMovieObject.getTrailers();

                // If there are trailers - add their links to the share Intent
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("\nAlso check out the Trailers:\n");
                for (String each : mMovieObject.getTrailers()) {
                    stringBuilder.append("https://www.youtube.com/watch?v=").append(each).append("\n");
                }
                mMovie += stringBuilder.toString();
            }

            // Get Reviews from AsyncTask and put them in a simple TextView
            FetchJSON fetchJSONReviews = new FetchJSON();

            ArrayList<String> reviewsArrayList = fetchJSONReviews
                    .execute(Utility.getReviewsURL(mMovieObject.getId()).toString())
                    .get();

            if (reviewsArrayList != null) {
                mTextReviews.setText(TextUtils.join("\n", reviewsArrayList));
            }

        } catch (ExecutionException e) {
            Log.e(TAG, "error");
        } catch (InterruptedException e2) {
            Log.e(TAG, "error" + e2);
        }
    }

}
