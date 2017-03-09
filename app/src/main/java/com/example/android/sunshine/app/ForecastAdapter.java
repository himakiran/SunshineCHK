package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(mContext,high, isMetric) + "/" + Utility.formatTemperature(mContext,low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {

        /*
            We use the array constants defined in ForecastFragment to get the column indices.
         */
        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }


    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        // TODO: Determine layoutId from viewType

        if(viewType == VIEW_TYPE_TODAY)
            layoutId = R.layout.list_item_forecast_today;
        else if (viewType == VIEW_TYPE_FUTURE_DAY)
            layoutId = R.layout.list_item_forecast;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder vh = new ViewHolder(view);
        view.setTag(vh);
        return view;



    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        boolean isMetric = Utility.isMetric(context);

        ViewHolder vh = (ViewHolder) view.getTag();

        int viewType = getItemViewType(cursor.getPosition());

        if (viewType == VIEW_TYPE_TODAY) {

            switch (cursor.getString(ForecastFragment.COL_WEATHER_DESC)) {
                case "Clear":
                    vh.iconView.setImageResource(R.drawable.art_clear);
                    break;
                case "Rain":
                    vh.iconView.setImageResource(R.drawable.art_rain);
                    break;
                case "Clouds":
                    vh.iconView.setImageResource(R.drawable.art_clouds);
                    break;
                case "Fog":
                    vh.iconView.setImageResource(R.drawable.art_fog);
                    break;
                case "Light_clouds":
                    vh.iconView.setImageResource(R.drawable.art_light_clouds);
                    break;
                case "Light_rain":
                    vh.iconView.setImageResource(R.drawable.art_light_rain);
                    break;
                case "Snow":
                    vh.iconView.setImageResource(R.drawable.art_snow);
                    break;
                case "Storm":
                    vh.iconView.setImageResource(R.drawable.art_storm);
                    break;
                default:
                    vh.iconView.setImageResource(R.drawable.ic_logo);
                    break;
            }
        } else {
            switch (cursor.getString(ForecastFragment.COL_WEATHER_DESC)) {
                case "Clear":
                    vh.iconView.setImageResource(R.drawable.ic_clear);
                    break;
                case "Rain":
                    vh.iconView.setImageResource(R.drawable.ic_rain);
                    break;
                case "Clouds":
                    vh.iconView.setImageResource(R.drawable.ic_cloudy);
                    break;
                case "Fog":
                    vh.iconView.setImageResource(R.drawable.ic_fog);
                    break;
                case "Light_clouds":
                    vh.iconView.setImageResource(R.drawable.ic_light_clouds);
                    break;
                case "Light_rain":
                    vh.iconView.setImageResource(R.drawable.ic_light_rain);
                    break;
                case "Snow":
                    vh.iconView.setImageResource(R.drawable.ic_snow);
                    break;
                case "Storm":
                    vh.iconView.setImageResource(R.drawable.ic_storm);
                    break;
                default:
                    vh.iconView.setImageResource(R.drawable.ic_logo);
                    break;
            }

        }



        long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        vh.dateView.setText(Utility.getFriendlyDayString(context,dateInMillis));

        String desc = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        vh.descriptionView.setText(desc);

        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);

        vh.highTempView.setText(Utility.formatTemperature(context,high,isMetric));

        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        vh.lowTempView.setText(Utility.formatTemperature(context,low,isMetric));

    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }
    /**
     * Cache of the children views for a forecast list item.
     */
    public  static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}

