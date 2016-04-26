package com.example.georg.mainsoftweather;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.georg.mainsoftweather.orm.HelperFactory;
import com.example.georg.mainsoftweather.orm.PreviewCityWeatherLoader;
import com.example.georg.mainsoftweather.orm.entitys.City;
import com.example.georg.mainsoftweather.orm.entitys.MyWeather;
import com.example.georg.mainsoftweather.preview.PreviewCityWeather;
import com.example.georg.mainsoftweather.rest.RestApi;
import com.example.georg.mainsoftweather.rest.pojo.Model;

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

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener , LoaderManager.LoaderCallbacks<List<PreviewCityWeather>>{

    private ProgressDialog searchLocationDialog;
    private LocationManager locationManager;

    private SharedPreferences mSettings;
    public static final int MY_PERMISSION_REQUEST = 1;
    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_FIRST_LAUNCH = "isFirstLaunch";
    public static final int SEARCH_GPS_MIN_INTERVAL = 10000;
    public static final int SEARCH_GPS_TIME = 60000;
    public static final int SEARCH_GPS_DISTANCE = 10;

    public static final String GPS_TAG = "gps";
    public static final String DATA_TAG = "data";
    public static final String LOADER_TAG = "loader";
    public static final int LOADER_ID = 1;

    // Key e96b626a0cb231086ffea9d1f23488bd

    private RecyclerView mRecyclerView;
    private TextView emptyView;
    private CitiesAdapter mAdapter;
    private Loader<List<PreviewCityWeather>> mLoader;
    private ArrayList<PreviewCityWeather> previewCityWeathers;
    private RestApi service;
    private Retrofit retrofit;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_cities);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        emptyView = (TextView) findViewById(R.id.tv_empty_list);

        retrofit = new Retrofit.Builder()
                .baseUrl(RestApi.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(RestApi.class);

        mLoader = getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    private void startSearchLocation() {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SEARCH_GPS_MIN_INTERVAL, SEARCH_GPS_DISTANCE, locationListener);

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, SEARCH_GPS_MIN_INTERVAL, SEARCH_GPS_DISTANCE, locationListener);

            Handler h = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    stopSearchLocation(false,false);
                }
            };
            h.postDelayed(r,SEARCH_GPS_TIME);

            searchLocationDialog = new ProgressDialog(this);
            searchLocationDialog.setTitle(getString(R.string.search_location));
            searchLocationDialog.setMessage(getString(R.string.searching_location));
            searchLocationDialog.setCancelable(false);
            searchLocationDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    stopSearchLocation(true,false);
                }
            });
            searchLocationDialog.show();
        }
        else {
            Utils.showOkDialog(this, getString(R.string.turn_on_gps_header), getString(R.string.turn_on_gps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
        }
    }

    private void stopSearchLocation(boolean isUserStop, boolean succes) {
        if (locationManager != null)
        locationManager.removeUpdates(locationListener);

        if (isUserStop) {
            if (searchLocationDialog != null) {
                if (searchLocationDialog.isShowing()) {
                    searchLocationDialog.dismiss();
                }
            }
        }else {
            if (!succes)
            Utils.showOkDialog(this, getString(R.string.location_undefind), getString(R.string.empty_list));
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
                        Utils.showOkDialog(this, getString(R.string.explain_gps), getString(R.string.allow_gps));
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

        //getWeather("Madrid");

        refreshList();

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSearchLocation(true,false);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(GPS_TAG, "onLocationChanged: " + location.toString());
            String cityName = getCityName(location);
            if (cityName != null) {
                getWeather(cityName);
                stopSearchLocation(false,true);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(GPS_TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(GPS_TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(GPS_TAG, "Status: " + String.valueOf(status));
        }
    };

    private String getCityName(Location location) {

        String cityName = null;

        if (location != null)
        Log.d(GPS_TAG, formatLocation(location));

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
                    Log.d(GPS_TAG, "City name " + cityName);
                else
                    Log.d(GPS_TAG, "City name is unknown");
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
                    Utils.showOkDialog(this, getString(R.string.explain_gps), getString(R.string.allow_gps));
                }
            }
        }
    }

    private void getWeather(String cityName){

        Call<Model> call = service.getWheatherReportByCityName(cityName);

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Response<Model> response, Retrofit retrofit) {

                AsyncTask<Response<Model>,Void,Void> saveDataTask = new AsyncTask<Response<Model>, Void, Void>() {
                    @Override
                    protected Void doInBackground(Response<Model>... params) {
                        saveWeatherResponse(params[0]);
                        return null;
                    }
                };
                saveDataTask.execute(response);
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.showOkDialog(MainActivity.this, getString(R.string.no_server), getString(R.string.check_internet));
            }
        });
    }

    private void saveWeatherResponse(Response<Model> response) {
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

            mLoader.onContentChanged();

            Log.d(DATA_TAG, city.toString() + " " + myWeather.toString());
        }
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
        getWeather(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void refreshList(){

        if (previewCityWeathers!=null) {
            if (previewCityWeathers.size() > 0) {
                emptyView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }

            if (mAdapter != null) {
                mAdapter.setCityWeathers(previewCityWeathers);
            } else {
                mAdapter = new CitiesAdapter(this, previewCityWeathers);
                mAdapter.setHasStableIds(true);
                mRecyclerView.setAdapter(mAdapter);
            }
            mAdapter.notifyDataSetChanged();
        }else {
            emptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

    }

    @Override
    public Loader<List<PreviewCityWeather>> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            Log.d(LOADER_TAG, "onCreateLoader");
            return new PreviewCityWeatherLoader(MainActivity.this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<PreviewCityWeather>> loader, List<PreviewCityWeather> data) {
        Log.d(LOADER_TAG, "onLoadFinished");

        if (loader.getId() == LOADER_ID){
            previewCityWeathers = (ArrayList<PreviewCityWeather>) data;
            refreshList();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<PreviewCityWeather>> loader) {
        Log.d(LOADER_TAG, "onLoaderReset");
    }
}


