package com.aalaa.foodplanner.ui.plans.view;

import com.aalaa.foodplanner.datasource.db.PlanEntity;

import java.util.List;

public interface PlanView {
    void showPlans(List<PlanEntity> plans);

    void showEmptyState();

    void showError(String error);

    void showAddedToPlan();

    void showRemovedFromPlan();
}
