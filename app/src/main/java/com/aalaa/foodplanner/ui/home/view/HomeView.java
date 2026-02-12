package com.aalaa.foodplanner.ui.home.view;

import com.aalaa.foodplanner.domain.models.Area;
import com.aalaa.foodplanner.domain.models.Category;
import com.aalaa.foodplanner.domain.models.MealsItem;

import java.util.List;

public interface HomeView {
    void showLoading();

    void hideLoading();

    void showError(String message);
    void showRandomMeal(MealsItem meal);

    void showForYouMeals(List<MealsItem> meals);

    void showCategories(List<Category> categories);

    void showCountries(List<Area> countries);
}
