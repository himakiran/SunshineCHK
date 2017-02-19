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
    private static final int FORECAST_LOADER = 12;
    public double geoLat;
    public double geoLong;
    public Uri geolocation;
    // mForecastAdapter has been made a global variable so that it can be accessed from within FetchWeatherTask
    private ForecastAdapter mForecastAdapter;

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
                             Bundle savedInstanceState) {





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
        ListView listview = (ListView) rootView.findViewById(R.id.list_view_forecast);
        listview.setAdapter(mForecastAdapter);
         /*
        The below code from the gist of the class to replace the toast in MainActivity.java has been
        included here ..the gist of the class does not require toast_layout.xml
        */
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = mForecastAdapter.getItem(position).toString();
                /*
                The below code takes geoLat and geoLong from the doInBackground() and sets the
                Uri geolocation.
                 */
                String geo = "geo:" + Double.valueOf(geoLat) + "," + Double.valueOf(geoLong);
                geolocation = Uri.parse(geo);
                /*
                The code below illustrates making a new intent, declaring the second activity to open
                ie DetailActivity and then pass a string parameter ie forecast. which will be used
                by the onCreateView() in detailActivity to set the weataher string.
                It also passes geo which shall be used by the if (id == R.id.detail_see_map)
                function in detailActivity to set the Uri.
                 */

                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, forecast);
                intent.putExtra("GEO-TEXT", geo);
                startActivity(intent);
            }
        });


        return rootView;

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
        //Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
        //locationSetting, System.currentTimeMillis());
        /*
            Corrected this line after advice by ashesh on 1:1 appointment. Now the app is displaying
            data on the main screen.
         */
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.CONTENT_URI;

        return new CursorLoader(this.getContext(), weatherForLocationUri, null, null, null, sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);

    }


}