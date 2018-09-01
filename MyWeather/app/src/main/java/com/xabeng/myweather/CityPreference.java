package com.xabeng.myweather;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sev_user on 8/29/2018.
 */

public class CityPreference {
    private final String TAG = CityPreference.class.getCanonicalName();
    SharedPreferences prefs;

    String cityField;
    String updatedField;
    String detailsField;
    String currentTemperatureField;
    String weatherIcon;

    public String getCityField() {
        return cityField;
    }

    public void setCityField(String cityField) {
        this.cityField = cityField;
    }

    public String getUpdatedField() {
        return updatedField;
    }

    public void setUpdatedField(String updatedField) {
        this.updatedField = updatedField;
    }

    public String getDetailsField() {
        return detailsField;
    }

    public void setDetailsField(String detailsField) {
        this.detailsField = detailsField;
    }

    public String getCurrentTemperatureField() {
        return currentTemperatureField;
    }

    public void setCurrentTemperatureField(String currentTemperatureField) {
        this.currentTemperatureField = currentTemperatureField;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public CityPreference(Context context){
        prefs = context.getSharedPreferences(TAG, Activity.MODE_PRIVATE);
    }

    // If the user has not chosen a city yet, return
    // Ha Noi as the default city
    String getCity(){
        return prefs.getString("city", "ha noi");
    }

    void setCity(String city) {
        prefs.edit().putString("city", city).commit();
    }
}
