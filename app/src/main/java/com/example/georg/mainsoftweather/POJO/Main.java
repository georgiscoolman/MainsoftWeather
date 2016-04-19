
package com.example.georg.mainsoftweather.pojo;

import com.google.gson.annotations.Expose;

public class Main {

    @Expose
    public Double temp;
    @Expose
    public Integer pressure;
    @Expose
    public Integer humidity;
    @Expose
    public Double tempMin;
    @Expose
    public Double tempMax;

    public Integer getHumidity() {
        return humidity;
    }

    public Integer getPressure() {
        return pressure;
    }
}
