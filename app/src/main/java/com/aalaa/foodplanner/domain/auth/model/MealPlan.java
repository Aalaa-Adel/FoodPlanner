package com.aalaa.foodplanner.domain.auth.model;

import java.util.HashMap;
import java.util.Map;

public class MealPlan {
    private Map<String, DayMeals> days;

    public MealPlan(Map<String, DayMeals> days) {
        this.days = days != null ? days : new HashMap<>();
    }



}