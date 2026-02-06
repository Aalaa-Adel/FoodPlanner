package com.aalaa.foodplanner.datasource.remote;

import com.aalaa.foodplanner.domain.auth.model.model.MealsItem;

import java.util.List;

public interface MealsNetworkResponse {
    void onMealsSuccess(List<MealsItem> meals);
    void noInternet();
    void onFailure(String message);
}
