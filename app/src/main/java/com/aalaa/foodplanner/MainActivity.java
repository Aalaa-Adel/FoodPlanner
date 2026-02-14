package com.aalaa.foodplanner;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.aalaa.foodplanner.data.datasource.remote.MealRemoteDataSource;
import com.aalaa.foodplanner.data.db.AppDatabase;
import com.aalaa.foodplanner.data.firebase.FirebaseModule;
import com.aalaa.foodplanner.data.local.SharedPreferencesHelper;
import com.aalaa.foodplanner.data.network.ConnectivityObserver;
import com.aalaa.foodplanner.data.repository.FavoritesRepositoryImpl;
import com.aalaa.foodplanner.data.repository.MealRepositoryImpl;
import com.aalaa.foodplanner.data.repository.PlanRepositoryImpl;
import com.aalaa.foodplanner.data.repository.SyncRepositoryImpl;
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

                SharedPreferencesHelper prefs = new SharedPreferencesHelper(
                                this);
                if (prefs.isDarkMode()) {
                        AppCompatDelegate
                                        .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                       AppCompatDelegate
                                        .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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

        }

        private void setupSyncObserver() {
                ConnectivityObserver connectivityObserver =ConnectivityObserver
                                .getInstance(this);

                FavoritesRepositoryImpl favRepo = FavoritesRepositoryImpl
                                .getInstance(getApplication());
                PlanRepositoryImpl planRepo = PlanRepositoryImpl
                                .getInstance(getApplication());
                MealRemoteDataSource remoteSource = MealRemoteDataSource
                                .getInstance();
                MealRepositoryImpl mealRepo = MealRepositoryImpl
                                .getInstance(remoteSource);
                AppDatabase db = AppDatabase
                                .getInstance(getApplication());

                SyncRepositoryImpl syncRepo =SyncRepositoryImpl
                                .getInstance(favRepo, planRepo, mealRepo, db.pendingActionDao());

                disposables.add(
                                connectivityObserver.observeNetwork()
                                                .distinctUntilChanged()
                                                .filter(isConnected -> isConnected)
                                                .switchMapCompletable(isConnected -> syncRepo.processPendingActions())
                                                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                                                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
                                                                .mainThread())
                                                .subscribe(
                                                                () -> android.util.Log.d("MainActivity",
                                                                                "Pending actions processed"),
                                                                throwable -> android.util.Log.e("MainActivity",
                                                                                "Error processing pending actions",
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