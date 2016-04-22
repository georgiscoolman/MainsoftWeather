package com.example.georg.mainsoftweather;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.georg.mainsoftweather.preview.PreviewCityWeather;
import com.example.georg.mainsoftweather.rest.RestApi;
import com.example.georg.mainsoftweather.orm.HelperFactory;
import com.example.georg.mainsoftweather.rest.pojo.Model;
import com.example.georg.mainsoftweather.orm.entitys.City;
import com.example.georg.mainsoftweather.orm.entitys.MyWeather;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private ProgressDialog searchLocationDialog;
    private LocationManager locationManager;

    private SharedPreferences mSettings;
    public static final int MY_PERMISSION_REQUEST = 1;
    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_FIRST_LAUNCH = "isFirstLaunch";
    public static final int SEARCH_GPS_MIN_INTERVAL = 10000;
    public static final int SEARCH_GPS_TIME = 60000;
    public static final int SEARCH_GPS_DISTANCE = 10;
    public static final String GPS_LOG = "gps";

    // Key e96b626a0cb231086ffea9d1f23488bd

    private RecyclerView mRecyclerView;
    private CitiesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_cities);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        refreshList();

    }

    private void startSearchLocation() {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SEARCH_GPS_MIN_INTERVAL, SEARCH_GPS_DISTANCE, locationListener);

            Handler h = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    stopSearchLocation(true);
                }
            };
            h.postDelayed(r,SEARCH_GPS_TIME);

            searchLocationDialog = new ProgressDialog(this);
            searchLocationDialog.setTitle("Searching location");
            searchLocationDialog.setMessage("Please wait");
            searchLocationDialog.setCancelable(false);
            searchLocationDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    stopSearchLocation(false);
                }
            });
            searchLocationDialog.show();
        }
        else {
            showTurnOnGPSMessage();
        }

    }

    private void stopSearchLocation(boolean isTimeEnd) {
        if (locationManager != null)
        locationManager.removeUpdates(locationListener);

        if (searchLocationDialog != null) {
            if (searchLocationDialog.isShowing()) {
                searchLocationDialog.dismiss();

                if (isTimeEnd){
                    showFailGetGPSDialog();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mSettings.contains(APP_PREFERENCES_FIRST_LAUNCH)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startSearchLocation();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        showExplainGPSMessage();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST);
                    }
                }
            } else {
                startSearchLocation();
            }

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(APP_PREFERENCES_FIRST_LAUNCH, true);
            editor.apply();
       }

        refreshList();

       // getWeather("Minsk");


    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSearchLocation(false);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(GPS_LOG, "onLocationChanged: " + location.toString());
            String cityName = getCityName(location);
            if (cityName != null) {
                getWeather(cityName);
                stopSearchLocation(false);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(GPS_LOG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(GPS_LOG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(GPS_LOG, "Status: " + String.valueOf(status));
        }
    };

    private String getCityName(Location location) {

        String cityName = null;

        if (location != null)
        Log.d(GPS_LOG, formatLocation(location));

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses!=null){
            if (addresses.size() > 0){
                Address address = addresses.get(0);
                cityName = address.getLocality();
                if (cityName!= null)
                    Log.d(GPS_LOG, "City name " + cityName);
                else
                    Log.d(GPS_LOG, "City name is unknown");
            }
        }

        return cityName;
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(location.getTime()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSearchLocation();
                } else {
                    showExplainGPSMessage();
                }
                return;
            }
        }
    }

    private void showExplainGPSMessage(){
        Toast.makeText(this, "To find your location allow GPS access.",Toast.LENGTH_LONG).show();
    }

    private void showTurnOnGPSMessage(){
        Toast.makeText(this, "To find your location turn on GPS.",Toast.LENGTH_LONG).show();
    }

    private void showFailGetGPSDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sorry, your location is undefined")
                .setMessage("Please, use search button to find your city")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void getWeather(String cityName){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestApi.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestApi service = retrofit.create(RestApi.class);

        Call<Model> call = service.getWheatherReportByCityName(cityName);

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Response<Model> response, Retrofit retrofit) {

                if (response != null) {

                    Model model = response.body();

                    City city = new City(model);
                    try {
                        HelperFactory.getHelper().getDao(City.class).createOrUpdate(city);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    MyWeather myWeather = new MyWeather(model, city);
                    try {
                        HelperFactory.getHelper().getDao(MyWeather.class).createOrUpdate(myWeather);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    refreshList();

                    Log.d("data", city.toString() + " " + myWeather.toString());
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("data", t.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint(getString(R.string.city_name));
        searchView.setOnQueryTextListener(this);

        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void refreshList(){

        List<City> cities = new ArrayList<>();

        try {
            cities = HelperFactory.getHelper().getDao(City.class).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (adapter==null){
            adapter = new CitiesAdapter(this, PreviewCityWeather.fromArray(cities));
            adapter.setHasStableIds(true);
            mRecyclerView.setAdapter(adapter);
        }
        else {
            adapter.setCityWeathers(PreviewCityWeather.fromArray(cities));
            mRecyclerView.scrollToPosition(0);
        }

    }
}


