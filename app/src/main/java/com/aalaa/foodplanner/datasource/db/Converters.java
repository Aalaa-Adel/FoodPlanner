package com.aalaa.foodplanner.datasource.db;

import androidx.room.TypeConverter;

import com.aalaa.foodplanner.domain.models.MealsItem;
import com.google.gson.Gson;

public class Converters {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static MealsItem fromString(String value) {
        return value == null ? null : gson.fromJson(value, MealsItem.class);
    }

    @TypeConverter
    public static String fromMealsItem(MealsItem meal) {
        return meal == null ? null : gson.toJson(meal);
    }
}
