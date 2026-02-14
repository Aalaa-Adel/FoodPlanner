package com.aalaa.foodplanner.data.firebase.auth;

import com.aalaa.foodplanner.domain.models.User;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface FirebaseAuthService {

    Single<User> signInWithEmail(String email, String password);

    Single<User> signUpWithEmail(String email, String password, String displayName);

    Single<User> signInWithGoogleToken(String idToken);

    Completable signOut();

    User getCurrentUser();

    boolean isUserLoggedIn();

    String getCurrentUserId();
}