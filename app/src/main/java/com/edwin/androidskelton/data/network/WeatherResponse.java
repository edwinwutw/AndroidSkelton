package com.edwin.androidskelton.data.network;

import android.support.annotation.NonNull;

import com.edwin.androidskelton.data.database.WeatherEntry;


/**
 * Weather response from the backend. Contains the weather forecasts.
 */
class WeatherResponse {

    @NonNull
    private final WeatherEntry[] mWeatherForecast;

    public WeatherResponse(@NonNull final WeatherEntry[] weatherForecast) {
        mWeatherForecast = weatherForecast;
    }

    public WeatherEntry[] getWeatherForecast() {
        return mWeatherForecast;
    }
}