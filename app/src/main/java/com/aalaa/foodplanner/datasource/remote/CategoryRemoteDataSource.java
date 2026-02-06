package com.aalaa.foodplanner.datasource.remote;

import com.aalaa.foodplanner.domain.auth.model.model.Category;
import com.aalaa.foodplanner.domain.auth.model.model.CategoryResponse;
import com.aalaa.foodplanner.data.network.Network;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRemoteDataSource {

    private MealServices mealServices;

    public CategoryRemoteDataSource() {
        this.mealServices = new Network().getMealServices();
    }

    public void getCategories(CategoryNetworkResponse callback) {
        mealServices.getAllCategories().enqueue(
                new Callback<CategoryResponse>() {
                    @Override
                    public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                        if (response.code() == 200) {
                            CategoryResponse categoryResponse = response.body();
                            List<Category> categories = categoryResponse.getCategories();
                            callback.onCategoriesSuccess(categories);
                        } else {
                            callback.onFailure("Something went wrong");
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryResponse> call, Throwable t) {
                        if (t instanceof IOException) {
                            callback.noInternet();
                        } else {
                            callback.onFailure("Something went wrong");
                        }
                    }
                });
    }

}
