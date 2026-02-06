package com.aalaa.foodplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipeDetailFragment extends Fragment {

    // Views
    private CollapsingToolbarLayout collapsingToolbar;
    private ViewPager2 viewPagerImages;
    private CardView btnBack;
    private CardView btnFavorite;
    private ImageView ivFavorite;
    private TextView tvCategory;
    private TextView tvCountry;
    private TextView tvRating;
    private TextView tvRecipeName;
    private TextView tvTime;
    private TextView tvServings;
    private TextView tvCalories;
    private TabLayout tabLayout;
    private ViewPager2 viewPagerContent;
    private CardView btnAddToMealPlan;

    // Data
    private boolean isFavorite = false;
    private String recipeId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get recipe ID from arguments
        if (getArguments() != null) {
            recipeId = getArguments().getString("recipeId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        initViews(view);
        setupClickListeners(view);
        setupTabs();
        loadRecipeData();

        return view;
    }

    private void initViews(View view) {
        collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);
        viewPagerImages = view.findViewById(R.id.viewpager_images);
        btnBack = view.findViewById(R.id.btn_back);
        btnFavorite = view.findViewById(R.id.btn_favorite);
        ivFavorite = view.findViewById(R.id.iv_favorite);
        tvCategory = view.findViewById(R.id.tv_category);
        tvCountry = view.findViewById(R.id.tv_country);
        tvRating = view.findViewById(R.id.tv_rating);
        tvRecipeName = view.findViewById(R.id.tv_recipe_name);
        tvTime = view.findViewById(R.id.tv_time);
        tvServings = view.findViewById(R.id.tv_servings);
        tvCalories = view.findViewById(R.id.tv_calories);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPagerContent = view.findViewById(R.id.viewpager_content);
//        btnAddToMealPlan = view.findViewById(R.id.btn_add_to_meal_plan);
    }

    private void setupClickListeners(View view) {
        btnBack.setOnClickListener(v -> navigateBack(view));

        btnFavorite.setOnClickListener(v -> toggleFavorite());

//        btnAddToMealPlan.setOnClickListener(v -> addToMealPlan());
    }

    private void setupTabs() {
        // Create ViewPager adapter for tabs
        RecipeTabsAdapter adapter = new RecipeTabsAdapter(this);
        viewPagerContent.setAdapter(adapter);

        // Connect TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, viewPagerContent,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Ingredients");
                            break;
                        case 1:
                            tab.setText("Steps");
                            break;
                        case 2:
                            tab.setText("Video");
                            break;
                    }
                }
        ).attach();
    }

    private void loadRecipeData() {
        // TODO: Load recipe data from database/API using recipeId
        // For now, using sample data

        tvCategory.setText("PASTA");
        tvCountry.setText("Italy");
        tvRating.setText("4.9");
        tvRecipeName.setText("Truffle Mushroom Pasta");
        tvTime.setText("25 Min");
        tvServings.setText("2 Ppl");
        tvCalories.setText("450");

        // Load recipe images
        // TODO: Setup image slider adapter
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;

        if (isFavorite) {
            ivFavorite.setImageResource(R.drawable.ic_heart_filled);
            // TODO: Save to favorites
        } else {
            ivFavorite.setImageResource(R.drawable.ic_heart_outline);
            // TODO: Remove from favorites
        }
    }

    private void addToMealPlan() {
        // TODO: Show date picker and add recipe to meal plan
        // For now, just show a toast
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(),
                    "Added to Meal Plan",
                    android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateBack(View view) {
        // Use Navigation Component to go back
        Navigation.findNavController(view).navigateUp();
    }
}