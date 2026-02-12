package com.aalaa.foodplanner.domain.repository;

import com.aalaa.foodplanner.domain.models.AreaResponse;
import com.aalaa.foodplanner.domain.models.CategoryResponse;
import com.aalaa.foodplanner.domain.models.IngredientsResponse;
import com.aalaa.foodplanner.domain.models.MealCategoryResponse;
import com.aalaa.foodplanner.domain.models.MealCountryResponse;
import com.aalaa.foodplanner.domain.models.MealIngredientResponse;
import com.aalaa.foodplanner.domain.models.MealResponse;
import com.aalaa.foodplanner.domain.models.MealsItem;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface MealRepository {


    Single<MealResponse> getRandomMeal();

    Single<List<MealsItem>> getMultipleRandomMeals(int count);

    Single<MealsItem> getMealById(String id);


    Single<MealCategoryResponse> getMealsByCategory(String category);

    Single<MealCountryResponse> getMealsByArea(String area);

    Single<MealIngredientResponse> getMealsByIngredient(String ingredient);


    Single<CategoryResponse> getAllCategories();

    Single<CategoryResponse> getCategoriesList();


    Single<AreaResponse> getAllAreas();


    Single<IngredientsResponse> getAllIngredients();
}