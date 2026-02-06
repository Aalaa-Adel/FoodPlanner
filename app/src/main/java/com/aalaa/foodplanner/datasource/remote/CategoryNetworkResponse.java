package com.aalaa.foodplanner.datasource.remote;

import com.aalaa.foodplanner.domain.auth.model.model.Category;

import java.util.List;

public interface CategoryNetworkResponse {

    void onCategoriesSuccess(List<Category> categories);

    void noInternet();

    void onFailure(String message);
}
