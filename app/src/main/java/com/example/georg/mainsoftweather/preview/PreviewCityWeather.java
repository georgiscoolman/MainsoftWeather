package com.example.georg.mainsoftweather.preview;

import com.example.georg.mainsoftweather.orm.HelperFactory;
import com.example.georg.mainsoftweather.orm.entitys.City;
import com.example.georg.mainsoftweather.orm.entitys.MyWeather;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Georg on 22.04.2016.
 */
public class PreviewCityWeather {

    private City city;
    private MyWeather lastWeather;

    public PreviewCityWeather(City city) {
        this.city = city;

        try {
            Dao<MyWeather, String> myWeatherDao = HelperFactory.getHelper().getDao(MyWeather.class);
            QueryBuilder<MyWeather, String> queryBuilder = myWeatherDao.queryBuilder();
            queryBuilder.where().eq(MyWeather.CITY_ID, city.getId());
            queryBuilder.orderBy(MyWeather.DATE,false);
            queryBuilder.limit(1L);

            PreparedQuery<MyWeather> preparedQuery = queryBuilder.prepare();
            lastWeather = myWeatherDao.queryForFirst(preparedQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<PreviewCityWeather> fromArray(List<City> cities){
        ArrayList<PreviewCityWeather> res = new ArrayList<>();

        for (City city : cities) {
            res.add(new PreviewCityWeather(city));
        }

        return res;
    }

    public City getCity() {
        return city;
    }

    public MyWeather getLastWeather() {
        return lastWeather;
    }
}
