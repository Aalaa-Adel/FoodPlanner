package com.aalaa.foodplanner.domain.models;

import java.util.ArrayList;

public class MealIngredientResponse {
    private ArrayList<MealSpecification> meals;

    public ArrayList<MealSpecification> getMealsFromIngredient() {
        return meals;
    }
}