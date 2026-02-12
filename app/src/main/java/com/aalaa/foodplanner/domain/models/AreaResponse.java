package com.aalaa.foodplanner.domain.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AreaResponse {
    @SerializedName("meals")
    private List<Area> meals;

    public List<Area> getAreas() {
        return meals;
    }

}