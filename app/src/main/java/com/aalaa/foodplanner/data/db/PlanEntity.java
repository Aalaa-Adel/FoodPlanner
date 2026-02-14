package com.aalaa.foodplanner.data.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.TypeConverters;
import com.aalaa.foodplanner.domain.models.MealsItem;

@Entity(tableName = "plan_meals", primaryKeys = { "date", "slot", "mealId" })
public class PlanEntity {

    @NonNull
    private String date;

    @NonNull
    private String slot;

    @NonNull
    private String mealId;

    @TypeConverters(Converters.class)
    private MealsItem meal;

    public PlanEntity(@NonNull String date, @NonNull String slot, @NonNull String mealId, MealsItem meal) {
        this.date = date;
        this.slot = slot;
        this.mealId = mealId;
        this.meal = meal;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    @NonNull
    public String getSlot() {
        return slot;
    }

    public void setSlot(@NonNull String slot) {
        this.slot = slot;
    }

    @NonNull
    public String getMealId() {
        return mealId;
    }

    public void setMealId(@NonNull String mealId) {
        this.mealId = mealId;
    }

    public MealsItem getMeal() {
        return meal;
    }

    public void setMeal(MealsItem meal) {
        this.meal = meal;
    }
}
