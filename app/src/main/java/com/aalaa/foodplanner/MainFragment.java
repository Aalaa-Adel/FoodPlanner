package com.aalaa.foodplanner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        BottomNavigationView bottomNav = view.findViewById(R.id.bottomNav);

        NavHostFragment navHost =
                (NavHostFragment) getChildFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        NavController controller = navHost.getNavController();

        NavigationUI.setupWithNavController(bottomNav, controller);

        return view;
    }
}
