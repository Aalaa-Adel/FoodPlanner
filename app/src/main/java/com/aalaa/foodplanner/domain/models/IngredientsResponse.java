package com.aalaa.foodplanner.domain.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class IngredientsResponse {
    @SerializedName("meals")
    private ArrayList<Ingredients> meals;

    public void setMeals(ArrayList<Ingredients> meals) {
        this.meals = meals;
    }

    public ArrayList<Ingredients> getMeals() {
        return meals;
    }
}