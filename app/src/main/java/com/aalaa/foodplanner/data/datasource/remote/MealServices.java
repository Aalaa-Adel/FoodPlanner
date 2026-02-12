package com.aalaa.foodplanner.data.datasource.remote;

import com.aalaa.foodplanner.domain.models.AreaResponse;
import com.aalaa.foodplanner.domain.models.CategoryResponse;
import com.aalaa.foodplanner.domain.models.IngredientsResponse;
import com.aalaa.foodplanner.domain.models.MealCategoryResponse;
import com.aalaa.foodplanner.domain.models.MealCountryResponse;
import com.aalaa.foodplanner.domain.models.MealIngredientResponse;
import com.aalaa.foodplanner.domain.models.MealResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealServices {


    @GET("search.php")
    Single<MealResponse> getMealByName(@Query("s") String mealName);

    @GET("search.php")
    Single<MealResponse> getMealByFirstLetter(@Query("f") String firstLetter);

    @GET("lookup.php")
    Single<MealResponse> getMealById(@Query("i") String mealId);

    @GET("random.php")
    Single<MealResponse> getRandomMeal();

    @GET("categories.php")
    Single<CategoryResponse> getAllCategories();

    @GET("list.php?c=list")
    Single<CategoryResponse> getCategoriesList();

    @GET("list.php?a=list")
    Single<AreaResponse> getAreasList();

    @GET("list.php?i=list")
    Single<IngredientsResponse> getIngredientsList();

    @GET("filter.php")
    Single<MealIngredientResponse> filterByIngredient(@Query("i") String ingredient);

    @GET("filter.php")
    Single<MealCategoryResponse> filterByCategory(@Query("c") String category);

    @GET("filter.php")
    Single<MealCountryResponse> filterByArea(@Query("a") String area);
}