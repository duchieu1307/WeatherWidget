package com.xabeng.myweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class MyWeatherWidget extends AppWidgetProvider {

    private static final String REFRESH = "refresh";
    private static final String DEF_CITY = "ha noi";

    String cityField;
    String updatedField;
    String detailsField;
    String currentTemperatureField;
    String weatherIcon;

    boolean shouldShowProgress = false;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_weather_widget);
        if (shouldShowProgress) {
            views.setViewVisibility(R.id.refresh_image, View.GONE);
            views.setViewVisibility(R.id.refresh_progress, View.VISIBLE);
        } else {
            if (weatherIcon != null) {
                views.setImageViewBitmap(R.id.icon, buildIcon(context, weatherIcon));
                views.setTextViewText(R.id.temp, currentTemperatureField);
                views.setTextViewText(R.id.city, cityField);
                views.setTextViewText(R.id.last_update, updatedField);
            }
            //Hide progress
            shouldShowProgress = false;
            views.setViewVisibility(R.id.refresh_image, View.VISIBLE);
            views.setViewVisibility(R.id.refresh_progress, View.GONE);
        }
        // Launch app
        Intent intent = new Intent(context, WeatherActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // Get the layout for the App Widget and attach an on-click listener to the button
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);


        // Refresh
        Intent refreshIntent = new Intent(context, MyWeatherWidget.class);

        refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
        refreshIntent.putExtra(REFRESH, REFRESH);

        PendingIntent pendingRefreshIntent = PendingIntent.getBroadcast(context,
                0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.refresh, pendingRefreshIntent);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(REFRESH, "intent action: " + intent.getAction());
        if (intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE_OPTIONS")) {
            if (Utils.mCityPreference != null) {
                Utils.changeCity(Utils.mCityPreference.getCity());
            } else {
                appNotLunchedCase(context);
            }
        }
        if (intent.hasExtra(REFRESH)) {
            shouldShowProgress = true;
            int[] ids = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(new ComponentName(context, MyWeatherWidget.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
            if (Utils.mCityPreference != null) {
                Utils.changeCity(Utils.mCityPreference.getCity());
            } else {
                appNotLunchedCase(context);
            }
        } else {
            shouldShowProgress = false;
            cityField = intent.getStringExtra(Utils.CITY);
            updatedField = intent.getStringExtra(Utils.LAST_UPDATE);
            currentTemperatureField = intent.getStringExtra(Utils.TEMP);
            weatherIcon = intent.getStringExtra(Utils.ICON);
            detailsField = intent.getStringExtra(Utils.DETAIL);
        }
        super.onReceive(context, intent);
    }

    public Bitmap buildIcon(Context context, String icon)
    {
        Bitmap myBitmap = Bitmap.createBitmap(300, 280, Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        Typeface icontf = Typeface.createFromAsset(context.getAssets(),"weatherfont.ttf");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(icontf);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(180);
        paint.setTextAlign(Paint.Align.CENTER);
        myCanvas.drawText(icon, 150, 140, paint);
        return myBitmap;
    }

    public void appNotLunchedCase (Context context){
        Utils.initCity(context);
        Utils.initRefresh();
        Utils.changeCity(DEF_CITY);
        Toast.makeText(context, "Please start app for initialize", Toast.LENGTH_LONG).show();
    }
}