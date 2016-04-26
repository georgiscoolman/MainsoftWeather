package com.example.georg.mainsoftweather.rest;

import com.example.georg.mainsoftweather.rest.pojo.Model;

import retrofit.Call;
import retrofit.http.Query;
import retrofit.http.GET;
import retrofit2.http.Path;

/**
 * Created by Georg on 19.04.2016.
 */
public interface RestApi {

    String URL = "http://api.openweathermap.org";
    String API_KEY = "e96b626a0cb231086ffea9d1f23488bd";
    String METRIC = "&units=metric";

    @GET("/data/2.5/weather?appid=" + API_KEY + METRIC)
    Call<Model> getWheatherReportByCityName(@Query("q") String cityName);

    @GET("/data/2.5/weather?appid=" + API_KEY + METRIC)
    Call<Model> getWheatherReportByCityId(@Query("id") int id);

}