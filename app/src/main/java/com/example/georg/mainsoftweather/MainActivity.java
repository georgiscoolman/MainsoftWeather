package com.example.georg.mainsoftweather;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.georg.mainsoftweather.orm.PreviewCityWeatherLoader;
import com.example.georg.mainsoftweather.orm.entitys.City;
import com.example.georg.mainsoftweather.preview.PreviewCityWeather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener , LoaderManager.LoaderCallbacks<List<PreviewCityWeather>>{

    private ProgressDialog searchLocationDialog;
    private ProgressDialog getWeatherDialog;
    private LocationManager locationManager;

    private SharedPreferences mSettings;
    public static final int MY_PERMISSION_REQUEST = 1;
    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_FIRST_LAUNCH = "isFirstLaunch";
    public static final int SEARCH_GPS_MIN_INTERVAL = 10000;
    public static final int SEARCH_GPS_TIME = 60000;
    public static final int SEARCH_GPS_DISTANCE = 10;

    public static final String GPS_TAG = "gps";
    public static final String LOADER_TAG = "loader";
    public static final int LOADER_ID = 1;

    private RecyclerView mRecyclerView;
    private TextView emptyView;
    private CitiesAdapter mAdapter;
    private Loader<List<PreviewCityWeather>> mLoader;
    private ArrayList<PreviewCityWeather> previewCityWeathers;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GetByNameResultReceiver getByNameResultReceiver;
    private UpdateAllResultReceiver updateAllResultReceiver;


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

        mLoader = getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWeathers();
            }
        });

        getByNameResultReceiver = new GetByNameResultReceiver();
        IntentFilter getByNameIntentFilter = new IntentFilter(WeatherService.GET_BY_NAME);
        getByNameIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(getByNameResultReceiver,getByNameIntentFilter);

        updateAllResultReceiver = new UpdateAllResultReceiver();
        IntentFilter updateAlLIntentFilter = new IntentFilter(WeatherService.UPDATE_ALL);
        updateAlLIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(updateAllResultReceiver,updateAlLIntentFilter);
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
                    stopSearchLocation(true);
                }
            };
            h.postDelayed(r,SEARCH_GPS_TIME);

            searchLocationDialog = Utils.initProgressDialog(this,getString(R.string.search_location),getString(R.string.searching_location),new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    stopSearchLocation(false);
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

    private void stopSearchLocation(boolean isTimeEnd) {
        if (locationManager != null)
            locationManager.removeUpdates(locationListener);

        Utils.dissmissDialog(searchLocationDialog);

        if (isTimeEnd) {
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

        refreshPreviewList();

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSearchLocation(false);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(GPS_TAG, "onLocationChanged: " + location.toString());
            String cityName = getCityName(location);
            if (cityName != null) {
                getWeather(cityName);
                stopSearchLocation(false);
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

        getWeatherDialog = Utils.initProgressDialog(this,getString(R.string.getting_weather_title), String.format(getString(R.string.getting_weather_format), cityName));
        getWeatherDialog.show();

        Intent intent = new Intent(this, WeatherService.class);
        intent.setAction(WeatherService.GET_BY_NAME);
        intent.putExtra(WeatherService.NAME,cityName);
        startService(intent);

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
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_refresh){
            swipeRefreshLayout.setRefreshing(true);
            refreshWeathers();
        }

        return super.onOptionsItemSelected(item);
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

    private void refreshPreviewList(){

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
            refreshPreviewList();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<PreviewCityWeather>> loader) {
        Log.d(LOADER_TAG, "onLoaderReset");
    }

    private void refreshWeathers(){
        Intent intent = new Intent(this, WeatherService.class);
        intent.setAction(WeatherService.UPDATE_ALL);
        startService(intent);
    }

    public void removeCity(long id){
        new AsyncTask<Long, Void, Void>(){
            @Override
            protected Void doInBackground(Long... params) {
                Long id = params[0];
                if (id != null){
                    City.removeCity(id);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mLoader.onContentChanged();
            }
        }.execute(id);
    }

    public class GetByNameResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            boolean success = intent.getBooleanExtra(WeatherService.SUCCESS,false);
            Utils.dissmissDialog(getWeatherDialog);
            if (success){
                mLoader.onContentChanged();
            }else{
                Utils.showOkDialog(MainActivity.this, getString(R.string.no_server), getString(R.string.check_internet));
            }

            Log.d("GetByNameResultReceiver", "onReceive " + success);
        }
    }

    public class UpdateAllResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            boolean success = intent.getBooleanExtra(WeatherService.SUCCESS,false);
            swipeRefreshLayout.setRefreshing(false);
            if (success){
                mLoader.onContentChanged();
            }else{
                Utils.showOkDialog(MainActivity.this, getString(R.string.no_server), getString(R.string.check_internet));
            }

            Log.d("UpdateAllResultReceiver", "onReceive " + success);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateAllResultReceiver);
        unregisterReceiver(getByNameResultReceiver);
    }
}


