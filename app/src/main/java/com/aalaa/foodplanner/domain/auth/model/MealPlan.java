package com.aalaa.foodplanner.domain.auth.model;

import java.util.HashMap;
import java.util.Map;

public class MealPlan {
    private Map<String, DayMeals> days;

    public MealPlan() {
        this.days = new HashMap<>();
    }

    public MealPlan(Map<String, DayMeals> days) {
        this.days = days != null ? days : new HashMap<>();
    }

    public Map<String, DayMeals> getDays() { return days; }
    public void setDays(Map<String, DayMeals> days) { this.days = days; }

    public DayMeals getDayMeals(String date) {
        return days.get(date);
    }

    public void setDayMeals(String date, DayMeals dayMeals) {
        if (dayMeals != null && !dayMeals.isEmpty()) {
            days.put(date, dayMeals);
        } else {
            days.remove(date);
        }
    }

    public void removeDayMeals(String date) {
        days.remove(date);
    }

    public boolean hasMealsForDay(String date) {
        DayMeals dayMeals = days.get(date);
        return dayMeals != null && !dayMeals.isEmpty();
    }
}