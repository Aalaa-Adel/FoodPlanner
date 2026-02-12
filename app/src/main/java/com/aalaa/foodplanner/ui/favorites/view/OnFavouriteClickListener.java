package com.aalaa.foodplanner.ui.favorites.view;

import com.aalaa.foodplanner.domain.models.MealsItem;

public interface OnFavouriteClickListener {
    void onRemoveClick(MealsItem meal);

    void onMealClick(MealsItem meal);
}
