package com.aalaa.foodplanner.ui.plans.view;

import com.aalaa.foodplanner.data.db.PlanEntity;

public interface OnPlanClickListener {
    void onRemoveClick(PlanEntity plan);

    void onMealClick(PlanEntity plan);
}
