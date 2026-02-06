package com.aalaa.foodplanner;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.domain.auth.model.model.Area;
import com.aalaa.foodplanner.domain.auth.model.model.Category;
import com.aalaa.foodplanner.domain.auth.model.model.MealsItem;
import com.aalaa.foodplanner.datasource.remote.AreaNetworkResponse;
import com.aalaa.foodplanner.datasource.remote.AreaRemoteDataSource;
import com.aalaa.foodplanner.datasource.remote.CategoryNetworkResponse;
import com.aalaa.foodplanner.datasource.remote.CategoryRemoteDataSource;
import com.aalaa.foodplanner.datasource.remote.MealsNetworkResponse;
import com.aalaa.foodplanner.datasource.remote.MealsRemoteDataSource;

import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView rvForYou;
    RecyclerView rvCategories;
    RecyclerView rvCountries;
    MealAdapter mealAdapter;
    CategoryAdapter categoryAdapter;
    CountryAdapter countryAdapter;
    MealsRemoteDataSource mealsRemoteDataSource;
    CategoryRemoteDataSource categoryRemoteDataSource;

    AreaRemoteDataSource areaRemoteDataSource;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvForYou = view.findViewById(R.id.rv_for_you);
        rvCategories = view.findViewById(R.id.rv_categories);
        rvCountries = view.findViewById(R.id.rv_countries);

        rvForYou.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        rvCountries.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));

        mealAdapter = new MealAdapter();
        rvForYou.setAdapter(mealAdapter);

        categoryAdapter = new CategoryAdapter();
        rvCategories.setAdapter(categoryAdapter);

        countryAdapter = new CountryAdapter();
        rvCountries.setAdapter(countryAdapter);

        mealsRemoteDataSource = new MealsRemoteDataSource();

        mealsRemoteDataSource.getMeals(new MealsNetworkResponse() {
            @Override
            public void onMealsSuccess(List<MealsItem> meals) {
                rvForYou.setVisibility(VISIBLE);
                mealAdapter.setMeals(meals);
            }

            @Override
            public void noInternet() {
            }

            @Override
            public void onFailure(String message) {
            }
        });

        categoryRemoteDataSource = new CategoryRemoteDataSource();

        categoryRemoteDataSource.getCategories(new CategoryNetworkResponse() {
            @Override
            public void onCategoriesSuccess(List<Category> categories) {
                rvCategories.setVisibility(VISIBLE);
                categoryAdapter.setCategoryList(categories);
            }

            @Override
            public void noInternet() {
            }

            @Override
            public void onFailure(String message) {
            }
        });

        areaRemoteDataSource = new AreaRemoteDataSource();

        areaRemoteDataSource.getArea(new AreaNetworkResponse() {
            @Override
            public void onAreasSuccess(List<Area> areas) {
                rvCountries.setVisibility(VISIBLE);
                countryAdapter.setAreas(areas);
            }

            @Override
            public void noInternet() {
            }

            @Override
            public void onFailure(String message) {
            }
        });

        return view;
    }

    private void navigateToRecipeDetail(String recipeId) {
        Bundle args = new Bundle();
        args.putString("recipeId", recipeId);

        // Get the main NavController from the Activity
        NavController navController = Navigation.findNavController(
                requireActivity(),
                R.id.nav_host
        );

        // Navigate from HomeContainerFragment to RecipeDetailFragment
        navController.navigate(R.id.action_home_to_recipe_detail, args);
    }
}


//    private void setupClickListeners(View view) {
//        if (searchBar != null) {
//            searchBar.setOnClickListener(v -> {
//                // TODO: navigate to search screen
//            });
//        }
//
//        View mealOfDayCard = view.findViewById(R.id.meal_of_day_card);
//        if (mealOfDayCard != null) {
//            mealOfDayCard.setOnClickListener(v -> {
//                // Navigate to meal details
//                navigateToRecipeDetail("meal_of_day_1");
//            });
//        }
//
//        setupCategoryClicks(view);
//        setupCountryClicks(view);
//    }
//
//    private void setupCategoryClicks(View view) {
//        int[] categoryIds = {
//                R.id.category_vegan,
//                R.id.category_dessert,
//                R.id.category_seafood,
//                R.id.category_italian
//        };
//        String[] categoryNames = {"Vegan", "Dessert", "Seafood", "Italian"};
//
//        for (int i = 0; i < categoryIds.length; i++) {
//            View category = view.findViewById(categoryIds[i]);
//            String name = categoryNames[i];
//            if (category != null) {
//                category.setOnClickListener(v -> navigateToCategory(name));
//            }
//        }
//    }
//
//    private void setupCountryClicks(View view) {
//        int[] countryIds = {
//                R.id.country_italy,
//                R.id.country_japan,
//                R.id.country_mexico,
//                R.id.country_india
//        };
//        String[] countryNames = {"Italy", "Japan", "Mexico", "India"};
//
//        for (int i = 0; i < countryIds.length; i++) {
//            View country = view.findViewById(countryIds[i]);
//            String name = countryNames[i];
//            if (country != null) {
//                country.setOnClickListener(v -> navigateToCountry(name));
//            }
//        }
//    }
//
//    private void navigateToRecipeDetail(String recipeId) {
//        Bundle args = new Bundle();
//        args.putString("recipeId", recipeId);
//
//        Navigation.findNavController(requireView())
//                .navigate(R.id.action_home_to_recipe_detail, args);
//    }
//
//    private void navigateToCategory(String category) {
//        // TODO: implement navigation to category screen
//    }
//
//    private void navigateToCountry(String country) {
//        // TODO: implement navigation to country screen
//    }
//}