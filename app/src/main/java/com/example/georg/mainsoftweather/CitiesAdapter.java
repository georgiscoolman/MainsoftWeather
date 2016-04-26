package com.example.georg.mainsoftweather;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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
    private Context context;

    public CitiesAdapter(Context context, List<PreviewCityWeather> cityWeathers) {
        this.cityWeathers = cityWeathers;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public CityWeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.weather, parent, false);
        return new CityWeatherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CityWeatherViewHolder holder, int position) {
        final PreviewCityWeather item = cityWeathers.get(position);
        holder.bind(item);
        holder.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showRemoveCityDialog(item);
                return false;
            }
        });
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

    public void showRemoveCityDialog(PreviewCityWeather weather){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final City city = weather.getCity();
        builder.setTitle(context.getString(R.string.remove) + String.format(context.getString(R.string.city_country_format), city.getName(), city.getCountry()) )
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (context instanceof MainActivity){
                            ((MainActivity) context).removeCity(city.getId().intValue());
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
