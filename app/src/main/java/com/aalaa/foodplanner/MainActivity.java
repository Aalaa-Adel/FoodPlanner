package com.aalaa.foodplanner;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.aalaa.foodplanner.data.firebase.FirebaseModule;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    private static final Set<Integer> TOP_LEVEL_DESTINATIONS = new HashSet<>();

    static {
        TOP_LEVEL_DESTINATIONS.add(R.id.homeFragment);
        TOP_LEVEL_DESTINATIONS.add(R.id.searchFragment);
        TOP_LEVEL_DESTINATIONS.add(R.id.favoriteFragment);
        TOP_LEVEL_DESTINATIONS.add(R.id.planFragment);
        TOP_LEVEL_DESTINATIONS.add(R.id.profileFragment);
        TOP_LEVEL_DESTINATIONS.add(R.id.categoriesFragment);
        TOP_LEVEL_DESTINATIONS.add(R.id.countriesFragment);
    }

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(com.aalaa.foodplanner.ui.common.LanguageHelper.onAttach(newBase));
    }

    private io.reactivex.rxjava3.disposables.CompositeDisposable disposables = new io.reactivex.rxjava3.disposables.CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.aalaa.foodplanner.data.local.SharedPreferencesHelper prefs = new com.aalaa.foodplanner.data.local.SharedPreferencesHelper(
                this);
        if (prefs.isDarkMode()) {
            androidx.appcompat.app.AppCompatDelegate
                    .setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            androidx.appcompat.app.AppCompatDelegate
                    .setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        }

        FirebaseModule.initialize(this);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host);

        navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (TOP_LEVEL_DESTINATIONS.contains(destination.getId())) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            } else {
                bottomNavigationView.setVisibility(View.GONE);
            }
        });

        setupSyncObserver();

        if (savedInstanceState == null) {
            String target = getIntent().getStringExtra("TARGET_DEST");

            if ("AUTH".equals(target)) {
                navController.navigate(R.id.auth_graph, null,
                        new androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.root_graph, true)
                                .build());
            } else if ("HOME".equals(target)) {
                navController.navigate(R.id.main_graph, null,
                        new androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.root_graph, true)
                                .build());
            }

            getIntent().removeExtra("TARGET_DEST");
        }


    }

    private void setupSyncObserver() {
        com.aalaa.foodplanner.data.network.ConnectivityObserver connectivityObserver = com.aalaa.foodplanner.data.network.ConnectivityObserver
                .getInstance(this);

        com.aalaa.foodplanner.data.repository.FavoritesRepositoryImpl favRepo = com.aalaa.foodplanner.data.repository.FavoritesRepositoryImpl
                .getInstance(getApplication());
        com.aalaa.foodplanner.data.repository.PlanRepositoryImpl planRepo = com.aalaa.foodplanner.data.repository.PlanRepositoryImpl
                .getInstance(getApplication());
        com.aalaa.foodplanner.data.datasource.remote.MealRemoteDataSource remoteSource = com.aalaa.foodplanner.data.datasource.remote.MealRemoteDataSource
                .getInstance();
        com.aalaa.foodplanner.data.repository.MealRepositoryImpl mealRepo = com.aalaa.foodplanner.data.repository.MealRepositoryImpl
                .getInstance(remoteSource);
        com.aalaa.foodplanner.datasource.db.AppDatabase db = com.aalaa.foodplanner.datasource.db.AppDatabase
                .getInstance(getApplication());

        com.aalaa.foodplanner.data.repository.SyncRepositoryImpl syncRepo = com.aalaa.foodplanner.data.repository.SyncRepositoryImpl
                .getInstance(favRepo, planRepo, mealRepo, db.pendingActionDao());

        disposables.add(
                connectivityObserver.observeNetwork()
                        .distinctUntilChanged()
                        .filter(isConnected -> isConnected) // Only when coming online
                        .switchMapCompletable(isConnected -> syncRepo.processPendingActions())
                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                        .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> android.util.Log.d("MainActivity", "Pending actions processed"),
                                throwable -> android.util.Log.e("MainActivity", "Error processing pending actions",
                                        throwable)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

}