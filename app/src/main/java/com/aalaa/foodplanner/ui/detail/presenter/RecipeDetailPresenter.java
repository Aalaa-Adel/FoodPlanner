package com.aalaa.foodplanner.ui.detail.presenter;

public interface RecipeDetailPresenter {
    void loadMeal(String mealId);
    void onFavoriteClicked();
    void dispose();
}
