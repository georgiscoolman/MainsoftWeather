package com.example.georg.mainsoftweather.preview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.georg.mainsoftweather.R;
import com.example.georg.mainsoftweather.orm.entitys.City;
import com.example.georg.mainsoftweather.orm.entitys.MyWeather;
import com.example.georg.mainsoftweather.rest.RestApi;
import com.squareup.picasso.Picasso;

/**
 * Created by Georg on 22.04.2016.
 */
public class CityWeatherViewHolder extends RecyclerView.ViewHolder {

    private static final String WEB_IMAGES_FORMAT = "%s/img/w/%s.png";

    TextView cityCountry;
    TextView temp;
    ImageView icon;
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
        icon = (ImageView) itemView.findViewById(R.id.icon);
        tempMin = (TextView) itemView.findViewById(R.id.temp_min);
        tempMax = (TextView) itemView.findViewById(R.id.temp_max);
        description = (TextView) itemView.findViewById(R.id.descr);
        pressure = (TextView) itemView.findViewById(R.id.pressure);
        humidity = (TextView) itemView.findViewById(R.id.humidity);
        wind = (TextView) itemView.findViewById(R.id.wind);
        date = (TextView) itemView.findViewById(R.id.date);

    }

    public void bind(PreviewCityWeather item) {
        City city = item.getCity();
        MyWeather weather = item.getLastWeather();

        Context context = temp.getContext();

        if (city != null){
            cityCountry.setVisibility(View.VISIBLE);
            cityCountry.setText(String.format(context.getString(R.string.city_country_format), city.getName(), city.getCountry()));
        }
        else {
            cityCountry.setVisibility(View.GONE);
        }

        if (weather != null){
            Double tmp = weather.getTemp();
            if (tmp!=null) {
                temp.setVisibility(View.VISIBLE);
                temp.setText(String.format(context.getString(R.string.temp_format), String.valueOf(Math.round(tmp))));
            }else {
                temp.setVisibility(View.GONE);
            }

            String url = String.format(WEB_IMAGES_FORMAT, RestApi.URL, weather.getIcon());
            Log.d("img", url);
            Picasso.with(temp.getContext()).load(url).into(icon);


            Double tmpMax = weather.getTemp();
            if (tmpMax!=null) {
                tempMax.setVisibility(View.VISIBLE);
                tempMax.setText(String.format(context.getString(R.string.max_temp_format), String.valueOf(Math.round(tmpMax))));
            }else {
                tempMax.setVisibility(View.GONE);
            }

            Double tmpMin = weather.getTemp();
            if (tmpMin!=null) {
                tempMin.setVisibility(View.VISIBLE);
                tempMin.setText(String.format(context.getString(R.string.min_temp_format), String.valueOf(Math.round(tmpMin))));
            }else {
                tempMin.setVisibility(View.GONE);
            }

            description.setText(weather.getDescription());

            Double prs = weather.getPressure();
            if (prs!=null) {
                pressure.setVisibility(View.VISIBLE);
                pressure.setText(String.format("pressure %s", String.valueOf(Math.round(prs))));
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
