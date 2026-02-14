package com.aalaa.foodplanner.ui.favorites.presenter;

import com.aalaa.foodplanner.domain.models.MealsItem;

public interface FavoritesPresenter {
    void loadFavorites();

    void addToFavorites(MealsItem  meal);

    void removeFromFavorites(MealsItem  meal);

    void checkIfFavorite(String mealId);

    void dispose();
}
