package com.aalaa.foodplanner.ui.detail.presenter;

import com.aalaa.foodplanner.data.repository.MealRepositoryImpl;
import com.aalaa.foodplanner.ui.detail.view.DetailView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import com.aalaa.foodplanner.data.repository.FavoritesRepositoryImpl;
import com.aalaa.foodplanner.data.repository.MealRepositoryImpl;
import com.aalaa.foodplanner.domain.models.MealsItem;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RecipeDetailPresenterImpl implements RecipeDetailPresenter {

    private final DetailView view;
    private final MealRepositoryImpl mealRepo;
    private final FavoritesRepositoryImpl favRepo;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private MealsItem currentMeal;
    private boolean isFavorite = false;

    public RecipeDetailPresenterImpl(
            DetailView view,
            MealRepositoryImpl mealRepo,
            FavoritesRepositoryImpl favRepo
    ) {
        this.view = view;
        this.mealRepo = mealRepo;
        this.favRepo = favRepo;
    }

    @Override
    public void loadMeal(String mealId) {
        if (mealId == null || mealId.trim().isEmpty()) {
            view.showError("Invalid meal id");
            return;
        }

        view.showLoading();

        disposables.add(
                mealRepo.getMealById(mealId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meal -> {
                                    currentMeal = meal;
                                    view.hideLoading();
                                    view.showRecipe(meal);
                                    checkFavorite(meal.getIdMeal());
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError(error.getMessage() != null ? error.getMessage() : "Failed to load meal");
                                }
                        )
        );
    }

    private void checkFavorite(String mealId) {
        if (mealId == null || mealId.trim().isEmpty()) return;

        disposables.add(
                favRepo.isFavorite(mealId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                fav -> {
                                    isFavorite = fav != null && fav;
                                    view.renderFavorite(isFavorite);
                                },
                                error -> {
                                    isFavorite = false;
                                    view.renderFavorite(false);
                                }
                        )
        );
    }

    @Override
    public void onFavoriteClicked() {
        if (currentMeal == null) return;

        boolean newState = !isFavorite;
        isFavorite = newState;
        view.renderFavorite(isFavorite);

        if (newState) {
            disposables.add(
                    favRepo.addToFavorites(currentMeal)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> view.showAddedToFavorites(),
                                    error -> {
                                        isFavorite = false;
                                        view.renderFavorite(false);
                                        view.showError("Failed to add to favorites");
                                    }
                            )
            );
        } else {
            disposables.add(
                    favRepo.removeFromFavorites(currentMeal)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> view.showRemovedFromFavorites(),
                                    error -> {
                                        isFavorite = true;
                                        view.renderFavorite(true);
                                        view.showError("Failed to remove from favorites");
                                    }
                            )
            );
        }
    }

    @Override
    public void dispose() {
        disposables.clear();
    }
}
