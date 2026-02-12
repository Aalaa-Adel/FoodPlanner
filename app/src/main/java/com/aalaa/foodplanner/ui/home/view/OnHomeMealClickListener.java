package com.aalaa.foodplanner.ui.home.view;

import com.aalaa.foodplanner.domain.models.MealsItem;

public interface OnHomeMealClickListener {
    void onMealClick(MealsItem meal);

    void onBookmarkClick(MealsItem meal, int position);
}
