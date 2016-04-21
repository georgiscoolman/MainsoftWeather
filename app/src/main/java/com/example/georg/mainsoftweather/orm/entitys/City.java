package com.example.georg.mainsoftweather.orm.entitys;

import com.example.georg.mainsoftweather.rest.pojo.Model;
import com.example.georg.mainsoftweather.rest.pojo.Sys;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

/**
 * Created by Georg on 21.04.2016.
 */

@DatabaseTable(tableName = City.TABLE_NAME)
public class City implements BaseEntity{

    public static final String TABLE_NAME = "City";

    public static final String NAME = "name";
    public static final String COUNTRY = "country";
    public static final String WEATHER_LOG = "weather_log";


    @DatabaseField(id = true, columnName = ID)
    private Long id;
    @DatabaseField(columnName = NAME)
    private String name;
    @DatabaseField(columnName = COUNTRY)
    private String country;
    @ForeignCollectionField(columnName = WEATHER_LOG)
    private Collection<MyWeather> weatherLog;

    public City() {
    }

    public City(Model model){
        if (model!=null) {
            this.id = model.getId().longValue();
            this.name = model.getName();

            Sys sys = model.getSys();

            if (sys!=null){
                this.country = sys.getCountry();
            }
        }
    }

    @Override
    public String toString() {
        return "id " + id + " name " + name + " country " + country;
    }
}
