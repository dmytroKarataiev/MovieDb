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

    private String LOG_TAG = DetailFragment.class.getSimpleName();
    private ViewHolder viewHolder;

    /**
     * Cache of the children views for a forecast list item.
     */

    public static class ViewHolder {

        public final ImageView iconView;
        public final TextView friendlyDateView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final TextView humidityView;
        public final TextView windView;
        public final TextView pressureView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.detail_icon);
            friendlyDateView = (TextView) view.findViewById(R.id.detail_day_textview);
            dateView = (TextView) view.findViewById(R.id.detail_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.detail_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.detail_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.detail_low_textview);
            humidityView = (TextView) view.findViewById(R.id.detail_humidity_textview);
            windView = (TextView) view.findViewById(R.id.detail_wind_textview);
            pressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);
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
        String name = intent.getStringExtra("name");
        String path = intent.getStringExtra("path");

        viewHolder.descriptionView.setText(name);
        viewHolder.dateView.setText(path);

        Picasso.with(getContext()).load(path).into(viewHolder.iconView);

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);

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
        viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        // Description
        String description = data.getString(COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(description);

        // Nicely formatted date
        long date = data.getLong(COL_WEATHER_DATE);
        String friendlyDateText = Utility.getFriendlyDayString(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);
        viewHolder.friendlyDateView.setText(friendlyDateText);
        viewHolder.dateView.setText(dateText);

        // High temp + min temp
        boolean isMetric = Utility.isMetric(getActivity());

        double maxTemperature = data.getDouble(COL_WEATHER_MAX_TEMP);
        String high = Utility.formatTemperature(getActivity(), maxTemperature, isMetric);
        viewHolder.highTempView.setText(high);

        double minTemperature = data.getDouble(COL_WEATHER_MIN_TEMP);
        String low = Utility.formatTemperature(getActivity(), minTemperature, isMetric);
        viewHolder.lowTempView.setText(low);

        // Humidity
        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        viewHolder.humidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

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
