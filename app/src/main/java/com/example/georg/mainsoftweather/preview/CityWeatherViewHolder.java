package com.example.georg.mainsoftweather.preview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.georg.mainsoftweather.R;
import com.example.georg.mainsoftweather.orm.entitys.City;
import com.example.georg.mainsoftweather.orm.entitys.MyWeather;

/**
 * Created by Georg on 22.04.2016.
 */
public class CityWeatherViewHolder extends RecyclerView.ViewHolder {


    TextView cityCountry;
    TextView temp;
    TextView tempMin;
    TextView tempMax;
    TextView description;
    TextView pressure;
    TextView humidity;
    TextView wind;
    TextView date;

    public CityWeatherViewHolder(View itemView) {
        super(itemView);

        cityCountry = (TextView) itemView.findViewById(R.id.city_country);
        temp = (TextView) itemView.findViewById(R.id.temp);
        tempMin = (TextView) itemView.findViewById(R.id.temp_min);
        tempMax = (TextView) itemView.findViewById(R.id.tempMax);
        description = (TextView) itemView.findViewById(R.id.descr);
        pressure = (TextView) itemView.findViewById(R.id.pressure);
        humidity = (TextView) itemView.findViewById(R.id.humidity);
        wind = (TextView) itemView.findViewById(R.id.wind);
        date = (TextView) itemView.findViewById(R.id.date);

    }

    public void bind(PreviewCityWeather item) {
        City city = item.getCity();
        MyWeather weather = item.getLastWeather();

        if (city != null){
            cityCountry.setVisibility(View.VISIBLE);
            cityCountry.setText(city.getName() + ", " + city.getCountry());
        }
        else {
            cityCountry.setVisibility(View.GONE);
        }

        if (weather != null){
            Double tmp = weather.getTemp();
            if (tmp!=null) {
                temp.setVisibility(View.VISIBLE);
                temp.setText(String.valueOf(Math.round(tmp)));
            }else {
                temp.setVisibility(View.GONE);
            }

            Double tmpMax = weather.getTemp();
            if (tmpMax!=null) {
                tempMax.setVisibility(View.VISIBLE);
                tempMax.setText(String.valueOf(Math.round(tmpMax)));
            }else {
                tempMax.setVisibility(View.GONE);
            }

            Double tmpMin = weather.getTemp();
            if (tmpMin!=null) {
                tempMin.setVisibility(View.VISIBLE);
                tempMin.setText(String.valueOf(Math.round(tmpMin)));
            }else {
                tempMin.setVisibility(View.GONE);
            }

            description.setText(weather.getDescription());

            Double prs = weather.getPressure();
            if (prs!=null) {
                pressure.setVisibility(View.VISIBLE);
                pressure.setText("pressure " + String.valueOf(Math.round(prs)));
            }else {
                pressure.setVisibility(View.GONE);
            }

            humidity.setText("humidity " + String.valueOf(weather.getHumidity()));

            Double wnd = weather.getWindSpeed();
            if (wnd!=null) {
                wind.setVisibility(View.VISIBLE);
                wind.setText("wind " + String.valueOf(Math.round(wnd)));
            }else {
                wind.setVisibility(View.GONE);
            }

            date.setText(weather.getDateReadable());
        }
    }

}
