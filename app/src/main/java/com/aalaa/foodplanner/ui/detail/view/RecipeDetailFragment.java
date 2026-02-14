package com.aalaa.foodplanner.ui.detail.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.aalaa.foodplanner.ui.common.AppSnack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.data.repository.FavoritesRepositoryImpl;
import com.aalaa.foodplanner.data.repository.PlanRepositoryImpl;
import com.aalaa.foodplanner.data.db.PlanEntity;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.aalaa.foodplanner.data.datasource.remote.MealRemoteDataSource;
import com.aalaa.foodplanner.data.repository.MealRepositoryImpl;
import com.aalaa.foodplanner.ui.detail.presenter.RecipeDetailPresenterImpl;
import com.aalaa.foodplanner.ui.favorites.presenter.FavoritesPresenterImp;
import com.aalaa.foodplanner.ui.favorites.view.FavView;
import com.aalaa.foodplanner.ui.plans.presenter.PlanPresenterImpl;
import com.aalaa.foodplanner.ui.plans.view.AddToPlanDialog;
import com.aalaa.foodplanner.ui.plans.view.PlanView;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class RecipeDetailFragment extends Fragment implements DetailView, FavView, PlanView {

    private static final String TAG = "RecipeDetailFragment";

    private CollapsingToolbarLayout collapsingToolbar;
    private CardView btnBack;
    private CardView btnFavorite;
    private ImageView ivFavorite;
    private TextView tvCategory;
    private TextView tvCountry;
    private TextView tvRecipeName;
    private TabLayout tabLayout;
    private ViewPager2 viewPagerContent;
    private View btnAddToMealPlan;
    private ImageView imageView;

    private boolean isFavorite = false;
    private String recipeId;
    private MealsItem currentMeal;

    private RecipeDetailPresenterImpl presenter;
    private FavoritesPresenterImp favoritesPresenter;
    private PlanPresenterImpl planPresenter;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            RecipeDetailFragmentArgs args = RecipeDetailFragmentArgs.fromBundle(getArguments());
            currentMeal = args.getMeal();

            if (currentMeal != null) {
                recipeId = currentMeal.getIdMeal();
                Log.d(TAG, "Received full meal object: " + currentMeal.getStrMeal());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        initViews(root);
        setupTabs();
        setupClickListeners(root);

        MealRepositoryImpl mealRepo = MealRepositoryImpl.getInstance(MealRemoteDataSource.getInstance());
        FavoritesRepositoryImpl favRepo = FavoritesRepositoryImpl.getInstance(requireActivity().getApplication());

        presenter = new RecipeDetailPresenterImpl(this, mealRepo, favRepo);

        PlanRepositoryImpl planRepo = PlanRepositoryImpl.getInstance(requireActivity().getApplication());
        planPresenter = new PlanPresenterImpl(planRepo, this);

        loadRecipeData();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null)
            presenter.dispose();
        if (planPresenter != null)
            planPresenter.dispose();
    }

    private void initViews(View view) {
        collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);
        imageView = view.findViewById(R.id.meal_image);
        btnBack = view.findViewById(R.id.btn_back);
        btnFavorite = view.findViewById(R.id.btn_favorite);
        ivFavorite = view.findViewById(R.id.iv_favorite);
        tvCategory = view.findViewById(R.id.tv_category);
        tvCountry = view.findViewById(R.id.tv_country);
        tvRecipeName = view.findViewById(R.id.tv_recipe_name);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPagerContent = view.findViewById(R.id.viewpager_content);
        btnAddToMealPlan = view.findViewById(R.id.btn_add_to_meal_plan);
        progressBar = view.findViewById(R.id.progressBar);

    }

    private void setupClickListeners(View view) {
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        btnFavorite.setOnClickListener(v -> {
            btnFavorite.setEnabled(false);
            presenter.onFavoriteClicked();
        });

        if (btnAddToMealPlan != null) {
            btnAddToMealPlan.setOnClickListener(v -> addToMealPlan());
        }
    }

    private void setupTabs() {
        RecipeTabsAdapter adapter = new RecipeTabsAdapter(this);
        viewPagerContent.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPagerContent, (tab, position) -> {
            if (position == 0)
                tab.setText("Ingredients");
            else if (position == 1)
                tab.setText("Steps");
            else
                tab.setText("Video");
        }).attach();
    }

    private void loadRecipeData() {
        if (recipeId != null && !recipeId.trim().isEmpty()) {
            presenter.loadMeal(recipeId);
        } else if (currentMeal != null) {
            showRecipe(currentMeal);
            presenter.loadMeal(currentMeal.getIdMeal());
        } else {
            showError("No recipe provided");
        }
    }

    @Override
    public void showRecipe(MealsItem meal) {
        currentMeal = meal;
        updateUI(meal);
        notifyChildren();
    }

    @Override
    public void renderFavorite(boolean isFav) {
        ivFavorite.setImageResource(isFav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        btnFavorite.setEnabled(true);
    }

    @Override
    public void showError(String message) {
        btnFavorite.setEnabled(true);
        AppSnack.showError(getView() != null ? getView() : requireActivity().findViewById(android.R.id.content),
                message);
    }

    @Override
    public void showLoading() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }

    private void updateUI(MealsItem meal) {
        if (meal == null)
            return;

        tvCategory.setText(meal.getStrCategory() != null ? meal.getStrCategory() : "");
        tvCountry.setText(meal.getStrArea() != null ? meal.getStrArea() : "");
        tvRecipeName.setText(meal.getStrMeal() != null ? meal.getStrMeal() : "");

        Glide.with(requireContext())
                .load(meal.getStrMealThumb())
                .placeholder(R.drawable.avatar_placeholder)
                .error(R.drawable.avatar_placeholder)
                .into(imageView);
    }

    private void addToMealPlan() {
        if (currentMeal != null) {
            AddToPlanDialog dialog = new AddToPlanDialog(
                    requireContext(),
                    currentMeal,
                    (meal, day, slot) -> planPresenter.addToPlan(meal, day, slot));
            dialog.show();
        }
    }

    private void notifyChildren() {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof VideoFragment)
                ((VideoFragment) fragment).updateVideo(currentMeal);
            if (fragment instanceof StepsFragment)
                ((StepsFragment) fragment).updateSteps(currentMeal);
            if (fragment instanceof IngredientsFragment)
                ((IngredientsFragment) fragment).updateIngredients(currentMeal);
        }
    }

    public MealsItem getCurrentMeal() {
        return currentMeal;
    }

    @Override
    public void showFavorites(List<MealsItem> favorites) {
    }

    @Override
    public void showEmptyState() {
    }

    @Override
    public void showAddedToFavorites() {
        btnFavorite.setEnabled(true);
        AppSnack.showSuccess(requireView(), "Added to Favorites ❤️");
    }

    @Override
    public void showRemovedFromFavorites() {
        btnFavorite.setEnabled(true);
        AppSnack.showInfo(requireView(), "Removed from Favorites 💔");
    }

    @Override
    public void updateFavoriteIcon(boolean isFavorite) {
        this.isFavorite = isFavorite;
        ivFavorite.setImageResource(
                isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
    }

    @Override
    public void showPlans(List<PlanEntity> plans) {
    }

    @Override
    public void showAddedToPlan() {
        AppSnack.showSuccess(requireView(), "Added to Meal Plan! 📅");
    }

    @Override
    public void showRemovedFromPlan() {
        AppSnack.showInfo(requireView(), "Removed from Meal Plan");
    }
}