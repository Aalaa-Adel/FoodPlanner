package com.aalaa.foodplanner.network;

import com.aalaa.foodplanner.datasource.remote.MealServices;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {

    private MealServices mealServices;

    private Retrofit retrofit;
    public static String BASEURL = "https://www.themealdb.com/api/json/v1/1/";

    public Network() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public MealServices getMealServices() {
        if (mealServices == null) {
            mealServices = retrofit.create(MealServices.class);
        }
        return mealServices;
    }
}
