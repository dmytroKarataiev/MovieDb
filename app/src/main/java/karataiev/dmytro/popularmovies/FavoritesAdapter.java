package karataiev.dmytro.popularmovies;

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

import karataiev.dmytro.popularmovies.database.MoviesContract;

/**
 * Adapter to the DB
 * Created by karataev on 12/22/15.
 */
class FavoritesAdapter extends CursorAdapter {

    private static final String LOG_TAG = FavoritesAdapter.class.getSimpleName();
    private final Context mContext;
    private static int sLoaderID;

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView textView;
        public final ProgressBar spinner;
        public final ImageView favImage;

        public ViewHolder(View view){
            imageView = (ImageView) view.findViewById(R.id.movie_poster);
            textView = (TextView) view.findViewById(R.id.movie_poster_text);
            spinner = (ProgressBar) view.findViewById(R.id.movie_item_spinner);
            favImage = (ImageView) view.findViewById(R.id.movie_poster_favorite);

        }
    }

    public FavoritesAdapter(Context context, Cursor c, int flags, int loaderID){
        super(context, c, flags);
        mContext = context;
        sLoaderID = loaderID;
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
        //viewHolder.textView.setText(versionName);

        //viewHolder.favImage.setImageResource(R.drawable.bookmark_fav);
        Picasso.with(context).load(R.drawable.bookmark_fav).into(viewHolder.favImage);

        // On favorite icon click
        viewHolder.favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Not favorite anymore", Toast.LENGTH_LONG).show();

                //viewHolder.favImage.setImageResource(R.drawable.bookmark);
                Picasso.with(context).load(R.drawable.bookmark).into(viewHolder.favImage);
                // Temp way to delete data from the db
                ContentValues contentValues = new ContentValues();
                contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, versionName);

                // Deletion on background thread
                UtilityAsyncTask utilityAsyncTask = new UtilityAsyncTask(mContext);
                utilityAsyncTask.execute(UtilityAsyncTask.DELETE, contentValues);
            }
        });

        viewHolder.spinner.setVisibility(View.GONE);

        // gets image from Picasso cache, instead of db
        int imageIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);
        //byte[] image = cursor.getBlob(imageIndex);
        String image = cursor.getString(imageIndex);
        if (image != null) {
            Picasso.with(context).load(image).into(viewHolder.imageView);
        //    viewHolder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }

    }
}
