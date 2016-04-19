package com.example.georg.mainsoftweather;

import com.example.georg.mainsoftweather.pojo.Model;

import retrofit.Call;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.GET;

/**
 * Created by Georg on 19.04.2016.
 */
public interface RestApi {

    String URL = "http://api.openweathermap.org";
    String API_KEY = "http://api.openweathermap.org";

    @GET("/data/2.5/weather?appid=" + API_KEY)
    Call<Model> getWheatherReport(@Query("q") String cityName);
}