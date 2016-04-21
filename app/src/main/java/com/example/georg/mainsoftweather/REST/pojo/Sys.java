
package com.example.georg.mainsoftweather.rest.pojo;

import com.google.gson.annotations.Expose;

public class Sys {

    @Expose
    private Integer type;
    @Expose
    private Integer id;
    @Expose
    private Double message;
    @Expose
    private String country;
    @Expose
    private Integer sunrise;
    @Expose
    private Integer sunset;

    public String getCountry() {
        return country;
    }
}
