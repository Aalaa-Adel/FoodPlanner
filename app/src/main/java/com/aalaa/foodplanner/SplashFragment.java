package com.aalaa.foodplanner;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class SplashFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        new Handler().postDelayed(() ->
                        NavHostFragment.findNavController(this)
                                .navigate(R.id.action_splash_to_onboarding)
                ,3000);

        return view;
    }
}
