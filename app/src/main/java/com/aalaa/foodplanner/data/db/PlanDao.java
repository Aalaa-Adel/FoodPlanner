package com.aalaa.foodplanner.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface PlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPlan(PlanEntity plan);

    @Delete
    Completable deletePlan(PlanEntity plan);

    @Query("SELECT * FROM plan_meals WHERE date = :date")
    Flowable<List<PlanEntity>> getPlansByDate(String date);

    @Query("SELECT * FROM plan_meals")
    Flowable<List<PlanEntity>> getAllPlans();

    @Query("DELETE FROM plan_meals")
    Completable clearAllPlans();
}
