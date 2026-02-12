package com.aalaa.foodplanner.data.repository;

import com.aalaa.foodplanner.data.datasource.remote.MealRemoteDataSource;
import com.aalaa.foodplanner.domain.repository.MealRepository;
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

public class MealRepositoryImpl implements MealRepository {

    private static volatile MealRepositoryImpl instance;
    private final MealRemoteDataSource remoteDataSource;

    public MealRepositoryImpl(MealRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }

    public static MealRepositoryImpl getInstance(MealRemoteDataSource remoteDataSource) {
        if (instance == null) {
            synchronized (MealRepositoryImpl.class) {
                if (instance == null) {
                    instance = new MealRepositoryImpl(remoteDataSource);
                }
            }
        }
        return instance;
    }

    @Override
    public Single<MealResponse> getRandomMeal() {
        return remoteDataSource.getRandomMeal();
    }

    @Override
    public Single<List<MealsItem>> getMultipleRandomMeals(int count) {
        return remoteDataSource.getMultipleRandomMeals(count);
    }

    @Override
    public Single<MealsItem> getMealById(String id) {
        return remoteDataSource.getMealById(id)
                .map(response -> {
                    if (response.getMeals() != null && !response.getMeals().isEmpty()) {
                        return response.getMeals().get(0);
                    }
                    throw new IllegalStateException("Meal not found with ID: " + id);
                });
    }


    @Override
    public Single<MealCategoryResponse> getMealsByCategory(String category) {
        return remoteDataSource.filterByCategory(category);
    }

    @Override
    public Single<MealCountryResponse> getMealsByArea(String area) {
        return remoteDataSource.filterByArea(area);
    }

    @Override
    public Single<MealIngredientResponse> getMealsByIngredient(String ingredient) {
        return remoteDataSource.filterByIngredient(ingredient);
    }


    @Override
    public Single<CategoryResponse> getAllCategories() {
        return remoteDataSource.getAllCategories();
    }

    @Override
    public Single<CategoryResponse> getCategoriesList() {
        return remoteDataSource.getCategoriesList();
    }


    @Override
    public Single<AreaResponse> getAllAreas() {
        return remoteDataSource.getAreas();
    }


    @Override
    public Single<IngredientsResponse> getAllIngredients() {
        return remoteDataSource.getIngredients();
    }
}