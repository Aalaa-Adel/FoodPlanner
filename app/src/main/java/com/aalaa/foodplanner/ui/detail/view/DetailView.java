package com.aalaa.foodplanner.ui.detail.view;

import com.aalaa.foodplanner.domain.models.MealsItem;

public interface DetailView {
    void showRecipe(MealsItem meal);
    void renderFavorite(boolean isFav);

    void showLoading();
    void hideLoading();
    void showError(String message);

    void showAddedToFavorites();
    void showRemovedFromFavorites();
}
