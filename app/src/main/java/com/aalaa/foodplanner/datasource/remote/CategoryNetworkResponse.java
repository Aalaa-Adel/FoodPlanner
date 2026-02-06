package com.aalaa.foodplanner.datasource.remote;

import com.aalaa.foodplanner.data.model.Category;
import com.aalaa.foodplanner.data.model.MealsItem;

import java.util.List;

public interface CategoryNetworkResponse {

    void onCategoriesSuccess(List<Category> categories);

    void noInternet();

    void onFailure(String message);
}
