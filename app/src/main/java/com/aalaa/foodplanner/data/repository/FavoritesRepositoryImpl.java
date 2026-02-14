package com.aalaa.foodplanner.data.repository;

import android.app.Application;

import com.aalaa.foodplanner.data.db.AppDatabase;
import com.aalaa.foodplanner.data.db.PendingActionDao;
import com.aalaa.foodplanner.data.local.FavoritesLocalDataSource;
import com.aalaa.foodplanner.data.db.FavoriteMealEntity;
import com.aalaa.foodplanner.data.db.PendingAction;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.aalaa.foodplanner.domain.repository.FavoritesRepository;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class FavoritesRepositoryImpl implements FavoritesRepository {

    private static volatile FavoritesRepositoryImpl instance;
    private final FavoritesLocalDataSource favoritesLocalDataSource;
    private final PendingActionDao pendingActionDao;

    public FavoritesRepositoryImpl(Application application) {
        this.favoritesLocalDataSource = new FavoritesLocalDataSource(application);
        this.pendingActionDao = AppDatabase.getInstance(application)
                .pendingActionDao();
    }

    public static FavoritesRepositoryImpl getInstance(Application application) {
        if (instance == null) {
            synchronized (FavoritesRepositoryImpl.class) {
                if (instance == null) {
                    instance = new FavoritesRepositoryImpl(application);
                }
            }
        }
        return instance;
    }

    @Override
    public Completable addToFavorites(MealsItem meal) {
        FavoriteMealEntity entity = new FavoriteMealEntity(meal);
        PendingAction action = new PendingAction(
                "INSERT",
                "FAV",
                meal.getIdMeal(),
                System.currentTimeMillis());

        return favoritesLocalDataSource.insertFavorite(entity)
                .andThen(pendingActionDao.insert(action));
    }

    @Override
    public Completable removeFromFavorites(MealsItem meal) {
        FavoriteMealEntity entity = new FavoriteMealEntity(meal);
        PendingAction action = new PendingAction(
                "DELETE",
                "FAV",
                meal.getIdMeal(),
                System.currentTimeMillis());
        return favoritesLocalDataSource.deleteFavorite(entity)
                .andThen(pendingActionDao.insert(action));
    }

    @Override
    public Flowable<List<MealsItem>> getFavorites() {
        return favoritesLocalDataSource.getFavorites()
                .map(entities -> {
                    List<MealsItem> list = new java.util.ArrayList<>(entities.size());
                    for (FavoriteMealEntity e : entities) {
                        list.add(e.getMeal());
                    }
                    return list;
                });
    }

    @Override
    public Single<Boolean> isFavorite(String id) {
        return favoritesLocalDataSource.isFavorite(id);
    }

    @Override
    public Completable clearFavorites() {
        return favoritesLocalDataSource.deleteAllFavorites();
    }

}
