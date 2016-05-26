package com.example.georg.mainsoftweather;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
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

    public static final int FINISHED_FAIL = 0;
    public static final int FINISHED_SUCCESS = 1;

    public static final int TASK_GET_BY_NAME = 2;
    public static final int TASK_GET_BY_ID = 3;
    public static final int TASK_UPDATE_ALL = 4;

    public static final String PARAM_PINTENT = "param_intent";
    public static final String PARAM_TASK_CODE = "param_task_code";

    public static final String NAME = "name";
    public static final String ID = "id";

    public WeatherService() {
        super("WeatherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int taskCode = 0;

        PendingIntent pi = intent.getParcelableExtra(WeatherService.PARAM_PINTENT);

        if (intent != null) {

            taskCode = intent.getIntExtra(PARAM_TASK_CODE,5);

            switch (taskCode){
                case TASK_GET_BY_NAME:
                    final String name = intent.getStringExtra(NAME);
                    getCityByName(name,pi);
                    break;
                case TASK_GET_BY_ID:
                    final Long id = intent.getLongExtra(ID, 0);
                    getCityById(id,pi);
                    break;
                case TASK_UPDATE_ALL:
                    updateAll(pi);
                    break;
            }

        }

        Log.d("WeatherService", "onHandleIntent " + taskCode);
    }

    private void getCityByName(String name, final PendingIntent pi) {

        Call<Model> call = RetrofitServiceFactory.getInstance().getWheatherReportByCityName(name);

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Response<Model> response, Retrofit retrofit) {
                if (response != null) {
                    try {
                        saveWeather(response.body());
                        sendResult(true, pi);
                    } catch (SQLException e) {
                        sendResult(false, pi);
                    }
                }
                else {
                    sendResult(false, pi);
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    private void getCityById(Long id, PendingIntent pi) {

    }

    private void updateAll(final PendingIntent pi) {

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
                    try {
                        for (Model model : models) {
                            saveWeather(model);
                        }
                        sendResult(true, pi);
                    } catch (SQLException e) {
                        sendResult(false, pi);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                sendResult(false, pi);
            }
        });
    }

    private void saveWeather(Model model) throws SQLException {

        City city = new City(model);
        DaoFactory.getInstance().getDao(City.class).createOrUpdate(city);

        MyWeather myWeather = new MyWeather(model, city);
        DaoFactory.getInstance().getDao(MyWeather.class).createOrUpdate(myWeather);

    }

    private void sendResult(boolean success, PendingIntent pi){

        Intent responseIntent = new Intent();

        int successRes = FINISHED_FAIL;

        if (success){
            successRes = FINISHED_SUCCESS;
        }

        try {
            pi.send(WeatherService.this, successRes,responseIntent);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

    }
}
