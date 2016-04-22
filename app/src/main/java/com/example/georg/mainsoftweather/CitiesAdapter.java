package com.example.georg.mainsoftweather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.georg.mainsoftweather.orm.entitys.City;
import com.example.georg.mainsoftweather.preview.CityWeatherViewHolder;
import com.example.georg.mainsoftweather.preview.PreviewCityWeather;

import java.util.List;

/**
 * Created by Georg on 22.04.2016.
 */
public class CitiesAdapter extends RecyclerView.Adapter<CityWeatherViewHolder> {

    private  List<PreviewCityWeather> cityWeathers;
    private final LayoutInflater mInflater;

    public CitiesAdapter(Context context, List<PreviewCityWeather> cityWeathers) {
        this.cityWeathers = cityWeathers;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public CityWeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.weather, parent, false);
        return new CityWeatherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CityWeatherViewHolder holder, int position) {
        holder.bind(cityWeathers.get(position));
    }

    @Override
    public int getItemCount() {
        return cityWeathers.size();
    }

    @Override
    public long getItemId(int position) {

        PreviewCityWeather item = cityWeathers.get(position);

        City city = item.getCity();

        if (city!=null){
            return city.getId();
        }
        else {
            return item.getLastWeather().getId();
        }
    }

    public void setCityWeathers(List<PreviewCityWeather> cityWeathers) {
        this.cityWeathers = cityWeathers;
        notifyDataSetChanged();
    }
}
