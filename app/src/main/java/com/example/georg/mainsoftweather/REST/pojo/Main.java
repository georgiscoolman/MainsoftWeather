
package com.example.georg.mainsoftweather.rest.pojo;

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

    public Double getTemp() {
        return temp;
    }

    public Double getTempMin() {
        return tempMin;
    }

    public Double getTempMax() {
        return tempMax;
    }


}
