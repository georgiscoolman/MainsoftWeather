package com.example.georg.mainsoftweather.rest;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Georg on 06.05.2016.
 */
public class RetrofitServiceFactory {

    private static volatile RestApi retrofitSingletone;

    private RetrofitServiceFactory() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestApi.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitSingletone = retrofit.create(RestApi.class);
    }

    public static RestApi getInstance(){
        if (retrofitSingletone == null) {
            synchronized(RestApi.class){
                if (retrofitSingletone == null) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(RestApi.URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    retrofitSingletone = retrofit.create(RestApi.class);
                }
            }
        }
        return retrofitSingletone;
    }

}
