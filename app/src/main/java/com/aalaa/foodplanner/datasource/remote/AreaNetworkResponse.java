package com.aalaa.foodplanner.datasource.remote;

import com.aalaa.foodplanner.domain.auth.model.model.Area;

import java.util.List;

public interface AreaNetworkResponse {
    void onAreasSuccess(List<Area> areas);

    void noInternet();

    void onFailure(String message);
}
