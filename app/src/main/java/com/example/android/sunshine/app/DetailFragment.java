package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherContract.WeatherEntry;

/**
 * A detail fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private static final int DETAIL_LOADER = 0;
    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_DEGREES

    };
    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_PRESSURE = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private ShareActionProvider mShareActionProvider;
    private String mForecast;
    private Uri mUri;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            Log.v("DETAIL-FRAGMENT", mUri.toString());
        }
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.detail_item_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            Log.v(LOG_TAG, "In onCreateLoader");
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (!data.moveToFirst()) {
            Log.v(LOG_TAG, "In onLoadFinished");
            return;
        }

        long dateInMills = data.getLong(COL_WEATHER_DATE);

        String weatherDescription =
                data.getString(COL_WEATHER_DESC);

        ImageView iv = (ImageView) getView().findViewById(R.id.detail_icon);

        switch (weatherDescription) {
            case "Clear":
                iv.setImageResource(R.drawable.art_clear);
                break;
            case "Rain":
                iv.setImageResource(R.drawable.art_rain);
                break;
            case "Clouds":
                iv.setImageResource(R.drawable.art_clouds);
                break;
            case "Fog":
                iv.setImageResource(R.drawable.art_fog);
                break;
            case "Light_clouds":
                iv.setImageResource(R.drawable.art_light_clouds);
                break;
            case "Light_rain":
                iv.setImageResource(R.drawable.art_light_rain);
                break;
            case "Snow":
                iv.setImageResource(R.drawable.art_snow);
                break;
            case "Storm":
                iv.setImageResource(R.drawable.art_storm);
                break;
            default:
                iv.setImageResource(R.drawable.ic_logo);
                break;
        }

        boolean isMetric = Utility.isMetric(getActivity());

        String high = Utility.formatTemperature(getContext(),
                data.getDouble(COL_WEATHER_MAX_TEMP));

        String low = Utility.formatTemperature(getContext(),
                data.getDouble(COL_WEATHER_MIN_TEMP));

        String dateString = Utility.getFormattedMonthDay(getContext(),
                data.getLong(COL_WEATHER_DATE));

        String humidity = String.format(getContext().getString(R.string.format_humidity), data.getFloat(COL_WEATHER_HUMIDITY));

        String wind = Utility.getFormattedWind(getContext(), data.getFloat(COL_WEATHER_WIND_SPEED),
                data.getFloat(COL_WEATHER_DEGREES));

        String pressure = String.format(getContext().getString(R.string.format_pressure), data.getFloat(COL_WEATHER_PRESSURE));

        TextView detailTextView = (TextView) getView().findViewById(R.id.detail_date_day);
        detailTextView.setText(Utility.getDayName(getContext(), dateInMills));

        detailTextView = (TextView) getView().findViewById(R.id.detail_date);
        detailTextView.setText(dateString);

        detailTextView = (TextView) getView().findViewById(R.id.detail_temp_high);
        detailTextView.setText(high);

        detailTextView = (TextView) getView().findViewById(R.id.detail_temp_low);
        detailTextView.setText(low);

        detailTextView = (TextView) getView().findViewById(R.id.detail_desc);
        detailTextView.setText(weatherDescription);

        detailTextView = (TextView) getView().findViewById(R.id.detail_humidity);
        detailTextView.setText(humidity);

        detailTextView = (TextView) getView().findViewById(R.id.detail_wind);
        detailTextView.setText(wind);

        detailTextView = (TextView) getView().findViewById(R.id.detail_pressure);
        detailTextView.setText(pressure);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    void onLocationChanged(String newLocation) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }
}