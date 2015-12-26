package karataiev.dmytro.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter with MovieObjects
 * Created by karataev on 12/14/15.
 */
class MovieObjectAdapter extends RecyclerView.Adapter<MovieObjectAdapter.ViewHolder> {

    private final String LOG_TAG = MovieObjectAdapter.class.getSimpleName();

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<MovieObject> mValues;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public String mBoundString;
        public final View mView;
        public final ImageView mImageView;
        public final TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.movie_poster);
            mTextView = (TextView) view.findViewById(R.id.movie_poster_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextView.getText();
        }
    }

    public MovieObjectAdapter(Context context, List<MovieObject> items) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mBoundString = mValues.get(position).getId();
        holder.mTextView.setText(mValues.get(position).getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("movie", mValues.get(position));
                context.startActivity(intent);
            }
        });

        Picasso.with(holder.mImageView.getContext()).load(mValues.get(position).getPosterPath()).into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

//
//    @Override
//    public View getView(final int position, View view, ViewGroup parent) {
//
//        final MovieObject movieObject = getItem(position);
//
//        final ContentValues favValue = Utility.makeContentValues(movieObject);
//
//        if (view == null) {
//            view = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
//        }
//
//        final ProgressBar spinner = (ProgressBar) view.findViewById(R.id.movie_item_spinner);
//        final ImageView poster = (ImageView) view.findViewById(R.id.movie_poster);
//        final ImageView favorite = (ImageView) view.findViewById(R.id.movie_poster_favorite);
//
//        // set favorites icon
//        if (Utility.isFavorite(getContext(), movieObject)) {
//            favorite.setImageResource(R.drawable.bookmark_fav);
//        } else {
//            favorite.setImageResource(R.drawable.bookmark);
//        }
//
//        // Scale posters correctly
//        poster.getLayoutParams().height = Utility.screenSize(getContext())[5];
//        spinner.getLayoutParams().height = Utility.screenSize(getContext())[5];
//        favorite.getLayoutParams().height = (int) Math.round(Utility.screenSize(getContext())[5] * 0.2);
//
//        spinner.setVisibility(View.VISIBLE);
//        favorite.setVisibility(View.GONE);
//
//        Picasso.with(getContext()).load(movieObject.getPosterPath()).into(poster, new Callback() {
//            @Override
//            public void onSuccess() {
//                spinner.setVisibility(View.GONE);
//                favorite.setVisibility(View.VISIBLE);
//
//                // On favorite icon click
//                favorite.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Toast.makeText(getContext(), movieObject.getTitle(), Toast.LENGTH_LONG).show();
//
//                        if (!Utility.isFavorite(getContext(), movieObject)) {
//
//                            // Save drawable for later usage
//                            byte[] bitmapData = Utility.makeByteArray(poster.getDrawable());
//
//                            // save byte array of an image to the database
//                            favValue.put(MoviesContract.MovieEntry.COLUMN_IMAGE, bitmapData);
//
//                            favorite.setImageResource(R.drawable.bookmark_fav);
//
//                            // Insert on background thread
//                            UtilityAsyncTask utilityAsyncTask = new UtilityAsyncTask(getContext());
//                            utilityAsyncTask.execute(UtilityAsyncTask.INSERT, favValue);
//                        } else {
//                            favorite.setImageResource(R.drawable.bookmark);
//
//                            // Delete on background thread
//                            UtilityAsyncTask utilityAsyncTask = new UtilityAsyncTask(getContext());
//                            utilityAsyncTask.execute(UtilityAsyncTask.DELETE, favValue);
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onError() {
//                poster.setBackgroundResource(R.color.white);
//                spinner.setVisibility(View.GONE);
//                favorite.setVisibility(View.GONE);
//            }
//        });
//
//        // If movie doesn't have an image - uses text instead
//        if (movieObject.getPosterPath().contains("null"))
//        {
//            TextView imageText = (TextView) view.findViewById(R.id.movie_poster_text);
//            imageText.setText(movieObject.getTitle());
//        }
//        poster.setContentDescription(movieObject.getTitle());
//
//        return view;
//    }

}
