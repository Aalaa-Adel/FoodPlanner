package com.aalaa.foodplanner.ui.listing.presenter;

public interface MealsListingPresenter {
    void getMealsByCategory(String category);

    void getMealsByArea(String area);

    void getMealById(String mealId);
    void onDestroy();

}
