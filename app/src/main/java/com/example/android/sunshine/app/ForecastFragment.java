package com.example.android.sunshine.app;

/**
 * Created by userhk on 07/11/16.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.sunshine.app.data.WeatherContract;

import static com.example.android.sunshine.app.R.layout.fragment_main;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;
    private static final int FORECAST_LOADER = 12;
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };
    public double geoLat;
    public double geoLong;
    public Uri geolocation;
    // mForecastAdapter has been made a global variable so that it can be accessed from within FetchWeatherTask
    private ForecastAdapter mForecastAdapter;
    private boolean mUseTodayLayout;

    private int mpos = ListView.INVALID_POSITION;
    private ListView listview;

    public ForecastFragment() {
    }

    // to create the object
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this code makes sure menu gets displayed and further allows onCreateOptionsMenu to function
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    }

    //to create the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {





        /*
            Code below added from gist of lesson 5/16 of loaders
         */
        // We have commented this as we will now use cursor loader to do the same functionality
//        String locationSetting = Utility.getPreferredLocation(getActivity());
//        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
//        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
//                locationSetting, System.currentTimeMillis());
//        Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri,
//                null, null, null, sortOrder);
//
//        mForecastAdapter = new ForecastAdapter(this.getActivity(), cur, 0);

        // The CursorAdapter will take data from our cursor and populate the ListView.
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        // the below line calls the layout defined in fragment_main
        View rootView = inflater.inflate(fragment_main, container, false);



           /*
            the code below loads the fragment_main.xml layout which has the id listview_forecast
            and connects the adapter to it...
            */
        listview = (ListView) rootView.findViewById(R.id.list_view_forecast);
        listview.setAdapter(mForecastAdapter);
         /*
        The below code from the gist of the class to replace the toast in MainActivity.java has been
        included here ..the gist of the class does not require toast_layout.xml
        */
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                Log.v("CHK-FORECASTFRAGMENT", cursor.getString(1));


                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    ((Callback) getActivity()).onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                            locationSetting, cursor.getLong(COL_WEATHER_DATE)
                    ));


                }
                // save the selected position.
                mpos = position;

            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey("select-pos")) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mpos = savedInstanceState.getInt("select-pos");
        }

        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);

        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mpos != ListView.INVALID_POSITION) {
            outState.putInt("select-pos", mpos);
        }
        super.onSaveInstanceState(outState);
    }

    /*
            This code will now use cursor loader to replace the functionality of lines
            mForecastAdapter = new ForecastAdapter(this.getActivity(), cur, 0);
            in onCreateView
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // to create the menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // this code inflates the menu we detailed in forecastfragment.xml
        inflater.inflate(R.menu.forecastfragment, menu);


    }

    /*
    This code ensures that the weather data displayed is always refreshed whenever the app
    is opened
     */
    @Override
    public void onStart() {
        super.onStart();
        updateWeather();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //code to handle each menu item
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        /*
        The code below calls the setting activity class
         */
        if (id == R.id.settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            return true;
        }
        /*
        This is an example of implicit intent which helps view the location on google map
         */
        if (id == R.id.see_map) {
            Intent intent = new Intent(Intent.ACTION_VIEW);


            //Log.v("CHK-GEO-URI", geo);
            //intent.setData takes an Uri and hence above Uri.parse on string is done
            intent.setData(geolocation);
            if (intent.resolveActivity(this.getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }


        }

        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        FetchWeatherTask fetch = new FetchWeatherTask(getActivity());
        String location = Utility.getPreferredLocation(getActivity());
        Log.v("ForecastFragmtUpdtWthr", location);
        //fetch takes one parameter that is a string
        // also as fetch executes the onPostExecute overridden function ensures that mForecastAdapter gets populated

        try {
            fetch.execute(location);
            //Log.v("CHK-ZIPCODE-FUNCTION", zipcode);
        } catch (Exception e) {
            Log.e("CHK-ZIPCODE-FUNCTION", "String not returned", e);
        }
    }

    /*
    This code makes use of sharedPrefernces to receive menu data from preferences
     */
    private String getzipcode() {
        /*
        The code below gets the saved preference in edittextprefernce
         */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return prefs.getString("location", "91101");

    }

    /*
    This code gets us the user seleted value of the temperature option. if user selects imperial
    the function returns 0 and metric 1.
     */
    private String getImperialOrMetric() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        return prefs.getString("temperature", "true");


    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {
        String locationSetting = Utility.getPreferredLocation(getActivity());
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        /*
            Corrected this line after advice by ashesh on 1:1 appointment. Now the app is displaying
            data on the main screen.
         */
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocation(
                locationSetting);

        //Uri weatherForLocationUri = CONTENT_URI;

        return new CursorLoader(this.getContext(), weatherForLocationUri, FORECAST_COLUMNS, null, null, sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        if (mpos != ListView.INVALID_POSITION) {

            listview.smoothScrollToPosition(mpos);

        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);

    }

    // since we read the location when we create the loader, all we need to do is restart things
    void onLocationChanged() {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mForecastAdapter != null) {
            mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri dateUri);
    }


}