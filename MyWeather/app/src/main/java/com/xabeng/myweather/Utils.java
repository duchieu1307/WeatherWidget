package com.xabeng.myweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sev_user on 8/30/2018.
 */

interface ChangeCityCallBack {
    void onChangeCityCallback();
}

public class Utils {

    public static String CITY = "city";
    public static String LAST_UPDATE = "last_update";
    public static String DETAIL = "detail";
    public static String TEMP = "temp";
    public static String ICON = "icon";

    static Handler handler;
    static CityPreference mCityPreference;
    static Context mContext;

    static  ChangeCityCallBack callBack;
    public Utils (ChangeCityCallBack cb) {
        callBack = cb;
    }




    public static void initCity(Context context) {
        mCityPreference = new CityPreference(context);
        mContext = context;
        handler = new Handler();
    }

    public static void initRefresh () {
        AlarmManager alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, RefreshReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 20, alarmIntent);
    }

    public static void changeCity (final String city) {
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(mContext, city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(mContext,
                                    mContext.getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // Change city in preference
                    mCityPreference.setCity(city);
                    //
                    handler.post(new Runnable(){
                        public void run(){
                            Utils.renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    public static void renderWeather(JSONObject json){
        try {
            mCityPreference.setCityField(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            mCityPreference.setDetailsField(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            mCityPreference.setCurrentTemperatureField(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            mCityPreference.setUpdatedField("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);
        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    public static void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = mContext.getString(R.string.weather_sunny);
            } else {
                icon = mContext.getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = mContext.getString(R.string.weather_thunder);
                    break;
                case 3 : icon = mContext.getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = mContext.getString(R.string.weather_foggy);
                    break;
                case 8 : icon = mContext.getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = mContext.getString(R.string.weather_snowy);
                    break;
                case 5 : icon = mContext.getString(R.string.weather_rainy);
                    break;
            }
        }
        mCityPreference.setWeatherIcon(icon);


        updateWidget();
        callBack.onChangeCityCallback();
    }

    public static void updateWidget() {
        int ids[] = AppWidgetManager.getInstance(mContext)
                .getAppWidgetIds(new ComponentName(mContext, MyWeatherWidget.class));

        Intent intent = new Intent(mContext, MyWeatherWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        intent.putExtra(ICON, mCityPreference.getWeatherIcon());
        intent.putExtra(TEMP, mCityPreference.getCurrentTemperatureField());
        intent.putExtra(CITY, mCityPreference.cityField);
        intent.putExtra(LAST_UPDATE, mCityPreference.getUpdatedField());
        intent.putExtra(DETAIL, mCityPreference.getDetailsField());
        mContext.sendBroadcast(intent);
    }


}
