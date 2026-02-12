package com.aalaa.foodplanner.ui.search.presenter;

import android.util.Log;

import com.aalaa.foodplanner.domain.repository.MealRepository;
import com.aalaa.foodplanner.domain.models.Area;
import com.aalaa.foodplanner.domain.models.Category;
import com.aalaa.foodplanner.domain.models.Ingredients;
import com.aalaa.foodplanner.domain.models.MealSpecification;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.aalaa.foodplanner.ui.search.view.ExploreCategoryAdapter;
import com.aalaa.foodplanner.ui.search.view.SearchView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPresenterImpl implements SearchPresenter {

    private static final String TAG = "SearchPresenter";

    private final SearchView view;
    private final MealRepository repository;
    private final CompositeDisposable disposables;

    private List<Category> allCategories = new ArrayList<>();
    private List<Area> allAreas = new ArrayList<>();
    private List<Ingredients> allIngredients = new ArrayList<>();

    public SearchPresenterImpl(SearchView view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void loadCategories() {
        view.showLoading();
        disposables.add(
                repository.getAllCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    view.hideLoading();
                                    allCategories = response.getCategories();
                                    view.showCategories(allCategories);
                                },
                                error -> {
                                    view.hideLoading();
                                    Log.e(TAG, "Error loading categories", error);
                                    view.showError("Failed to load categories");
                                }));
    }

    @Override
    public void loadAreas() {
        view.showLoading();
        disposables.add(
                repository.getAllAreas()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    view.hideLoading();
                                    allAreas = response.getAreas();
                                    view.showAreas(allAreas);
                                },
                                error -> {
                                    view.hideLoading();
                                    Log.e(TAG, "Error loading areas", error);
                                    view.showError("Failed to load countries");
                                }));
    }

    @Override
    public void loadIngredients() {
        view.showLoading();
        disposables.add(
                repository.getAllIngredients()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    view.hideLoading();
                                    allIngredients = response.getMeals();
                                    view.showIngredients(allIngredients);
                                },
                                error -> {
                                    view.hideLoading();
                                    Log.e(TAG, "Error loading ingredients", error);
                                    view.showError("Failed to load ingredients");
                                }));
    }

    @Override
    public void searchCategories(String query) {
        if (allCategories == null || allCategories.isEmpty())
            return;

        List<ExploreCategoryAdapter.ExploreItem> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Category cat : allCategories) {
            if (cat.getStrCategory().toLowerCase().contains(lowerQuery)) {
                filtered.add(new ExploreCategoryAdapter.ExploreItem(
                        cat.getStrCategory(),
                        cat.getStrCategoryThumb()));
            }
        }

        if (filtered.isEmpty()) {
            view.showEmptyState("No categories match your search");
        } else {
            view.showExploreItems(filtered);
        }
    }

    @Override
    public void searchAreas(String query) {
        if (allAreas == null || allAreas.isEmpty())
            return;

        List<ExploreCategoryAdapter.ExploreItem> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Area area : allAreas) {
            if (area.getStrArea().toLowerCase().contains(lowerQuery)) {
                filtered.add(new ExploreCategoryAdapter.ExploreItem(
                        area.getStrArea(),
                        null));
            }
        }

        if (filtered.isEmpty()) {
            view.showEmptyState("No countries match your search");
        } else {
            view.showExploreItems(filtered);
        }
    }

    @Override
    public void searchIngredients(String query) {
        if (allIngredients == null || allIngredients.isEmpty())
            return;

        List<ExploreCategoryAdapter.ExploreItem> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Ingredients ing : allIngredients) {
            if (ing.getStrIngredient().toLowerCase().contains(lowerQuery)) {
                filtered.add(new ExploreCategoryAdapter.ExploreItem(
                        ing.getStrIngredient(),
                        "https://www.themealdb.com/images/ingredients/" + ing.getStrIngredient() + "-Small.png"));
            }
        }

        if (filtered.isEmpty()) {
            view.showEmptyState("No ingredients match your search");
        } else {
            view.showExploreItems(filtered);
        }
    }

    @Override
    public void getMealsByCategory(String category) {
        view.showLoading();
        disposables.add(
                repository.getMealsByCategory(category)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(response -> convertSpecsToMeals(response.getMealsFromCategory()))
                        .subscribe(
                                meals -> {
                                    view.hideLoading();
                                    if (meals.isEmpty()) {
                                        view.showEmptyState("No meals found for category: " + category);
                                    } else {
                                        view.showMeals(meals, "Category: " + category);
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Search failed: " + error.getMessage());
                                }));
    }

    @Override
    public void getMealsByArea(String area) {
        view.showLoading();
        disposables.add(
                repository.getMealsByArea(area)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(response -> convertSpecsToMeals(response.getMealsFromCountry()))
                        .subscribe(
                                meals -> {
                                    view.hideLoading();
                                    if (meals.isEmpty()) {
                                        view.showEmptyState("No meals found for area: " + area);
                                    } else {
                                        view.showMeals(meals, "Country: " + area);
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Search failed: " + error.getMessage());
                                }));
    }

    @Override
    public void getMealsByIngredient(String ingredient) {
        view.showLoading();
        disposables.add(
                repository.getMealsByIngredient(ingredient)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(response -> convertSpecsToMeals(response.getMealsFromIngredient()))
                        .subscribe(
                                meals -> {
                                    view.hideLoading();
                                    if (meals.isEmpty()) {
                                        view.showEmptyState("No meals found for ingredient: " + ingredient);
                                    } else {
                                        view.showMeals(meals, "Ingredient: " + ingredient);
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Search failed: " + error.getMessage());
                                }));
    }

    private List<MealsItem> convertSpecsToMeals(List<MealSpecification> specs) {
        List<MealsItem> meals = new ArrayList<>();
        if (specs != null) {
            for (MealSpecification spec : specs) {
                MealsItem item = new MealsItem();
                item.setIdMeal(spec.getIdMeal());
                item.setStrMeal(spec.getStrMeal());
                item.setStrMealThumb(spec.getStrMealThumb());
                meals.add(item);
            }
        }
        return meals;
    }

    @Override
    public void dispose() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }
}