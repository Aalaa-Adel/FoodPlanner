package com.aalaa.foodplanner.data.datasource.remote;

import com.aalaa.foodplanner.data.network.Network;
import com.aalaa.foodplanner.domain.models.AreaResponse;
import com.aalaa.foodplanner.domain.models.CategoryResponse;
import com.aalaa.foodplanner.domain.models.IngredientsResponse;
import com.aalaa.foodplanner.domain.models.MealCategoryResponse;
import com.aalaa.foodplanner.domain.models.MealCountryResponse;
import com.aalaa.foodplanner.domain.models.MealIngredientResponse;
import com.aalaa.foodplanner.domain.models.MealResponse;
import com.aalaa.foodplanner.domain.models.MealsItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class MealRemoteDataSource {

    private static volatile MealRemoteDataSource INSTANCE;
    private final MealServices mealServices;

    private MealRemoteDataSource(MealServices mealServices) {
        this.mealServices = mealServices;
    }

    public static MealRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (MealRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MealRemoteDataSource(Network.getInstance().mealServices());
                }
            }
        }
        return INSTANCE;
    }

    public Single<MealResponse> getRandomMeal() {
        return mealServices.getRandomMeal();
    }

    public Single<List<MealsItem>> getMultipleRandomMeals(int count) {
        List<Single<MealResponse>> calls = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            calls.add(mealServices.getRandomMeal());
        }

        return Single.zip(calls, responses -> {
            List<MealsItem> allMeals = new ArrayList<>();
            for (Object response : responses) {
                MealResponse mealResponse = (MealResponse) response;
                if (mealResponse.getMeals() != null && !mealResponse.getMeals().isEmpty()) {
                    allMeals.add(mealResponse.getMeals().get(0));
                }
            }
            return allMeals;
        });
    }

    public Single<MealResponse> getMealById(String id) {
        return mealServices.getMealById(id);
    }

    public Single<MealCategoryResponse> filterByCategory(String category) {
        return mealServices.filterByCategory(category);
    }

    public Single<MealCountryResponse> filterByArea(String area) {
        return mealServices.filterByArea(area);
    }

    public Single<MealIngredientResponse> filterByIngredient(String ingredient) {
        return mealServices.filterByIngredient(ingredient);
    }

    public Single<CategoryResponse> getAllCategories() {
        return mealServices.getAllCategories();
    }

    public Single<CategoryResponse> getCategoriesList() {
        return mealServices.getCategoriesList();
    }

    public Single<AreaResponse> getAreas() {
        return mealServices.getAreasList();
    }

    public Single<IngredientsResponse> getIngredients() {
        return mealServices.getIngredientsList();
    }
}
