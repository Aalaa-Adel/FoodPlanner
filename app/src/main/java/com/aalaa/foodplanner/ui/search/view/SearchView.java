package com.aalaa.foodplanner.ui.search.view;

import com.aalaa.foodplanner.domain.models.Area;
import com.aalaa.foodplanner.domain.models.Category;
import com.aalaa.foodplanner.domain.models.Ingredients;
import com.aalaa.foodplanner.domain.models.MealsItem;

import java.util.List;

public interface  SearchView {
    void showCategories(List<Category> categories);

    void showAreas(List<Area> areas);

    void showIngredients(List<Ingredients> ingredients);

    void showExploreItems(List<ExploreCategoryAdapter.ExploreItem> items);

    void showMeals(List<MealsItem> meals, String title);

    void showLoading();

    void hideLoading();

    void showError(String message);

    void showEmptyState(String message);
}
