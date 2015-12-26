package karataiev.dmytro.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import karataiev.dmytro.popularmovies.database.MoviesContract;

/**
 * Adapter with MovieObjects
 * Created by karataev on 12/14/15.
 */
class MovieObjectAdapter extends RecyclerView.Adapter<MovieObjectAdapter.ViewHolder> {

    private final String LOG_TAG = MovieObjectAdapter.class.getSimpleName();

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<MovieObject> mValues;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public String mBoundString;
        public final View mView;
        public final ImageView poster;
        public final ImageView favorite;
        public final ProgressBar spinner;
        public final TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            poster = (ImageView) view.findViewById(R.id.movie_poster);
            favorite = (ImageView) view.findViewById(R.id.movie_poster_favorite);
            spinner = (ProgressBar) view.findViewById(R.id.movie_item_spinner);
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
        mContext = context;
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

        final MovieObject movieObject = mValues.get(position);

        final ContentValues favValue = Utility.makeContentValues(movieObject);

        holder.mBoundString = movieObject.getId();

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("movie", mValues.get(position));
                context.startActivity(intent);
            }
        });

        Picasso.with(holder.poster.getContext()).load(mValues.get(position).getPosterPath()).into(holder.poster);

        // set favorites icon
        if (Utility.isFavorite(holder.favorite.getContext(), movieObject)) {
            holder.favorite.setImageResource(R.drawable.bookmark_fav);
        } else {
            holder.favorite.setImageResource(R.drawable.bookmark);
        }

        // Scale posters correctly
        holder.poster.getLayoutParams().height = Utility.screenSize(mContext)[5];
        holder.spinner.getLayoutParams().height = Utility.screenSize(mContext)[5];
        holder.favorite.getLayoutParams().height = (int) Math.round(Utility.screenSize(mContext)[5] * 0.2);

        holder.spinner.setVisibility(View.VISIBLE);
        holder.favorite.setVisibility(View.GONE);

        Picasso.with(mContext).load(movieObject.getPosterPath()).into(holder.poster, new Callback() {
            @Override
            public void onSuccess() {
                holder.spinner.setVisibility(View.GONE);
                holder.favorite.setVisibility(View.VISIBLE);

                // On favorite icon click
                holder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(mContext, movieObject.getTitle(), Toast.LENGTH_LONG).show();

                        if (!Utility.isFavorite(mContext, movieObject)) {

                            // Save drawable for later usage
                            byte[] bitmapData = Utility.makeByteArray(holder.poster.getDrawable());

                            // save byte array of an image to the database
                            favValue.put(MoviesContract.MovieEntry.COLUMN_IMAGE, bitmapData);

                            holder.favorite.setImageResource(R.drawable.bookmark_fav);

                            // Insert on background thread
                            UtilityAsyncTask utilityAsyncTask = new UtilityAsyncTask(mContext);
                            utilityAsyncTask.execute(UtilityAsyncTask.INSERT, favValue);
                        } else {
                            holder.favorite.setImageResource(R.drawable.bookmark);

                            // Delete on background thread
                            UtilityAsyncTask utilityAsyncTask = new UtilityAsyncTask(mContext);
                            utilityAsyncTask.execute(UtilityAsyncTask.DELETE, favValue);
                        }
                    }
                });
            }

            @Override
            public void onError() {
                holder.poster.setBackgroundResource(R.color.white);
                holder.spinner.setVisibility(View.GONE);
                holder.favorite.setVisibility(View.GONE);
            }
        });

        // If movie doesn't have an image - uses text instead
        if (movieObject.getPosterPath().contains("null"))
        {
            holder.mTextView.setText(movieObject.getTitle());
        } else {
            holder.mTextView.setText("");
        }
        holder.poster.setContentDescription(movieObject.getTitle());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

}
