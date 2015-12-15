package karataiev.dmytro.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

    /**
     * Cache of the children views for a forecast list item.
     */

    public static class ViewHolder {

        public final ImageView posterView;
        public final TextView movieName;
        public final TextView movieReleaseDate;
        public final TextView movieRating;
        public final TextView movieDescription;


        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.poster_imageview);
            movieName = (TextView) view.findViewById(R.id.movie_name);
            movieReleaseDate = (TextView) view.findViewById(R.id.detail_releasedate_textview);
            movieRating = (TextView) view.findViewById(R.id.detail_rating_textview);
            movieDescription = (TextView) view.findViewById(R.id.detail_description_textview);

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
        /*
        String name = intent.getStringExtra("name");
        String path = intent.getStringExtra("path");
        String description = intent.getStringExtra("description");
        String rating = intent.getStringExtra("rating");
        String year = intent.getStringExtra("release_date");
        */
        viewHolder.movieName.setText(fromIntent.name);
        viewHolder.movieDescription.setText(fromIntent.description);
        viewHolder.movieRating.setText(fromIntent.rating);
        viewHolder.movieReleaseDate.setText(fromIntent.year);

        Picasso.with(getContext()).load(fromIntent.pathToDetailImage).into(viewHolder.posterView);

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present.

        // Retrieve the share menu item
        //MenuItem item = menu.findItem(R.id.share);

        // Get the provider and hold onto it to set/change the share intent.
        //mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);


        //if (mShareActionProvider != null) {
        //    mShareActionProvider.setShareIntent(weatherIntent());
        //}
        //else
        //{
        //    Log.v(LOG_TAG, "fail");
        //}
    }
/*
    private Intent weatherIntent() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, mForecast + " #Sunshine");

        return sendIntent;
    } */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
/*
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(LOG_TAG, "In onCreateLoader");

        Intent intent = getActivity().getIntent();

        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        // Use placeholder image for now
        int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
        viewHolder.posterView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        // Description
        String description = data.getString(COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(description);

        // Nicely formatted date
        long date = data.getLong(COL_WEATHER_DATE);
        String friendlyDateText = Utility.getFriendlyDayString(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);
        viewHolder.movieName.setText(friendlyDateText);
        viewHolder.dateView.setText(dateText);

        // High temp + min temp
        boolean isMetric = Utility.isMetric(getActivity());

        double maxTemperature = data.getDouble(COL_WEATHER_MAX_TEMP);
        String high = Utility.formatTemperature(getActivity(), maxTemperature, isMetric);
        viewHolder.movieReleaseDate.setText(high);

        double minTemperature = data.getDouble(COL_WEATHER_MIN_TEMP);
        String low = Utility.formatTemperature(getActivity(), minTemperature, isMetric);
        viewHolder.movieRating.setText(low);

        // Humidity
        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        viewHolder.movieDescription.setText(getActivity().getString(R.string.format_humidity, humidity));

        // Wind speed and direction
        float windSpeed = data.getFloat(COL_WEATHER_WIND);
        float degrees = data.getFloat(COL_WEATHER_DEGREES);
        viewHolder.windView.setText(Utility.getFormattedWind(getActivity(), windSpeed, degrees));

        // Pressure
        float pressure = data.getFloat(COL_WEATHER_PRESSURE);
        viewHolder.pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

        // Share Intent
        mForecast = String.format("%s - %s - %s/%s", dateText, description, high, low);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if ( mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(weatherIntent());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        Toast.makeText(getActivity(), "onLoaderReset", Toast.LENGTH_SHORT).show();
    }

*/

}
