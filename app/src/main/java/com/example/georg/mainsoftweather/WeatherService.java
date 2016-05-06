package com.example.georg.mainsoftweather;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.georg.mainsoftweather.orm.DaoFactory;
import com.example.georg.mainsoftweather.orm.entitys.City;
import com.example.georg.mainsoftweather.orm.entitys.MyWeather;
import com.example.georg.mainsoftweather.rest.RetrofitServiceFactory;
import com.example.georg.mainsoftweather.rest.pojo.Model;
import com.example.georg.mainsoftweather.rest.pojo.WeatherList;

import java.sql.SQLException;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class WeatherService extends IntentService {

    public static final String GET_BY_NAME = "get_by_name";
    public static final String GET_BY_ID = "get_by_id";
    public static final String UPDATE_ALL = "update_all";

    public static final String NAME = "name";
    public static final String ID = "id";

    public WeatherService() {
        super("WeatherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (GET_BY_NAME.equals(action)) {
                final String name = intent.getStringExtra(NAME);
                getCityByName(name);
            } else if (GET_BY_ID.equals(action)) {
                final Long id = intent.getLongExtra(ID,0);
                getCityById(id);
            } else if (UPDATE_ALL.equals(action)) {
                updateAll();
            }
        }
    }

    private void getCityByName(String name) {

        Call<Model> call = RetrofitServiceFactory.getInstance().getWheatherReportByCityName(name);

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Response<Model> response, Retrofit retrofit) {
                if (response != null)
                    saveWeather(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    private void getCityById(Long id) {

    }

    private void updateAll() {

        Call<WeatherList> call = RetrofitServiceFactory.getInstance().getWheatherReportBySeveralCityId(City.getAllCitiesId());

        call.enqueue(new Callback<WeatherList>() {
            @Override
            public void onResponse(Response<WeatherList> response, Retrofit retrofit) {
                WeatherList list = null;
                if (response != null){
                    list = response.body();
                }

                if (list!= null){
                    ArrayList<Model> models = (ArrayList<Model>) list.getList();
                    for (Model model : models) {
                        saveWeather(model);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    private void saveWeather(Model model) {

        City city = new City(model);
        try {
            DaoFactory.getInstance().getDao(City.class).createOrUpdate(city);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        MyWeather myWeather = new MyWeather(model, city);
        try {
            DaoFactory.getInstance().getDao(MyWeather.class).createOrUpdate(myWeather);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
