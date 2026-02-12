package com.aalaa.foodplanner.data.local;

import android.content.Context;

import com.aalaa.foodplanner.datasource.db.AppDatabase;
import com.aalaa.foodplanner.datasource.db.FavoriteMealDao;
import com.aalaa.foodplanner.datasource.db.FavoriteMealEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class FavoritesLocalDataSource {

    private FavoriteMealDao favoritesDao;

    public FavoritesLocalDataSource(Context context) {
        this.favoritesDao = AppDatabase.getInstance(context).favoriteMealDao();
    }

    public Flowable<List<FavoriteMealEntity>> getFavorites() {
        return favoritesDao.getAllFavorites();
    }

    public Completable insertFavorite(FavoriteMealEntity favorite) {
        return favoritesDao.insertMeal(favorite);
    }

    public Completable deleteFavorite(FavoriteMealEntity favorite) {
        return favoritesDao.deleteMeal(favorite);
    }

    public Single<Boolean> isFavorite(String mealId) {
        return favoritesDao.isMealFavorite(mealId).map(v -> v != null && v == 1);
    }

    public Completable deleteAllFavorites() {
        return favoritesDao.deleteAllMeals();
    }


}
