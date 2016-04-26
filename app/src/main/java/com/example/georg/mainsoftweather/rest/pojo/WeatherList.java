package com.example.georg.mainsoftweather.rest.pojo;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Georg on 26.04.2016.
 */
public class WeatherList {

    @Expose
    public Integer cnt;

    @Expose
    public List<Model> list = new ArrayList<>();

    public Integer getCnt() {
        return cnt;
    }

    public List<Model> getList() {
        return list;
    }
}
