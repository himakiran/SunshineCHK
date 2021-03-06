/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class DetailActivity extends ActionBarActivity {
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent in = getIntent();
        Uri contenturi = in.getData();
        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.DETAIL_URI, contenturi);
        DetailFragment df = new DetailFragment();
        df.setArguments(args);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, df)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
       /*
       The below code implements the shareActionProvider function.
       It uses setShareIntent() and createShareItent() in addition to below code
        */
        MenuItem item = menu.findItem(R.id.detail_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setShareIntent(createShareIntent());
        //Return true to display menu
        return true;
    }


    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        Intent intent = this.getIntent();
        String weather = intent.getStringExtra(Intent.EXTRA_TEXT);
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                weather + "#SunshineApp");
        return shareIntent;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.detail_action_refresh) {
            Intent intent = new Intent(getBaseContext(), ForecastFragment.class);
            intent.putExtra("menu", "onOptionsItemSelected");//goes to previous Intent
            startActivity(intent);

            return true;
        }
        /*
        The code below calls the setting activity class
         */
        if (id == R.id.detail_settings) {
            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            return true;
        }
        if (id == R.id.detail_see_map) {
            Intent intent1 = this.getIntent();
            String geo = intent1.getStringExtra("GEO-TEXT");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri geolocation = Uri.parse(geo);
            Log.v("CHK-GEO-URI", geo);
            //intent.setData takes an Uri and hence above Uri.parse on string is done
            intent.setData(geolocation);
            if (intent.resolveActivity(this.getBaseContext().getPackageManager()) != null) {
                startActivity(intent);
            }


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    }

