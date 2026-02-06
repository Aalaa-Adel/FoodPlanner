package com.aalaa.foodplanner.datasource.remote;

import com.aalaa.foodplanner.data.model.Category;
import com.aalaa.foodplanner.data.model.CategoryResponse;
import com.aalaa.foodplanner.data.model.MealResponse;
import com.aalaa.foodplanner.data.model.MealsItem;
import com.aalaa.foodplanner.network.Network;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealsRemoteDataSource {
    private MealServices mealServices;
    public MealsRemoteDataSource() {
        this.mealServices = new Network().getMealServices();
    }

    public void getMeals(MealsNetworkResponse callback) {
        mealServices.getRandomMeal().enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if(response.code()==200)
                {
                    MealResponse mealResponse = response.body();
                    List<MealsItem> mealsItems = mealResponse.getMeals();
                    callback.onMealsSuccess(mealsItems);
                }
                else {
                    callback.onFailure("Something went wrong");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                if(t instanceof IOException){
                    callback.noInternet();
                }
                else {
                    callback.onFailure("Something went wrong");
                }
            }
        });
    }

}
