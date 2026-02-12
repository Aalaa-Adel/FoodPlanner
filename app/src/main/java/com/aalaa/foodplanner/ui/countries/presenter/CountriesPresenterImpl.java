package com.aalaa.foodplanner.ui.countries.presenter;

import com.aalaa.foodplanner.domain.repository.MealRepository;
import com.aalaa.foodplanner.ui.countries.view.CountriesView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CountriesPresenterImpl implements CountriesPresenter {

    private final CountriesView view;
    private final MealRepository repository;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public CountriesPresenterImpl(CountriesView view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void getCountries() {
        view.showLoading();
        disposable.add(repository.getAllAreas()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            view.hideLoading();
                            if (response != null && response.getAreas() != null) {
                                view.showCountries(response.getAreas());
                            } else {
                                view.showError("No countries found");
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
