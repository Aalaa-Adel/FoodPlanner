package com.aalaa.foodplanner.ui.categories.presenter;

import com.aalaa.foodplanner.domain.repository.MealRepository;
import com.aalaa.foodplanner.ui.categories.view.CategoriesView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CategoriesPresenterImpl implements CategoriesPresenter {

    private final CategoriesView view;
    private final MealRepository repository;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public CategoriesPresenterImpl(CategoriesView view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void getCategories() {
        view.showLoading();
        disposable.add(repository.getAllCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            view.hideLoading();
                            if (response != null && response.getCategories() != null) {
                                view.showCategories(response.getCategories());
                            } else {
                                view.showError("No categories found");
                            }
                        },
                        error -> {
                            view.hideLoading();
                            view.showError(error.getMessage());
                        }));
    }

    @Override
    public void clear() {
        disposable.clear();
    }
}
