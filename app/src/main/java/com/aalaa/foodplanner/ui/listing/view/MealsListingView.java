package com.aalaa.foodplanner.ui.listing.view;

import com.aalaa.foodplanner.domain.models.MealSpecification;
import com.aalaa.foodplanner.domain.models.MealsItem;

import java.util.List;

public interface MealsListingView {
    void showMeals(List<MealSpecification> meals);

    void navigateToMealDetail(MealsItem meal);

    void showLoading();

    void hideLoading();

    void showError(String message);

    void showAddedToFavorites();

    void showRemovedFromFavorites();
}
