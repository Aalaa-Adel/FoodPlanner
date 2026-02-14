package com.aalaa.foodplanner.ui.plans.presenter;

import android.util.Log;

import com.aalaa.foodplanner.data.db.PlanEntity;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.aalaa.foodplanner.domain.repository.PlanRepository;
import com.aalaa.foodplanner.ui.plans.view.PlanView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlanPresenterImpl implements PlanPresenter {

    private static final String TAG = "PlanPresenterImpl";

    private final PlanView planView;
    private final PlanRepository planRepository;
    private final CompositeDisposable disposable;

    public PlanPresenterImpl(PlanRepository planRepository, PlanView planView) {
        this.planRepository = planRepository;
        this.planView = planView;
        this.disposable = new CompositeDisposable();
    }

    @Override
    public void loadPlans() {
        disposable.add(
                planRepository.getAllPlans()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                plans -> {
                                    if (plans == null || plans.isEmpty()) {
                                        planView.showEmptyState();
                                    } else {
                                        planView.showPlans(plans);
                                    }
                                },
                                error -> {
                                    Log.e(TAG, "Error loading plans", error);
                                    planView.showError(error.getMessage());
                                }));
    }

    @Override
    public void addToPlan(MealsItem meal, String day, String slot) {
        disposable.add(
                planRepository.addToPlan(meal, day, slot)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> planView.showAddedToPlan(),
                                error -> {
                                    Log.e(TAG, "Error adding to plan", error);
                                    planView.showError(error.getMessage());
                                }));
    }

    @Override
    public void removeFromPlan(PlanEntity plan) {
        disposable.add(
                planRepository.removeMealFromPlan(plan)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> planView.showRemovedFromPlan(),
                                error -> {
                                    Log.e(TAG, "Error removing from plan", error);
                                    planView.showError(error.getMessage());
                                }));
    }

    @Override
    public void dispose() {
        disposable.clear();
    }
}
