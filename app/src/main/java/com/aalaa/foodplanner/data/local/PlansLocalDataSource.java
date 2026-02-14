package com.aalaa.foodplanner.data.local;

import android.content.Context;

import com.aalaa.foodplanner.data.db.AppDatabase;
import com.aalaa.foodplanner.data.db.PlanDao;
import com.aalaa.foodplanner.data.db.PlanEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public class PlansLocalDataSource {

    private PlanDao planDao;

    public PlansLocalDataSource(Context context) {
        this.planDao = AppDatabase.getInstance(context).planDao();
    }

    public Completable insertPlan(PlanEntity plan) {
        return planDao.insertPlan(plan);
    }

    public Completable deletePlan(PlanEntity plan) {
        return planDao.deletePlan(plan);
    }

    public Flowable<List<PlanEntity>> getPlansByDate(String date) {
        return planDao.getPlansByDate(date);
    }

    public Flowable<List<PlanEntity>> getAllPlans() {
        return planDao.getAllPlans();
    }

    public Completable clearAllPlans() {
        return planDao.clearAllPlans();
    }
}
