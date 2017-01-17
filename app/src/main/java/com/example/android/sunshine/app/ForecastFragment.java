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
import android.widget.ListView;

import com.example.android.sunshine.app.data.WeatherContract;

import static com.example.android.sunshine.app.R.layout.fragment_main;

//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import java.util.ArrayList;

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
        getLoaderManager().initLoader(12, null, this);
    }

    //to create the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // this is the array values that get filled in the layout
        //ArrayList<String> dailyWeatherUpdate = new ArrayList<String>();

            /*
             the below code connects the adapter to the string array and ensures the values are

            correctly filled in list_item_forecast_textview which is part of list_item_forecast layout

            */
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


//        FetchWeatherTask fetch = new FetchWeatherTask(getContext());
//        //fetch takes one parameter that is a string
//        // also as fetch executes the onPostExecute overridden function ensures that mForecastAdapter gets populated
//        fetch.execute(getzipcode());
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
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                //String forecast = mForecastAdapter.getItem(position);
//                /*
//                The below code takes geoLat and geoLong from the doInBackground() and sets the
//                Uri geolocation.
//                 */
//                String geo = "geo:" + Double.valueOf(geoLat) + "," + Double.valueOf(geoLong);
//                geolocation = Uri.parse(geo);
//                /*
//                The code below illustrates making a new intent, declaring the second activity to open
//                ie DetailActivity and then pass a string parameter ie forecast. which will be used
//                by the onCreateView() in detailActivity to set the weataher string.
//                It also passes geo which shall be used by the if (id == R.id.detail_see_map)
//                function in detailActivity to set the Uri.
//                 */
//
//                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, forecast);
//                intent.putExtra("GEO-TEXT", geo);
//                startActivity(intent);
//            }
//        });


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
        return prefs.getString("location", "110010");

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


