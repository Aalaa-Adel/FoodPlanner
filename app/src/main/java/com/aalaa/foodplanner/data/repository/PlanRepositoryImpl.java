package com.aalaa.foodplanner.data.repository;

import android.app.Application;

import com.aalaa.foodplanner.data.local.PlansLocalDataSource;
import com.aalaa.foodplanner.data.db.AppDatabase;
import com.aalaa.foodplanner.data.db.PendingAction;
import com.aalaa.foodplanner.data.db.PendingActionDao;
import com.aalaa.foodplanner.data.db.PlanEntity;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.aalaa.foodplanner.domain.repository.PlanRepository;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public class PlanRepositoryImpl implements PlanRepository {

    private static volatile PlanRepositoryImpl instance;

    private final PlansLocalDataSource localDataSource;
    private final PendingActionDao pendingActionDao;

    private PlanRepositoryImpl(Application application) {
        this.localDataSource = new PlansLocalDataSource(application);
        this.pendingActionDao = AppDatabase.getInstance(application).pendingActionDao();
    }

    public static PlanRepositoryImpl getInstance(Application application) {
        if (instance == null) {
            synchronized (PlanRepositoryImpl.class) {
                if (instance == null) {
                    instance = new PlanRepositoryImpl(application);
                }
            }
        }
        return instance;
    }

    @Override
    public Completable addToPlan(MealsItem meal, String day, String slot) {
        PlanEntity entity = new PlanEntity(day, slot, meal.getIdMeal(), meal);

        PendingAction action = new PendingAction(
                "INSERT",
                "PLAN",
                buildPlanPayload(day, slot, meal.getIdMeal()),
                System.currentTimeMillis()
        );

        return localDataSource.insertPlan(entity)
                .andThen(pendingActionDao.insert(action));
    }

    @Override
    public Completable removeMealFromPlan(PlanEntity plan) {
        PendingAction action = new PendingAction(
                "DELETE",
                "PLAN",
                buildPlanPayload(
                        plan.getDate(),
                        plan.getSlot(),
                        plan.getMealId()
                ),
                System.currentTimeMillis()
        );

        return localDataSource.deletePlan(plan)
                .andThen(pendingActionDao.insert(action));
    }

    @Override
    public Flowable<List<PlanEntity>> getAllPlans() {
        return localDataSource.getAllPlans();
    }

    @Override
    public Completable clearAllPlans() {
        return localDataSource.clearAllPlans();
    }

    private String buildPlanPayload(String day, String slot, String mealId) {
        return day + "|" + slot.toLowerCase() + "|" + mealId;
    }
}
