package com.aalaa.foodplanner.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.aalaa.foodplanner.domain.models.MealsItem;

@Entity(tableName = "favorite_meals")
public class FavoriteMealEntity {

    @PrimaryKey
    @NonNull
    private String idMeal;
    @ColumnInfo(name = "strMeal")
    private String strMeal;
    @ColumnInfo(name = "strMealThumb")
    private String strMealThumb;
    @ColumnInfo(name = "strCategory")
    private String strCategory;
    @ColumnInfo(name = "strArea")
    private String strArea;

    @TypeConverters(Converters.class)
    private MealsItem meal;

    public FavoriteMealEntity(@NonNull String idMeal, String strMeal, String strMealThumb, String strCategory,
            String strArea, MealsItem meal) {
        this.idMeal = idMeal;
        this.strMeal = strMeal;
        this.strMealThumb = strMealThumb;
        this.strCategory = strCategory;
        this.strArea = strArea;
        this.meal = meal;
    }

    public FavoriteMealEntity(MealsItem item) {
        this.idMeal = item.getIdMeal();
        this.strMeal = item.getStrMeal();
        this.strMealThumb = item.getStrMealThumb();
        this.strCategory = item.getStrCategory();
        this.strArea = item.getStrArea();
        this.meal = item;
    }

    @NonNull
    public String getIdMeal() {
        return idMeal;
    }

    public void setIdMeal(@NonNull String idMeal) {
        this.idMeal = idMeal;
    }

    public String getStrMeal() {
        return strMeal;
    }

    public void setStrMeal(String strMeal) {
        this.strMeal = strMeal;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }

    public void setStrMealThumb(String strMealThumb) {
        this.strMealThumb = strMealThumb;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public void setStrCategory(String strCategory) {
        this.strCategory = strCategory;
    }

    public String getStrArea() {
        return strArea;
    }

    public void setStrArea(String strArea) {
        this.strArea = strArea;
    }

    public MealsItem getMeal() {
        return meal;
    }

    public void setMeal(MealsItem meal) {
        this.meal = meal;
    }
}
