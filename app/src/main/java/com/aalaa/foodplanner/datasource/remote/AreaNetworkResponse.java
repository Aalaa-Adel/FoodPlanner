package com.aalaa.foodplanner.datasource.remote;

import com.aalaa.foodplanner.data.model.Area;
import com.aalaa.foodplanner.data.model.Category;

import java.util.List;

public interface AreaNetworkResponse {
    void onAreasSuccess(List<Area> areas);

    void noInternet();

    void onFailure(String message);
}
