package com.aalaa.foodplanner.ui.launcher;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.aalaa.foodplanner.MainActivity;
import com.aalaa.foodplanner.data.local.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = SessionManager.getInstance(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        boolean isGuest = sessionManager.isGuest();
        boolean isLoggedIn = currentUser != null;

        Intent intent = new Intent(this, MainActivity.class);

        if (isLoggedIn || isGuest) {
            // User is logged in OR is a guest -> Go to Main (Home)
            intent.putExtra("TARGET_DEST", "HOME");
        } else {
            // Not logged in AND not guest -> Go to Auth flow
            intent.putExtra("TARGET_DEST", "AUTH");
        }

        startActivity(intent);
        finish();
    }
}
