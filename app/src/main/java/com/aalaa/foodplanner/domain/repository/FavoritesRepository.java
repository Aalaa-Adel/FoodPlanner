package com.aalaa.foodplanner.domain.repository;

import com.aalaa.foodplanner.domain.models.MealsItem;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface FavoritesRepository {
    Completable addToFavorites(MealsItem meal);

    Completable removeFromFavorites(MealsItem meal);

    Flowable<List<MealsItem>> getFavorites();

    Single<Boolean> isFavorite(String id);

    Completable clearFavorites();
}
