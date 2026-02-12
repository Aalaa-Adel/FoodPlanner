package com.aalaa.foodplanner.domain.models;

import java.util.ArrayList;

public class MealCountryResponse {
    private ArrayList<MealSpecification> meals;

    public ArrayList<MealSpecification> getMealsFromCountry() {
        return meals;
    }
}