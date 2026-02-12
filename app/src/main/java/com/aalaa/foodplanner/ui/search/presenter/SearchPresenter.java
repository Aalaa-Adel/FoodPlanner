package com.aalaa.foodplanner.ui.search.presenter;

public interface SearchPresenter {

    void loadCategories();

    void loadAreas();

    void loadIngredients();


    void searchCategories(String query);

    void searchAreas(String query);

    void searchIngredients(String query);

    void getMealsByCategory(String category);

    void getMealsByArea(String area);

    void getMealsByIngredient(String ingredient);

    void dispose();
}