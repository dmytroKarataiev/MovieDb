/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016. Dmytro Karataiev
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import karataiev.dmytro.popularmovies.adapters.ActorsAdapter;
import karataiev.dmytro.popularmovies.database.MoviesContract;
import karataiev.dmytro.popularmovies.interfaces.ItemClickListener;
import karataiev.dmytro.popularmovies.model.Consts;
import karataiev.dmytro.popularmovies.model.MovieCast;
import karataiev.dmytro.popularmovies.model.MovieCredits;
import karataiev.dmytro.popularmovies.model.MovieObject;
import karataiev.dmytro.popularmovies.model.Review;
import karataiev.dmytro.popularmovies.model.Trailer;
import karataiev.dmytro.popularmovies.remote.ApiService;
import karataiev.dmytro.popularmovies.remote.FetchMovies;
import karataiev.dmytro.popularmovies.utils.DatabaseTasks;
import karataiev.dmytro.popularmovies.utils.Utility;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Detailed Movie Fragment with poster, rating, description.
 * Created by karataev on 12/15/15.
 */
public class DetailFragment extends Fragment implements YouTubePlayer.OnInitializedListener, ItemClickListener<MovieCast, View> {

    private final String TAG = DetailFragment.class.getSimpleName();

    // String which is used in share intent
    private String mMovie;
    private MovieObject mMovieObject;
    private ShareActionProvider mShareActionProvider;

    // YouTube variables
    private YouTubePlayer mYouTubePlayer;
    private YouTubePlayerSupportFragment youTubePlayerSupportFragment;

    // save current video and position
    private int currentVideoMillis;
    private int currentVideo;
    private final String VIDEO_TAG = "youtube";
    private final String VIDEO_NUM = "video_num";

    // list of videos
    private List<String> mTrailersList;
    private List<String> mReviewsList;

    @BindView(R.id.movie_poster)
    ImageView mImagePoster;
    @BindView(R.id.movie_item_spinner)
    ProgressBar mProgressSpinner;
    @BindView(R.id.movie_poster_favorite)
    ImageView mImageFavorite;
    @BindView(R.id.detail_releasedate_textview)
    TextView mTextRelease;
    @BindView(R.id.detail_rating_textview)
    TextView mTextRating;
    @BindView(R.id.detail_description_textview)
    TextView mTextDescription;
    @BindView(R.id.detail_votecount_textview)
    TextView mTextVotes;
    @BindView(R.id.detail_background)
    NestedScrollView mLinearBackground;
    @BindView(R.id.detail_reviews_textview)
    TextView mTextReviews;
    @Nullable @BindView(R.id.backdrop)
    ImageView mImageBackdrop;

    private Unbinder mUnbinder;

    // Actors RecyclerList view
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerActors;
    private ActorsAdapter mActorsAdapter;

    // RxJava
    private ApiService mApiService;
    private CompositeSubscription _subscriptions;

    // Callback inside of Picasso Call
    private Callback callback = new Callback() {
        @Override
        public void onSuccess() {
            mProgressSpinner.setVisibility(View.GONE);
            mImageFavorite.setVisibility(View.VISIBLE);

            Palette palette = Palette.from(((BitmapDrawable) mImagePoster.getDrawable()).getBitmap()).generate();

            int lightVibrantColor = palette.getLightVibrantColor(0);
            if (lightVibrantColor == 0) {
                lightVibrantColor = palette.getLightMutedColor(0);
            }

            int vibrantColor = palette.getVibrantColor(0);
            if (vibrantColor == 0) {
                vibrantColor = palette.getMutedColor(0);
            }

            int darkVibrantColor = palette.getDarkVibrantColor(0);
            if (darkVibrantColor == 0) {
                darkVibrantColor = palette.getDarkMutedColor(0);
            }

            mLinearBackground.setBackgroundColor(lightVibrantColor);
            CollapsingToolbarLayout toolbarLayout = ButterKnife.findById(getActivity(), R.id.collapsing_toolbar);
            toolbarLayout.setContentScrimColor(vibrantColor);

            if (Build.VERSION.SDK_INT >= 21) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(darkVibrantColor);
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

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApiService = App.getApiManager().getMoviesService();
        _subscriptions = new CompositeSubscription();

        mReviewsList = new ArrayList<>();
        mTrailersList = new ArrayList<>();

        if (savedInstanceState != null && savedInstanceState.containsKey(VIDEO_TAG)) {
            // Get video progress
            currentVideoMillis = savedInstanceState.getInt(VIDEO_TAG);
            currentVideo = savedInstanceState.getInt(VIDEO_NUM);
        }

        Intent intent;

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieObject = arguments.getParcelable(MovieObject.MOVIE_OBJECT);
        } else if (getActivity().getIntent().hasExtra(Consts.MOVIE_ID)) {
            String movieId = getActivity().getIntent().getStringExtra(Consts.MOVIE_ID);
            _subscriptions.add(App.getApiManager().getMoviesService()
                    .getMovie(movieId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<MovieObject>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(MovieObject movieObject) {
                            // TODO: 5/28/16 refactor
                            mMovieObject = movieObject;
                            mMovieObject.makeNice(getContext());
                            loadData();
                            initRecycler();
                        }
                    }));
        } else {
            // Gets data from intent (using parcelable) and populates views
            intent = this.getActivity().getIntent();
            mMovieObject = intent.getParcelableExtra(MovieObject.MOVIE_OBJECT);
            Log.d("DetailFragment", "(mMovieObject != null):" + (mMovieObject != null));
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);

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

