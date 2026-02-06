package com.aalaa.foodplanner.data.firebase;

import android.content.Context;

import com.aalaa.foodplanner.data.firebase.auth.FirebaseAuthService;
import com.aalaa.foodplanner.data.firebase.auth.FirebaseAuthServiceImpl;
import com.aalaa.foodplanner.data.firebase.firestore.FirestoreService;
import com.aalaa.foodplanner.data.firebase.firestore.FirestoreServiceImpl;
import com.aalaa.foodplanner.data.local.SharedPreferencesHelper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseModule {
    private static volatile FirebaseModule instance;

    private final FirebaseAuthService authService;
    private final FirestoreService firestoreService;
    private final SharedPreferencesHelper preferencesHelper;
    private final UserSessionManager userSessionManager;

    private FirebaseModule(Context context) {
        FirebaseApp.initializeApp(context);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        preferencesHelper = new SharedPreferencesHelper(context);
        userSessionManager = new UserSessionManager();
        firestoreService = new FirestoreServiceImpl(firestore);
        authService = new FirebaseAuthServiceImpl(firebaseAuth);
    }

    public static void initialize(Context context) {
        if (instance == null) {
            synchronized (FirebaseModule.class) {
                if (instance == null) {
                    instance = new FirebaseModule(context.getApplicationContext());
                }
            }
        }
    }

    public static FirebaseModule getInstance() {
        if (instance == null) {
            throw new IllegalStateException("FirebaseModule not initialized. Call initialize() in Application class.");
        }
        return instance;
    }

    public FirebaseAuthService getAuthService() {
        return authService;
    }

    public FirestoreService getFirestoreService() {
        return firestoreService;
    }

    public SharedPreferencesHelper getPreferencesHelper() {
        return preferencesHelper;
    }

    public UserSessionManager getUserSessionManager() {
        return userSessionManager;
    }
}