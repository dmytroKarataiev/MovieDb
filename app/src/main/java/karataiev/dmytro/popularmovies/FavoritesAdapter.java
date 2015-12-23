package karataiev.dmytro.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import karataiev.dmytro.popularmovies.database.MoviesContract;

/**
 * Created by karataev on 12/22/15.
 */
public class FavoritesAdapter extends CursorAdapter {

    private static final String LOG_TAG = FavoritesAdapter.class.getSimpleName();
    private Context mContext;
    private static int sLoaderID;
    ContentResolver contentResolver;

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
        Log.d(LOG_TAG, "FavoritesAdapter");
        mContext = context;
        sLoaderID = loaderID;
        contentResolver = context.getContentResolver();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        int layoutId = R.layout.movie_item;

        Log.d(LOG_TAG, "In new View");

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor){

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        Log.d(LOG_TAG, "In bind View");

        int versionIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE);
        final String versionName = cursor.getString(versionIndex);
        viewHolder.textView.setText(versionName);

        // On favorite icon click
        viewHolder.favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Not favorite anymore", Toast.LENGTH_LONG).show();

                viewHolder.favImage.setImageResource(R.drawable.bookmark);
                contentResolver.delete(MoviesContract.MovieEntry.CONTENT_URI,
                        MoviesContract.MovieEntry.COLUMN_TITLE + " = ?",
                        new String[]{ versionName });
            }
        });

        viewHolder.spinner.setVisibility(View.GONE);

        int imageIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_IMAGE);
        byte[] image = cursor.getBlob(imageIndex);
        Log.i(LOG_TAG, "Image reference extracted");

        if (image != null) {
            viewHolder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }

    }
}
