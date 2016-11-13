package com.example.android.sunshine.app;

/**
 * Created by userhk on 07/11/16.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.example.android.sunshine.app.R.id.list_item_forecast_textview;
import static com.example.android.sunshine.app.R.layout.fragment_main;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    // mForecastAdapter has been made a global variable so that it can be accessed from within FetchWeatherTask
    private ArrayAdapter<String> mForecastAdapter;
    public ForecastFragment() {
    }

    // to create the object
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this code makes sure menu gets displayed and further allows onCreateOptionsMenu to function
        setHasOptionsMenu(true);

    }

    //to create the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // the below line calls the layout defined in fragment_main
        View rootView = inflater.inflate(fragment_main, container, false);

        // this is the array values that get filled in the layout
        ArrayList<String> dailyWeatherUpdate = new ArrayList<String>();

            /*
             the below code connects the adapter to the string array and ensures the values are

            correctly filled in list_item_forecast_textview which is part of list_item_forecast layout

            */
        mForecastAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.list_item_forecast, list_item_forecast_textview, dailyWeatherUpdate);
        FetchWeatherTask fetch = new FetchWeatherTask();
        //fetch takes one parameter that is a string
        // also as fetch executes the onPostExecute overridden function ensures that mForecastAdapter gets populated
        fetch.execute("94303");
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
                String forecast = mForecastAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });


        return rootView;

    }

    // to create the menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // this code inflates the menu we detailed in forecastfragment.xml
        inflater.inflate(R.menu.forecastfragment, menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //code to handle each menu item
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchWeatherTask fetch = new FetchWeatherTask();
            //fetch takes one parameter that is a string
            // also as fetch executes the onPostExecute overridden function ensures that mForecastAdapter gets populated
            fetch.execute("94303");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // to fetch data from external source
    public class FetchWeatherTask extends AsyncTask<String, String, String[]> {

        public FetchWeatherTask() {

        }

        /* The date/time conversion code is going to be moved outside the asynctask later,
 * so for convenience we're breaking it out into its own method now.
 */
        private String getReadableDateString(long time) {
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for (int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay + i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
                Log.v("CHK-TAG", "Forecast entry: " + s);
            }
            return resultStrs;

        }


        @Override
        public String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // here we define that doInBackground takes one string parameter.
            String zipCode = params[0];

// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String numOfDays = "7";

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q="+zipCode+"&mode=json&units=metric&cnt=7&appid=43015412ed029d0518c54689fa3c3a37");
                Uri.Builder weatherURL = new Uri.Builder();
                String wUrl = "api.openweathermap.org";
                weatherURL.scheme("http")
                        .authority(wUrl)
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter("q", zipCode)
                        .appendQueryParameter("mode", "json")
                        .appendQueryParameter("units", "metric")
                        .appendQueryParameter("cnt", numOfDays)
                        .appendQueryParameter("appid", "43015412ed029d0518c54689fa3c3a37")
                        .fragment("section-name");

                // Create the request to OpenWeatherMap, and open the connection
                URL url = new URL(weatherURL.build().toString());
                //Log.v("CHK-URL",url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            //Log.v("CHKInternet :", forecastJsonStr);

            try {
                return getWeatherDataFromJson(forecastJsonStr, Integer.parseInt(numOfDays));
            } catch (JSONException e) {
                Log.e("CHK-JSON-ARRAY", "Error closing stream", e);
            }
            return null;

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            // clear previous values
            mForecastAdapter.clear();

            // populate with fresh values
            for (String s : strings)
                mForecastAdapter.add(s);
        }


    }

}