        // TODO: 5/28/16 refactor
        if (mMovieObject != null) {
            loadData();
            initRecycler();
        }

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
        } else {
            Log.e(TAG, "fail to set a share intent");
        }
    }

    /**
     * Method to populate Intent with data
     *
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

        mYouTubePlayer = player;
        // Detect if display is in landscape mode and set YouTube layout height accordingly
        TypedValue tv = new TypedValue();

        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true) &&
                (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());

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
                Log.d(TAG, "onLoaded: ");
                currentVideo = mTrailersList.indexOf(s);
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
        if (!wasRestored) {
            if (mTrailersList != null && mTrailersList.size() > 0) {
                Log.d(TAG, "onInitializationSuccess: ");
                mYouTubePlayer.cueVideos(mTrailersList, currentVideo, currentVideoMillis);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        // Save YouTube progress
        if (mYouTubePlayer != null) {
            try {
                saveInstanceState.putInt(VIDEO_TAG, mYouTubePlayer.getCurrentTimeMillis());
                saveInstanceState.putInt(VIDEO_NUM, currentVideo);
            } catch (IllegalStateException e) {
                Log.e(TAG, "YouTube state error:" + e);
            }
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
        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
        }
        if (_subscriptions != null && !_subscriptions.isUnsubscribed()) {
            _subscriptions.unsubscribe();
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

        if (mImageBackdrop == null) {
            mImageBackdrop = ButterKnife.findById(getActivity(), R.id.backdrop);
        }

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

        mTextDescription.setText(mMovieObject.getOverview());
        mTextRating.setText(mMovieObject.getVoteAverage());
        mTextRelease.setText(mMovieObject.getReleaseDate());
        mTextVotes.setText(String.format(getActivity().getString(R.string.votes_text), mMovieObject.getVoteCount()));

        if (Utility.isFavorite(getContext(), mMovieObject)) {
            Picasso.with(getContext()).load(R.drawable.ic_bookmark_fav).into(mImageFavorite);
        } else {
            Picasso.with(getContext()).load(R.drawable.ic_bookmark).into(mImageFavorite);
        }

        // Initializes mMovie with info about a movie
        mMovie = mMovieObject.getTitle() +
                "\n" + mMovieObject.getReleaseDate() +
                "\n" + mMovieObject.getVoteAverage() +
                "\n" + mMovieObject.getOverview();

    }

    private void initRecycler() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        mActorsAdapter = new ActorsAdapter(getContext(), null);

        mRecyclerActors.setLayoutManager(layoutManager);
        mRecyclerActors.setAdapter(mActorsAdapter);
        mRecyclerActors.setNestedScrollingEnabled(true);

        _subscriptions.add(
                mApiService.getMovieCredits(mMovieObject.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<MovieCredits>() {
                            @Override
                            public void onCompleted() {
                                Log.d(TAG, "onCompleted: ");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: ", e);
                            }

                            @Override
                            public void onNext(MovieCredits movieCredits) {
                                mActorsAdapter = new ActorsAdapter(getContext(), movieCredits);
                                mRecyclerActors.setAdapter(mActorsAdapter);
                                mActorsAdapter.setData(DetailFragment.this);
                            }
                        })
        );

        _subscriptions.add(
                mApiService.getMovieVideos(mMovieObject.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(movieTrailers -> Observable.from(movieTrailers.getTrailers()))
                        .subscribe(new Observer<Trailer>() {
                            @Override
                            public void onCompleted() {
                                Log.d(TAG, "Trailers onCompleted: ");
                                if (mYouTubePlayer != null) {
                                    mYouTubePlayer.cueVideos(mTrailersList);
                                }
                                // If there are trailers - add their links to the share Intent
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("\nAlso check out the Trailers:\n");
                                for (String each : mTrailersList) {
                                    stringBuilder.append("https://www.youtube.com/watch?v=").append(each).append("\n");
                                }
                                mMovie += stringBuilder.toString();
                                if (mShareActionProvider != null) {
                                    mShareActionProvider.setShareIntent(movieIntent());
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: ", e);
                            }

                            @Override
                            public void onNext(Trailer trailer) {
                                mTrailersList.add(trailer.getKey());
                            }
                        })
        );

        _subscriptions.add(
                mApiService.getMovieReviews(mMovieObject.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(reviews -> Observable.from(reviews.getReviews()))
                        .subscribe(new Observer<Review>() {
                            @Override
                            public void onCompleted() {
                                if (mReviewsList != null) {
                                    mTextReviews.setText(TextUtils.join("\n", mReviewsList));
                                }
                                Log.d(TAG, "reviews completed");
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Review review) {
                                mReviewsList.add(review.getAuthor() + "\n" + review.getContent());
                            }
                        })
        );
    }

    @Override
    public void onItemClicked(MovieCast movieCast, View view) {
        Intent intent = new Intent(getContext(), ActorActivity.class);
        intent.putExtra(Consts.ACTOR_EXTRA, movieCast);
        startActivity(intent);
    }
}
