package com.example.georg.mainsoftweather.orm;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.georg.mainsoftweather.orm.HelperFactory;
import com.example.georg.mainsoftweather.orm.entitys.City;
import com.example.georg.mainsoftweather.preview.PreviewCityWeather;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Georg on 25.04.2016.
 */
public class PreviewCityWeatherLoader  extends AsyncTaskLoader<List<PreviewCityWeather>> {

    public final String TAG = getClass().getSimpleName();

    public PreviewCityWeatherLoader(Context context) {
        super(context);
    }

    @Override
    public List<PreviewCityWeather> loadInBackground() {
        Log.d(TAG, "loadInBackground");

        List<City> cities = new ArrayList<>();

        try {
            cities = DaoFactory.getInstance().getDao(City.class).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return PreviewCityWeather.fromArray(cities);
    }

    @Override
    public void forceLoad() {
        Log.d(TAG, "forceLoad");
        super.forceLoad();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.d(TAG, "onStartLoading");
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        Log.d(TAG, "onStopLoading");
    }

    @Override
    public void deliverResult(List<PreviewCityWeather> data) {
        super.deliverResult(data);
        Log.d(TAG, "deliverResult");
    }
}
