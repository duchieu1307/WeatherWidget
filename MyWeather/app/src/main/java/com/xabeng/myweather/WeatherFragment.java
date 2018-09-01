package com.xabeng.myweather;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by sev_user on 8/29/2018.
 */

public class WeatherFragment extends Fragment implements ChangeCityCallBack {

    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;

    Handler handler;

    private Utils utils;

    @Override
    public void onChangeCityCallback() {
        updateViews();
    }

    public WeatherFragment() {
        utils = new Utils(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        Utils.changeCity(Utils.mCityPreference.getCity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);

        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "weatherfont.ttf");
        weatherIcon.setTypeface(weatherFont);
        return rootView;
    }
    public void updateViews () {
        cityField.setText(Utils.mCityPreference.getCityField());
        updatedField.setText(Utils.mCityPreference.getUpdatedField());
        detailsField.setText(Utils.mCityPreference.getDetailsField());
        currentTemperatureField.setText(Utils.mCityPreference.getCurrentTemperatureField());
        weatherIcon.setText(Utils.mCityPreference.getWeatherIcon());
    }
}