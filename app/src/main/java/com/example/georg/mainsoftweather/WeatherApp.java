package com.example.georg.mainsoftweather;

import android.app.Application;

import com.example.georg.mainsoftweather.orm.HelperFactory;

/**
 * Created by Georg on 20.04.2016.
 */
public class WeatherApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HelperFactory.setHelper(getApplicationContext());
    }
    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();
        super.onTerminate();
    }
}
