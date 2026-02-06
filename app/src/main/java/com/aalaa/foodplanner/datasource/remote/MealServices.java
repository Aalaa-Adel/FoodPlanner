package com.aalaa.foodplanner.datasource.remote;

import com.aalaa.foodplanner.data.model.AreaResponse;
import com.aalaa.foodplanner.data.model.CategoryResponse;
import com.aalaa.foodplanner.data.model.FilterMealResponse;
import com.aalaa.foodplanner.data.model.IngredientsResponse;
import com.aalaa.foodplanner.data.model.MealResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MealServices {
    // get meal by name
    // www.themealdb.com/api/json/v1/1/search.php?s=Arrabiata
//    @GET("search.php")
//    Call<MealResponse> getMealByName(@Query("s") String mealName);

    // get all meals by first letter
    //www.themealdb.com/api/json/v1/1/search.php?f=a
//    @GET("search.php")
//    Call<MealResponse> getMealByFirstLetter(@Query("f") String firstLetter);

    // get full meal details by id
    //https://www.themealdb.com/api/json/v1/1/lookup.php?i=52772
//    @GET("lookup.php")
//    Call<MealResponse> getMealById(@Query("i") String mealId);

    // get a single random meal
    //www.themealdb.com/api/json/v1/1/random.php
    @GET("random.php")
    Call<MealResponse> getRandomMeal();

    // get all meal categories
    //www.themealdb.com/api/json/v1/1/categories.php
    @GET("categories.php")
    Call<CategoryResponse> getAllCategories();

    // get all Categories only its name
    //www.themealdb.com/api/json/v1/1/list.php?c=list
    //@GET("list.php?c=list")
    //Call<????> getCategoriesList();

    // get all Areas
    //www.themealdb.com/api/json/v1/1/list.php?a=list
    @GET("list.php?a=list")
    Call<AreaResponse> getAreasList();

    // get all Ingredients
    //www.themealdb.com/api/json/v1/1/list.php?i=list
    @GET("list.php?i=list")
    Call<IngredientsResponse> getIngredientsList();

    // filter by main ingredient
    //www.themealdb.com/api/json/v1/1/filter.php?i=chicken_breast
//    @GET("filter.php")
//    Call<FilterMealResponse> filterByIngredient(@Query("i") String ingredient);

    // filter by Category
    //www.themealdb.com/api/json/v1/1/filter.php?c=Seafood
//    @GET("filter.php")
//    Call<FilterMealResponse> filterByCategory(@Query("c") String category);

    // filter by Area
    //www.themealdb.com/api/json/v1/1/filter.php?a=Canadian
//    @GET("filter.php")
//    Call<FilterMealResponse> filterByArea(@Query("a") String area);
}
