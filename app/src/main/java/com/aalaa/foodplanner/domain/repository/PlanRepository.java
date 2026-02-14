package com.aalaa.foodplanner.domain.repository;

import com.aalaa.foodplanner.data.db.PlanEntity;
import com.aalaa.foodplanner.domain.models.MealsItem;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public interface PlanRepository {
    Completable addToPlan(MealsItem meal, String day, String slot);

    Completable removeMealFromPlan(PlanEntity plan);

    Flowable<List<PlanEntity>> getAllPlans();

    Completable clearAllPlans();
}
