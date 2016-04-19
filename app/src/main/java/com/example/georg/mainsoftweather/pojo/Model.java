
package com.example.georg.mainsoftweather.pojo;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Model {

    @Expose
    public Coord coord;
    @Expose
    public List<Weather> weather = new ArrayList<Weather>();
    @Expose
    public String base;
    @Expose
    public Main main;
    @Expose
    public Wind wind;
    @Expose
    public Clouds clouds;
    @Expose
    public Integer dt;
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
}
