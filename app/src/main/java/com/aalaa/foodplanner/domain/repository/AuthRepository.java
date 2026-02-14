package com.aalaa.foodplanner.domain.repository;

import com.aalaa.foodplanner.domain.models.User;
import com.aalaa.foodplanner.domain.models.UserSettings;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
public interface AuthRepository {
    Single<User> signInWithEmail(String email, String password);
    Single<User> signUpWithEmail(String email, String password, String displayName);
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
}