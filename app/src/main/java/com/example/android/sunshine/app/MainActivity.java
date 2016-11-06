package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import static com.example.android.sunshine.app.R.layout.fragment_main;


public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_main is dummy layout
        setContentView(R.layout.activity_main);
        //The code below calls the PlaceHolderFragment code..
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

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
    }
}