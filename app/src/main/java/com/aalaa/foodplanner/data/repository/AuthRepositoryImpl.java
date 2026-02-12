package com.aalaa.foodplanner.data.repository;

import com.aalaa.foodplanner.data.firebase.FirebaseModule;
import com.aalaa.foodplanner.data.firebase.UserSessionManager;
import com.aalaa.foodplanner.data.firebase.auth.FirebaseAuthService;
import com.aalaa.foodplanner.data.firebase.firestore.FirestoreService;
import com.aalaa.foodplanner.data.local.SharedPreferencesHelper;
import com.aalaa.foodplanner.domain.auth.model.DayMeals;
import com.aalaa.foodplanner.domain.auth.model.MealPlan;
import com.aalaa.foodplanner.domain.auth.model.MealType;
import com.aalaa.foodplanner.domain.auth.model.User;
import com.aalaa.foodplanner.domain.auth.model.UserSettings;
import com.aalaa.foodplanner.domain.repository.AuthRepository;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class AuthRepositoryImpl implements AuthRepository {

    private final FirebaseAuthService authService;
    private final FirestoreService firestoreService;
    private final SharedPreferencesHelper preferencesHelper;
    private final UserSessionManager sessionManager;

    public AuthRepositoryImpl() {
        FirebaseModule module = FirebaseModule.getInstance();
        this.authService = module.getAuthService();
        this.firestoreService = module.getFirestoreService();
        this.preferencesHelper = module.getPreferencesHelper();
        this.sessionManager = module.getUserSessionManager();
    }

    @Override
    public Single<User> signInWithEmail(String email, String password) {
        return authService.signInWithEmail(email, password)
                .flatMap(this::onAuthSuccess);
    }

    @Override
    public Single<User> signUpWithEmail(String email, String password, String displayName) {
        return authService.signUpWithEmail(email, password, displayName)
                .flatMap(user -> firestoreService.createUser(user)
                        .andThen(Single.just(user))
                        .onErrorResumeNext(e -> Single.just(user)))
                .flatMap(this::onAuthSuccess);}



    @Override
    public Single<User> signInWithGoogle(String idToken) {
        return authService.signInWithGoogleToken(idToken)
                .flatMap(user -> firestoreService.createUser(user)
                        .andThen(Single.just(user))
                        .onErrorResumeNext(e -> Single.just(user)))
                .flatMap(this::onAuthSuccess);
    }

    @Override
    public Completable signOut() {
        return authService.signOut()
                .doOnComplete(() -> {
                    sessionManager.clearSession();
                    preferencesHelper.clearUserData();
                });
    }

    @Override
    public User getCurrentUser() {
        User cachedUser = sessionManager.getCurrentUser();
        if (cachedUser != null) {
            return cachedUser;
        }
        return authService.getCurrentUser();
    }

    @Override
    public boolean isLoggedIn() {
        return preferencesHelper.isLoggedIn() && authService.isUserLoggedIn();
    }

    @Override
    public String getCurrentUserId() {
        return authService.getCurrentUserId();
    }

    @Override
    public void setOnboardingCompleted(boolean completed) {
        preferencesHelper.setOnboardingCompleted(completed);
    }

    @Override
    public boolean isOnboardingCompleted() {
        return preferencesHelper.isOnboardingCompleted();
    }

    @Override
    public Single<UserSettings> getUserSettings() {
        String userId = authService.getCurrentUserId();
        if (userId == null) {
            return Single.error(new Exception("User not logged in"));
        }

        return firestoreService.getUserSettings(userId)
                .doOnSuccess(settings -> {
                    preferencesHelper.setDarkMode(settings.isDarkMode());
                    preferencesHelper.setLanguage(settings.getLanguage());

                    User currentUser = sessionManager.getCurrentUser();
                    if (currentUser != null) {
                        currentUser.setSettings(settings);
                    }
                })
                .onErrorResumeNext(e -> {
                    UserSettings cachedSettings = new UserSettings(
                            preferencesHelper.isDarkMode(),
                            preferencesHelper.getLanguage()
                    );
                    return Single.just(cachedSettings);
                });
    }

    @Override
    public Completable updateUserSettings(UserSettings settings) {
        String userId = authService.getCurrentUserId();
        if (userId == null) {
            return Completable.error(new Exception("User not logged in"));
        }

        preferencesHelper.setDarkMode(settings.isDarkMode());
        preferencesHelper.setLanguage(settings.getLanguage());

        User currentUser = sessionManager.getCurrentUser();
        if (currentUser != null) {
            currentUser.setSettings(settings);
        }

        return firestoreService.updateUserSettings(userId, settings);
    }

    @Override
    public Single<List<String>> getFavoriteMeals() {
        String userId = authService.getCurrentUserId();
        if (userId == null) {
            return Single.error(new Exception("User not logged in"));
        }

        return firestoreService.getFavoriteMeals(userId)
                .doOnSuccess(favorites -> {
                    User currentUser = sessionManager.getCurrentUser();
                    if (currentUser != null) {
                        currentUser.setFavoriteMealIds(favorites);
                    }
                });
    }

    @Override
    public Completable addFavoriteMeal(String mealId) {
        String userId = authService.getCurrentUserId();
        if (userId == null) {
            return Completable.error(new Exception("User not logged in"));
        }

        sessionManager.addFavoriteMeal(mealId);

        return firestoreService.addFavoriteMeal(userId, mealId)
                .doOnError(e -> sessionManager.removeFavoriteMeal(mealId));
    }

    @Override
    public Completable removeFavoriteMeal(String mealId) {
        String userId = authService.getCurrentUserId();
        if (userId == null) {
            return Completable.error(new Exception("User not logged in"));
        }

        sessionManager.removeFavoriteMeal(mealId);

        return firestoreService.removeFavoriteMeal(userId, mealId)
                .doOnError(e -> sessionManager.addFavoriteMeal(mealId));
    }

    @Override
    public boolean isFavorite(String mealId) {
        return sessionManager.isFavorite(mealId);
    }



    private Single<User> onAuthSuccess(User user) {
        preferencesHelper.setLoggedIn(true);
        preferencesHelper.setUserId(user.getUid());

        return firestoreService.getUser(user.getUid())
                .doOnSuccess(fullUser -> {
                    sessionManager.setCurrentUser(fullUser);

                    if (fullUser.getSettings() != null) {
                        preferencesHelper.setDarkMode(fullUser.getSettings().isDarkMode());
                        preferencesHelper.setLanguage(fullUser.getSettings().getLanguage());
                    }
                })
                .onErrorResumeNext(e -> {
                    sessionManager.setCurrentUser(user);
                    return Single.just(user);
                });
    }
}