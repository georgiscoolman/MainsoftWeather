package com.example.georg.mainsoftweather.orm.entitys;

import java.text.SimpleDateFormat;

/**
 * Created by Georg on 21.04.2016.
 */
public interface BaseEntity {
    String ID = "id";

    public SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");


}
