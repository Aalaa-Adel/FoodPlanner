package com.aalaa.foodplanner;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.aalaa.foodplanner.data.local.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler().postDelayed(() -> {
            if (isAdded()) {
                checkAuthAndNavigate();
            }
        }, 5000);
    }

    private void checkAuthAndNavigate() {
        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        boolean isGuest = sessionManager.isGuest();
        boolean isLoggedIn = currentUser != null;

        if (isLoggedIn || isGuest) {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_splash_to_main);
        } else {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_splash_to_auth);
        }
    }
}
