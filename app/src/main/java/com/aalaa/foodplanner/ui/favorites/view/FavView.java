package com.aalaa.foodplanner.ui.favorites.view;

import com.aalaa.foodplanner.domain.models.MealsItem;

import java.util.List;

public interface FavView {
    void showFavorites(List<MealsItem> favorites);

    void showEmptyState();

    void showError(String error);

    void showAddedToFavorites();

    void showRemovedFromFavorites();

    void updateFavoriteIcon(boolean isFavorite);
}
