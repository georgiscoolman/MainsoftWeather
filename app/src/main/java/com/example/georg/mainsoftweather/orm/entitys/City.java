package com.example.georg.mainsoftweather.orm.entitys;

import com.example.georg.mainsoftweather.orm.DaoFactory;
import com.example.georg.mainsoftweather.rest.pojo.Model;
import com.example.georg.mainsoftweather.rest.pojo.Sys;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public Collection<MyWeather> getWeatherLog() {
        return weatherLog;
    }

    @Override
    public String toString() {
        return "id " + id + " name " + name + " country " + country;
    }

    public static String getAllCitiesId(){
        StringBuilder ids = new StringBuilder();

        Dao<City, Long> dao = null;

        try {
            dao = DaoFactory.getInstance().getDao(City.class);

            PreparedQuery<City> query =  dao.queryBuilder().selectColumns(ID).prepare();

            ArrayList<City> cities = (ArrayList<City>) dao.query(query);

            for (int i = 0; i < cities.size(); i++) {
                if (i!=0){
                    ids.append(',');
                }
                ids.append(String.valueOf(cities.get(i).getId()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ids.toString();
    }

    public static void removeCity(Long id){

        Dao<City, Long> dao = null;

        try {
            dao = DaoFactory.getInstance().getDao(City.class);
            dao.deleteById(id);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
