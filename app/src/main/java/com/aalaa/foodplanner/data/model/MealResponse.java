package com.aalaa.foodplanner.data.model;

import java.util.ArrayList;
import java.util.List;

public class MealResponse {
    private List<MealsItem> meals;

    public MealResponse() {
    }

    public MealResponse(MealsItem meals) {
        this.meals = new ArrayList<>();
        this.meals.add(meals);
    }

    public List<MealsItem> getMeals() {
        return meals;
    }

}