//
//    // to fetch data from external source
//    public class FetchWeatherTask extends AsyncTask<String, String, String[]> {
//
//        public FetchWeatherTask() {
//
//        }
//
//        /* The date/time conversion code is going to be moved outside the asynctask later,
// * so for convenience we're breaking it out into its own method now.
// */
//        private String getReadableDateString(long time) {
//            // Because the API returns a unix timestamp (measured in seconds),
//            // it must be converted to milliseconds in order to be converted to valid date.
//            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
//            return shortenedDateFormat.format(time);
//        }
//
//        /**
//         * Prepare the weather high/lows for presentation.
//         */
//        private String formatHighLows(double high, double low) {
//            // For presentation, assume the user doesn't care about tenths of a degree.
//            long roundedHigh = Math.round(high);
//            long roundedLow = Math.round(low);
//
//            String highLowStr = roundedHigh + "/" + roundedLow;
//            return highLowStr;
//        }
//
//
//        /**
//         * Take the String representing the complete forecast in JSON Format and
//         * pull out the data we need to construct the Strings needed for the wireframes.
//         * <p>
//         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
//         * into an Object hierarchy for us.
//         */
//        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
//                throws JSONException {
//
//            // These are the names of the JSON objects that need to be extracted.
//            final String OWM_LIST = "list";
//            final String OWM_WEATHER = "weather";
//            final String OWM_TEMPERATURE = "temp";
//            final String OWM_MAX = "max";
//            final String OWM_MIN = "min";
//            final String OWM_DESCRIPTION = "main";
//
//            JSONObject forecastJson = new JSONObject(forecastJsonStr);
//
//
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
//
//            // OWM returns daily forecasts based upon the local time of the city that is being
//            // asked for, which means that we need to know the GMT offset to translate this data
//            // properly.
//
//            // Since this data is also sent in-order and the first day is always the
//            // current day, we're going to take advantage of that to get a nice
//            // normalized UTC date for all of our weather.
//
//            Time dayTime = new Time();
//            dayTime.setToNow();
//
//            // we start at the day returned by local time. Otherwise this is a mess.
//            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
//
//            // now we work exclusively in UTC
//            dayTime = new Time();
//
//            String[] resultStrs = new String[numDays];
//            for (int i = 0; i < weatherArray.length(); i++) {
//                // For now, using the format "Day, description, hi/low"
//                String day;
//                String description;
//                String highAndLow;
//
//                // Get the JSON object representing the day
//                JSONObject dayForecast = weatherArray.getJSONObject(i);
//
//                // The date/time is returned as a long.  We need to convert that
//                // into something human-readable, since most people won't read "1400356800" as
//                // "this saturday".
//                long dateTime;
//                // Cheating to convert this to UTC time, which is what we want anyhow
//                dateTime = dayTime.setJulianDay(julianStartDay + i);
//                day = getReadableDateString(dateTime);
//
//                // description is in a child array called "weather", which is 1 element long.
//                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWM_DESCRIPTION);
//
//                // Temperatures are in a child object called "temp".  Try not to name variables
//                // "temp" when working with temperature.  It confuses everybody.
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//                double high = temperatureObject.getDouble(OWM_MAX);
//                double low = temperatureObject.getDouble(OWM_MIN);
//
//                try {
//                    if (getImperialOrMetric().equals("0")) {
//                        high = (high * 1.8) + 32;
//                        low = (low * 1.8) + 32;
//                    } else
//                    //Log.e("CHK-PREFS-TEST", getImperialOrMetric());
//                    {
//                    }
//                } catch (Exception e) {
//                    Log.e("CHK-PREFS-TEST", "IF-DID-NOT-EXEC", e);
//                }
//
//                highAndLow = formatHighLows(high, low);
//                resultStrs[i] = day + " - " + description + " - " + highAndLow;
//            }
//
//            /*for (String s : resultStrs) {
//                Log.v("CHK-TAG", "Forecast entry: " + s);
//            }*/
//
//            return resultStrs;
//
//        }
//
//
//        @Override
//        public String[] doInBackground(String... params) {
//            // These two need to be declared outside the try/catch
//// so that they can be closed in the finally block.
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//            // here we define that doInBackground takes one string parameter.
//            String zipCode = params[0];
//
//// Will contain the raw JSON response as a string.
//            String forecastJsonStr = null;
//            String numOfDays = "7";
//
//            try {
//                // Construct the URL for the OpenWeatherMap query
//                // Possible parameters are available at OWM's forecast API page, at
//                // http://openweathermap.org/API#forecast
//                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q="+zipCode+"&mode=json&units=metric&cnt=7&appid=43015412ed029d0518c54689fa3c3a37");
//                Uri.Builder weatherURL = new Uri.Builder();
//                String wUrl = "api.openweathermap.org";
//                weatherURL.scheme("http")
//                        .authority(wUrl)
//                        .appendPath("data")
//                        .appendPath("2.5")
//                        .appendPath("forecast")
//                        .appendPath("daily")
//                        .appendQueryParameter("q", zipCode)
//                        .appendQueryParameter("mode", "json")
//                        .appendQueryParameter("units", "metric")
//                        .appendQueryParameter("cnt", numOfDays)
//                        .appendQueryParameter("appid", "43015412ed029d0518c54689fa3c3a37")
//                        .fragment("section-name");
//
//                // Create the request to OpenWeatherMap, and open the connection
//                URL url = new URL(weatherURL.build().toString());
//                Log.v("CHK-URL", url.toString());
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    forecastJsonStr = null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    forecastJsonStr = null;
//                }
//                forecastJsonStr = buffer.toString();
//            } catch (IOException e) {
//                Log.e("PlaceholderFragment", "Error ", e);
//                // If the code didn't successfully get the weather data, there's no point in attempting
//                // to parse it.
//                forecastJsonStr = null;
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e("PlaceholderFragment", "Error closing stream", e);
//                    }
//                }
//            }
//            //Log.v("CHKInternet :", forecastJsonStr);
//            /*
//            The below code extracts lat long and stores it in the var geoLat and geoLong
//             */
//            try {
//                JSONObject forecastJson = new JSONObject(forecastJsonStr);
//                JSONObject cityJson = forecastJson.getJSONObject("city");
//                JSONObject latLongJ = cityJson.getJSONObject("coord");
//                geoLat = latLongJ.getDouble("lat");
//                geoLong = latLongJ.getDouble("lon");
//                //long[] latLongArray = {lat,lonG};
//                //Log.v("CHK-MAP", Arrays.toString(latLongArray));
//            } catch (JSONException j) {
//                Log.e("CHK-JSON-ISSUE", "json-object", j);
//            }
//
//
//
//            try {
//                return getWeatherDataFromJson(forecastJsonStr, Integer.parseInt(numOfDays));
//            } catch (JSONException e) {
//                Log.e("CHK-JSON-ARRAY", "Error closing stream", e);
//            }
//            return null;
//
//        }
//
//        @Override
//        protected void onPostExecute(String[] strings) {
//            super.onPostExecute(strings);
//            // clear previous values
//            mForecastAdapter.clear();
//
//            // populate with fresh values
//            for (String s : strings)
//                mForecastAdapter.add(s);
//        }
//
//
// }

}