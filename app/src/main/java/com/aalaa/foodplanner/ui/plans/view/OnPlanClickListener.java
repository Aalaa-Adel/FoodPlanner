package com.aalaa.foodplanner.ui.plans.view;

import com.aalaa.foodplanner.datasource.db.PlanEntity;

public interface OnPlanClickListener {
    void onRemoveClick(PlanEntity plan);

    void onMealClick(PlanEntity plan);
}
