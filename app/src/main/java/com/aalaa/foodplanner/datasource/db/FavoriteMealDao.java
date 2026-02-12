package com.aalaa.foodplanner.datasource.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface FavoriteMealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMeal(FavoriteMealEntity meal);

    @Delete
    Completable deleteMeal(FavoriteMealEntity meal);

    @Query("SELECT * FROM favorite_meals")
    Flowable<List<FavoriteMealEntity>> getAllFavorites();

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_meals WHERE idMeal = :idMeal LIMIT 1)")
    Single<Integer> isMealFavorite(String idMeal);

    @Query("DELETE FROM favorite_meals")
    Completable deleteAllMeals();
}
