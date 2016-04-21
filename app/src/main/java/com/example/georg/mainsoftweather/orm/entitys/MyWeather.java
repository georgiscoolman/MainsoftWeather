package com.example.georg.mainsoftweather.orm.entitys;

import com.example.georg.mainsoftweather.rest.pojo.Main;
import com.example.georg.mainsoftweather.rest.pojo.Model;
import com.example.georg.mainsoftweather.rest.pojo.Weather;
import com.example.georg.mainsoftweather.rest.pojo.Wind;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Georg on 21.04.2016.
 */
@DatabaseTable(tableName = MyWeather.TABLE_NAME)
public class MyWeather implements BaseEntity{

    public SimpleDateFormat sdfmad = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static final String TABLE_NAME = "Weather";

    public static final String DESCRIPTION = "description";
    public static final String DATE = "date";
    public static final String TEMP = "temp";
    public static final String TEMP_MAX = "tempMax";
    public static final String TEMP_MIN = "tempMin";
    public static final String PRESSURE = "pressure";
    public static final String HUMIDITY = "humidity";
    public static final String WIND_SPEED = "windSpeed";

    public static final String CITY_ID = "city_id";

    @DatabaseField(columnName = ID, generatedId = true)
    private Long id;
    @DatabaseField(columnName = DATE)
    private Date date;
    @DatabaseField(columnName = DESCRIPTION)
    private String description;
    @DatabaseField(columnName = TEMP)
    private Double temp;
    @DatabaseField(columnName = TEMP_MAX)
    private Double tempMax;
    @DatabaseField(columnName = TEMP_MIN)
    private Double tempMin;
    @DatabaseField(columnName = PRESSURE)
    private Integer pressure;
    @DatabaseField(columnName = HUMIDITY)
    private Integer humidity;
    @DatabaseField(columnName = WIND_SPEED)
    private Double windSpeed;
    @DatabaseField(foreign = true, columnName = CITY_ID, foreignColumnName = ID)
    private City city;

    public MyWeather() {
    }

    public MyWeather(Model model, City city){

        if (model!= null){

            Main main = model.getMain();

            date = new Date (model.getDt()*1000L);

            if (main != null){
                temp = main.getTemp();
                tempMax = main.getTempMax();
                tempMin = main.getTempMin();
                pressure = main.getPressure();
                humidity = main.getHumidity();
            }

            Wind wind = model.getWind();

            if (wind!=null){
                windSpeed = wind.getSpeed();
            }

            Weather weather = model.getWeather().get(0);
            if (weather!=null){
                description = weather.getDescription();
            }

            if (city!=null){
                this.city = city;
            }

        }

    }

    @Override
    public String toString() {
        sdfmad.setTimeZone(TimeZone.getDefault());
        return "id " + id + " date " + sdfmad.format(date) + " temp " + temp + " tempMax " + tempMax + " tempMin " + tempMin + " pressure "
                + pressure + " humidity " + humidity + " windSpeed " + windSpeed + " description " + description;
    }

}
