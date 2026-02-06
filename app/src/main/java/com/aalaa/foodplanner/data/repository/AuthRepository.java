package com.aalaa.foodplanner.data.repository;

import com.aalaa.foodplanner.domain.auth.model.DayMeals;
import com.aalaa.foodplanner.domain.auth.model.MealPlan;
import com.aalaa.foodplanner.domain.auth.model.MealType;
import com.aalaa.foodplanner.domain.auth.model.User;
import com.aalaa.foodplanner.domain.auth.model.UserSettings;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface AuthRepository {

    Single<User> signInWithEmail(String email, String password);

    Single<User> signUpWithEmail(String email, String password);

    Single<User> signInWithGoogle(String idToken);

    Completable signOut();

    User getCurrentUser();

    boolean isLoggedIn();

    String getCurrentUserId();

    void setOnboardingCompleted(boolean completed);

    boolean isOnboardingCompleted();

    Single<UserSettings> getUserSettings();

    Completable updateUserSettings(UserSettings settings);

    Single<List<String>> getFavoriteMeals();

    Completable addFavoriteMeal(String mealId);

    Completable removeFavoriteMeal(String mealId);

    boolean isFavorite(String mealId);

    Single<MealPlan> getMealPlan();

    Single<DayMeals> getDayMeals(String date);

    Completable setDayMeals(String date, DayMeals dayMeals);

    Completable setMealForDay(String date, MealType mealType, String mealId);

    Completable removeMealFromDay(String date, MealType mealType);
}