package com.aalaa.foodplanner.ui.listing.presenter;

import com.aalaa.foodplanner.data.repository.FavoritesRepositoryImpl;
import com.aalaa.foodplanner.data.repository.MealRepositoryImpl;
import com.aalaa.foodplanner.domain.models.MealCategoryResponse;
import com.aalaa.foodplanner.domain.models.MealCountryResponse;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.aalaa.foodplanner.ui.common.AppSnack;
import com.aalaa.foodplanner.ui.listing.view.MealsListingView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealsListingPresenterImpl implements MealsListingPresenter {

    private final MealsListingView view;
    private final MealRepositoryImpl repository;
    private final FavoritesRepositoryImpl favoritesRepository;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public MealsListingPresenterImpl(MealsListingView view, MealRepositoryImpl repository,
            FavoritesRepositoryImpl favoritesRepository) {
        this.view = view;
        this.repository = repository;
        this.favoritesRepository = favoritesRepository;
    }

    @Override
    public void getMealsByCategory(String category) {
        view.showLoading();
        disposable.add(repository.getMealsByCategory(category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::handleCategorySuccess,
                        this::handleError));
    }

    @Override
    public void getMealsByArea(String area) {
        view.showLoading();
        disposable.add(repository.getMealsByArea(area)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::handleAreaSuccess,
                        this::handleError));
    }

    private void handleCategorySuccess(MealCategoryResponse response) {
        view.hideLoading();
        if (response != null && response.getMealsFromCategory() != null) {
            view.showMeals(response.getMealsFromCategory());
        } else {
            view.showError("No meals found for this category");
        }
    }

    private void handleAreaSuccess(MealCountryResponse response) {
        view.hideLoading();
        if (response != null && response.getMealsFromCountry() != null) {
            view.showMeals(response.getMealsFromCountry());
        } else {
            view.showError("No meals found for this area");
        }
    }

    @Override
    public void getMealById(String mealId) {
        if (view != null) {
            view.showLoading();
            disposable.add(repository.getMealById(mealId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            meal -> {
                                view.hideLoading();
                                if (meal != null && meal.getIdMeal() != null && !meal.getIdMeal().isEmpty()) {
                                    view.navigateToMealDetail(meal);
                                } else {
                                    view.showError("Meal not found");
                                }
                            },
                            error -> {
                                view.hideLoading();
                                view.showError(error.getMessage() != null ? error.getMessage() : "Unknown error");
                            }));
        }
    }

    @Override
    public void addToFavorites(MealsItem meal) {
        disposable.add(favoritesRepository.addToFavorites(meal)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    view.showAddedToFavorites();
                }, this::handleError));
    }

    @Override
    public void removeFromFavorites(MealsItem meal) {
        disposable.add(favoritesRepository.removeFromFavorites(meal)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    view.showRemovedFromFavorites();
                }, this::handleError));
    }

    private void handleError(Throwable error) {
        view.hideLoading();
        view.showError(error.getMessage() != null ? error.getMessage() : "Unknown Error");
    }

    @Override
    public void onDestroy() {
        disposable.clear();
    }
}
