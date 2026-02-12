package com.aalaa.foodplanner.ui.home.presenter;

import com.aalaa.foodplanner.data.repository.MealRepositoryImpl;
import com.aalaa.foodplanner.domain.models.Area;
import com.aalaa.foodplanner.domain.models.AreaResponse;
import com.aalaa.foodplanner.domain.models.Category;
import com.aalaa.foodplanner.domain.models.CategoryResponse;
import com.aalaa.foodplanner.domain.models.MealResponse;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.aalaa.foodplanner.ui.home.view.HomeView;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenterImpl implements HomePresenter {

    private final HomeView view;
    private final MealRepositoryImpl repository;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public HomePresenterImpl(HomeView view, MealRepositoryImpl repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void getHomeData() {
        view.showLoading();

        disposable.add(
                Single.zip(
                                repository.getRandomMeal(),
                                repository.getAllCategories(),
                                repository.getAllAreas(),
                                repository.getMultipleRandomMeals(10),
                                (mealResponse, categoryResponse, areaResponse,
                                 forYouResponse) -> new HomeData(
                                        mealResponse, categoryResponse,
                                        areaResponse, forYouResponse))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::handleSuccess,
                                this::handleError));
    }

    private void handleSuccess(HomeData homeData) {
        view.hideLoading();

        if (homeData.mealResponse != null && homeData.mealResponse.getMeals() != null
                && !homeData.mealResponse.getMeals().isEmpty()) {
            view.showRandomMeal(homeData.mealResponse.getMeals().get(0));
        }

        if (homeData.forYouResponse != null && !homeData.forYouResponse.isEmpty()) {
            view.showForYouMeals(homeData.forYouResponse);
        }

        if (homeData.categoryResponse != null && homeData.categoryResponse.getCategories() != null) {
            List<Category> categories = homeData.categoryResponse.getCategories();
            view.showCategories(categories);
        }

        if (homeData.areaResponse != null && homeData.areaResponse.getAreas() != null) {
            List<Area> areas = homeData.areaResponse.getAreas();
            List<Area> limitedAreas;
            if (areas.size() > 6) {
                limitedAreas = areas.subList(0, 6);
            } else {
                limitedAreas = areas;
            }
            view.showCountries(limitedAreas);
        }
    }

    private void handleError(Throwable error) {
        view.hideLoading();
        view.showError(error.getMessage() != null ? error.getMessage() : "Unknown Error");
    }

    @Override
    public void onDestroy() {
        disposable.clear();
    }

    private static class HomeData {
        final MealResponse mealResponse;
        final CategoryResponse categoryResponse;
        final AreaResponse areaResponse;
        final List<MealsItem> forYouResponse;

        public HomeData(MealResponse m, CategoryResponse c, AreaResponse a, List<MealsItem> f) {
            this.mealResponse = m;
            this.categoryResponse = c;
            this.areaResponse = a;
            this.forYouResponse = f;
        }
    }
}
