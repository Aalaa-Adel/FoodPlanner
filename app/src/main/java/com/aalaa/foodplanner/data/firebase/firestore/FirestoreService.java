package com.aalaa.foodplanner.data.firebase.firestore;

import com.aalaa.foodplanner.domain.auth.model.DayMeals;
import com.aalaa.foodplanner.domain.auth.model.MealPlan;
import com.aalaa.foodplanner.domain.auth.model.MealType;
import com.aalaa.foodplanner.domain.auth.model.User;
import com.aalaa.foodplanner.domain.auth.model.UserSettings;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface FirestoreService {

    Completable createUser(User user);

    Single<User> getUser(String userId);

    Completable updateUserProfile(String userId, String displayName, String photoUrl);

    Completable deleteUser(String userId);

    Single<UserSettings> getUserSettings(String userId);

    Completable updateUserSettings(String userId, UserSettings settings);

    Single<List<String>> getFavoriteMeals(String userId);

    Completable addFavoriteMeal(String userId, String mealId);

    Completable removeFavoriteMeal(String userId, String mealId);

    Completable setFavoriteMeals(String userId, List<String> mealIds);

    Single<MealPlan> getMealPlan(String userId);

    Single<DayMeals> getDayMeals(String userId, String date);

    Completable setDayMeals(String userId, String date, DayMeals dayMeals);

    Completable removeDayMeals(String userId, String date);

    Completable setMealForDay(String userId, String date, MealType mealType, String mealId);

    Completable removeMealFromDay(String userId, String date, MealType mealType);
}