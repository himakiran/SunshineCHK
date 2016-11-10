package com.example.android.sunshine.app;

/**
 * Created by userhk on 07/11/16.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.android.sunshine.app.R.layout.fragment_main;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

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
        dailyWeatherUpdate.add("Today - Sunny - 88/76");
        dailyWeatherUpdate.add("Tommorow - Foggy - 78/75");
        dailyWeatherUpdate.add("Wed - Sunny - 89/75");
        dailyWeatherUpdate.add("Thurs - Rainy - 66/54");
        dailyWeatherUpdate.add("Fri - Clear - 83/76");
        dailyWeatherUpdate.add("Sat - Cloudy - 81/66");
        dailyWeatherUpdate.add("Sunday - Sunny - 88/76");

            /*
             the below code connects the adapter to the string array and ensures the values are

            correctly filled in list_item_forecast_textview which is part of list_item_forecast layout

            */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, dailyWeatherUpdate);

            /*
            the code below loads the fragment_main.xml layout which has the id listview_forecast
            and connects the adapter to it...
            */
        ListView listview = (ListView) rootView.findViewById(R.id.listview_forecast);
        listview.setAdapter(adapter);


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

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // to fetch data from external source
    public class FetchWeatherTask extends AsyncTask<String, String, String> {

        public FetchWeatherTask() {

        }

        @Override
        public String doInBackground(String... params) {
            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
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
            return forecastJsonStr;
        }

    }

}