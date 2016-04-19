
package com.example.georg.mainsoftweather.pojo;

import com.google.gson.annotations.Expose;

public class Sys {

    @Expose
    public Integer type;
    @Expose
    public Integer id;
    @Expose
    public Double message;
    @Expose
    public String country;
    @Expose
    public Integer sunrise;
    @Expose
    public Integer sunset;

}
