package com.aalaa.foodplanner.ui.plans.presenter;

import com.aalaa.foodplanner.datasource.db.PlanEntity;
import com.aalaa.foodplanner.domain.models.MealsItem;

public interface PlanPresenter {
    void loadPlans();

    void addToPlan(MealsItem meal, String day, String slot);

    void removeFromPlan(PlanEntity plan);

    void dispose();
}
