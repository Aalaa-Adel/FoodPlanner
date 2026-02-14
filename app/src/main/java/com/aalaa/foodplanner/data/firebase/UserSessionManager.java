package com.aalaa.foodplanner.data.firebase;

import com.aalaa.foodplanner.domain.models.MealPlan;
import com.aalaa.foodplanner.domain.models.User;
import com.aalaa.foodplanner.domain.models.UserSettings;

import java.util.ArrayList;
import java.util.List;

public class UserSessionManager {

    private User currentUser;
    private MealPlan mealPlan;
    private boolean isInitialized = false;

    public UserSessionManager() {
    }

    public synchronized void setCurrentUser(User user) {
        this.currentUser = user;
        this.isInitialized = true;
    }

    public synchronized User getCurrentUser() {
        return currentUser;
    }

    public synchronized void addFavoriteMeal(String mealId) {
        if (currentUser != null) {
            if (currentUser.getFavoriteMealIds() == null) {
                currentUser.setFavoriteMealIds(new ArrayList<>());
            }
            if (!currentUser.getFavoriteMealIds().contains(mealId)) {
                currentUser.getFavoriteMealIds().add(mealId);
            }
        }
    }

    public synchronized void removeFavoriteMeal(String mealId) {
        if (currentUser != null && currentUser.getFavoriteMealIds() != null) {
            currentUser.getFavoriteMealIds().remove(mealId);
        }
    }

    public synchronized boolean isFavorite(String mealId) {
        if (currentUser != null && currentUser.getFavoriteMealIds() != null) {
            return currentUser.getFavoriteMealIds().contains(mealId);
        }
        return false;
    }


    public synchronized void clearSession() {
        currentUser = null;
        mealPlan = null;
        isInitialized = false;
    }
}