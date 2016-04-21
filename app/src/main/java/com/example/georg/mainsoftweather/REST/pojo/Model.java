
package com.example.georg.mainsoftweather.rest.pojo;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Model {

    @Expose
    public List<Weather> weather = new ArrayList<>();
    @Expose
    public String base;
    @Expose
    public Main main;
    @Expose
    public Wind wind;
    @Expose
    public Long dt;
    @Expose
    public Sys sys;
    @Expose
    public Integer id;
    @Expose
    public String name;
    @Expose
    public Integer cod;

    public String getName() {
        return name;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    public Integer getId() {
        return id;
    }

    public Sys getSys() {
        return sys;
    }

    public Wind getWind() {
        return wind;
    }

    public Long getDt() {
        return dt;
    }
}
