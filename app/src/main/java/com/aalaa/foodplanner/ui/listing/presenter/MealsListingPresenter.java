package com.aalaa.foodplanner.ui.listing.presenter;

import com.aalaa.foodplanner.domain.models.MealsItem;

public interface MealsListingPresenter {
    void getMealsByCategory(String category);

    void getMealsByArea(String area);

    void getMealById(String mealId);

    void addToFavorites(MealsItem meal);

    void removeFromFavorites(MealsItem meal);

    void onDestroy();
}
