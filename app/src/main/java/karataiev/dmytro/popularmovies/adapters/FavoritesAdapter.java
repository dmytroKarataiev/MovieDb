package karataiev.dmytro.popularmovies.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import karataiev.dmytro.popularmovies.R;
import karataiev.dmytro.popularmovies.database.MoviesContract;
import karataiev.dmytro.popularmovies.utils.DatabaseTasks;

/**
 * Adapter to the DB
 * Created by karataev on 12/22/15.
 */
public class FavoritesAdapter extends CursorAdapter {

    private final Context mContext;

    public static class ViewHolder {
        public final ImageView mPosterImage;
        public final TextView mPosterText;
        public final ProgressBar mProgressSpinner;
        public final ImageView mFavImage;

        public ViewHolder(View view){
            mPosterImage = (ImageView) view.findViewById(R.id.movie_poster);
            mPosterText = (TextView) view.findViewById(R.id.movie_poster_text);
            mProgressSpinner = (ProgressBar) view.findViewById(R.id.movie_item_spinner);
            mFavImage = (ImageView) view.findViewById(R.id.movie_poster_favorite);

        }
    }

    public FavoritesAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        int layoutId = R.layout.movie_item;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor){

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        int versionIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE);
        final String versionName = cursor.getString(versionIndex);
        //viewHolder.mPosterText.setText(versionName);

        //viewHolder.mFavImage.setImageResource(R.drawable.ic_bookmark_fav);
        Picasso.with(context).load(R.drawable.ic_bookmark_fav).into(viewHolder.mFavImage);

        // On favorite icon click
        viewHolder.mFavImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Not favorite anymore", Toast.LENGTH_LONG).show();

                //viewHolder.mFavImage.setImageResource(R.drawable.ic_bookmark);
                Picasso.with(context).load(R.drawable.ic_bookmark).into(viewHolder.mFavImage);
                // Temp way to delete data from the db
                ContentValues contentValues = new ContentValues();
                contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, versionName);

                // Deletion on background thread
                DatabaseTasks databaseTasks = new DatabaseTasks(mContext);
                databaseTasks.execute(DatabaseTasks.DELETE, contentValues);
            }
        });

        viewHolder.mProgressSpinner.setVisibility(View.GONE);

        // gets image from Picasso cache, instead of db
        int imageIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);
        //byte[] image = cursor.getBlob(imageIndex);
        String image = cursor.getString(imageIndex);
        if (image != null) {
            Picasso.with(context).load(image).into(viewHolder.mPosterImage);
        //    viewHolder.mPosterImage.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }

    }
}
