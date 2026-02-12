package com.aalaa.foodplanner.data.network;


import com.aalaa.foodplanner.data.datasource.remote.MealServices;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {
    private static volatile Network INSTANCE;
    private final MealServices mealServices;

    private Retrofit retrofit;
    public static final String BASEURL = "https://www.themealdb.com/api/json/v1/1/";

    public Network() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        mealServices = retrofit.create(MealServices.class);

    }

    public static Network getInstance() {
        if (INSTANCE == null) {
            synchronized (Network.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Network();
                }
            }
        }
        return INSTANCE;
    }

    public MealServices mealServices() {
        return mealServices;
    }
}
