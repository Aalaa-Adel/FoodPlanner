
package com.aalaa.foodplanner.ui.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aalaa.foodplanner.ui.categories.view.CategoryAdapter;
import com.aalaa.foodplanner.ui.common.AppSnack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.data.repository.FavoritesRepositoryImpl;
import com.aalaa.foodplanner.datasource.db.AppDatabase;
import com.aalaa.foodplanner.data.datasource.remote.MealRemoteDataSource;
import com.aalaa.foodplanner.domain.models.Area;
import com.aalaa.foodplanner.domain.models.Category;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.aalaa.foodplanner.data.repository.MealRepositoryImpl;
import com.aalaa.foodplanner.ui.countries.view.CountryAdapter;
import com.aalaa.foodplanner.ui.favorites.presenter.FavoritesPresenterImp;
import com.aalaa.foodplanner.ui.favorites.view.FavView;
import com.aalaa.foodplanner.ui.home.presenter.HomePresenterImpl;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements HomeView, FavView, OnHomeMealClickListener {

    private HomePresenterImpl presenter;
    private FavoritesPresenterImp favoritesPresenter;

    private RecyclerView rvCategories, rvCountries, rvForYou;
    private CategoryAdapter categoryAdapter;
    private CountryAdapter countryAdapter;
    private MealAdapter forYouAdapter;
    private CardView mealOfDayCard;
    private ImageView mealOfDayImage;
    private TextView mealOfDayName;
    private TextView tvSeeAllCategories, tvSeeAllCountries, tvUserName;
    private ProgressBar progressForYou;
    private ProgressBar progressCategories;
    private ProgressBar progressCountries;
    private ProgressBar progressMealOfDay;

    private View offlineView;
    private View contentView;
    private io.reactivex.rxjava3.disposables.Disposable networkDisposable;
    private com.aalaa.foodplanner.data.network.ConnectivityObserver connectivityObserver;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

        progressForYou.setVisibility(View.VISIBLE);
        progressCategories.setVisibility(View.VISIBLE);
        progressCountries.setVisibility(View.VISIBLE);
        progressMealOfDay.setVisibility(View.VISIBLE);

        setupRecyclerViews();

        presenter = new HomePresenterImpl(this,
                MealRepositoryImpl.getInstance(MealRemoteDataSource.getInstance()));

        FavoritesRepositoryImpl favoritesRepo = FavoritesRepositoryImpl.getInstance(requireActivity().getApplication());
        favoritesPresenter = new FavoritesPresenterImp(favoritesRepo, this);

        presenter.getHomeData();
        bindUserInfo();
        observeNetwork();
    }

    private void observeNetwork() {
        connectivityObserver = com.aalaa.foodplanner.data.network.ConnectivityObserver.getInstance(requireContext());
        connectivityObserver.startListening();
        networkDisposable = connectivityObserver.observeNetwork()
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(isConnected -> {
                    android.util.Log.d("HomeFragment", "Network status changed: " + isConnected);
                    if (isConnected) {
                        offlineView.setVisibility(View.GONE);
                        contentView.setVisibility(View.VISIBLE);
                        if (presenter != null) {
                            presenter.getHomeData();
                        }
                    } else {
                        offlineView.setVisibility(View.VISIBLE);
                        contentView.setVisibility(View.GONE);
                    }
                }, throwable -> {
                    android.util.Log.e("HomeFragment", "Error observing network", throwable);
                });
    }

    private void initViews(View view) {
        rvCategories = view.findViewById(R.id.rv_categories);
        rvCountries = view.findViewById(R.id.rv_countries);
        rvForYou = view.findViewById(R.id.rv_for_you);
        mealOfDayCard = view.findViewById(R.id.meal_of_day_card);
        mealOfDayImage = view.findViewById(R.id.meal_of_day_image);
        mealOfDayName = view.findViewById(R.id.meal_of_day_name);
        progressForYou = view.findViewById(R.id.progressForYou);
        progressCategories = view.findViewById(R.id.progressCategories);
        progressCountries = view.findViewById(R.id.progressCountries);
        progressMealOfDay = view.findViewById(R.id.progressMealOfDay);
        tvUserName = view.findViewById(R.id.tv_user_name);

        offlineView = view.findViewById(R.id.offline_view);
        contentView = view.findViewById(R.id.content_view);

        if (tvSeeAllCategories != null) {
            tvSeeAllCategories.setOnClickListener(
                    v -> Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_categoriesFragment));
        }

        if (tvSeeAllCountries != null) {
            tvSeeAllCountries.setOnClickListener(
                    v -> Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_countriesFragment));
        }
    }

    private void bindUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            tvUserName.setText(" Guest");
            return;
        }

        String name = user.getDisplayName();

        tvUserName.setText(String.format(" %s", name != null && !name.trim().isEmpty() ? name : "User"));
    }

    private void setupRecyclerViews() {
        categoryAdapter = new CategoryAdapter(getContext(), new ArrayList<>(), this::onCategoryClick);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        countryAdapter = new CountryAdapter(getContext(), new ArrayList<>(), this::onCountryClick);
        rvCountries.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvCountries.setAdapter(countryAdapter);

        forYouAdapter = new MealAdapter(getContext(), new ArrayList<>(), this);
        rvForYou.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvForYou.setAdapter(forYouAdapter);
    }

    @Override
    public void onMealClick(MealsItem meal) {
        HomeFragmentDirections.ActionHomeToRecipeDetail action = HomeFragmentDirections.actionHomeToRecipeDetail(meal);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onBookmarkClick(MealsItem meal, int position) {
        com.aalaa.foodplanner.data.repository.FavoritesRepositoryImpl.getInstance(requireActivity().getApplication())
                .isFavorite(meal.getIdMeal())
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(isFav -> {
                    if (isFav) {
                        favoritesPresenter.removeFromFavorites(meal);
                    } else {
                        favoritesPresenter.addToFavorites(meal);
                    }
                }, error -> showError(error.getMessage()));
    }

    private void onCategoryClick(Category category) {
        HomeFragmentDirections.ActionHomeToMealsListing action = HomeFragmentDirections
                .actionHomeToMealsListing("category", category.getStrCategory());
        Navigation.findNavController(requireView()).navigate(action);
    }

    private void onCountryClick(Area area) {
        HomeFragmentDirections.ActionHomeToMealsListing action = HomeFragmentDirections.actionHomeToMealsListing("area",
                area.getStrArea());
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void showRandomMeal(MealsItem meal) {
        if (meal != null) {
            Glide.with(this)
                    .load(meal.getStrMealThumb())
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(mealOfDayImage);
            mealOfDayName.setText(meal.getStrMeal());
            mealOfDayCard.setOnClickListener(v -> onMealClick(meal));
        }
    }

    @Override
    public void showForYouMeals(List<MealsItem> meals) {
        forYouAdapter = new MealAdapter(getContext(), meals, this);
        rvForYou.setAdapter(forYouAdapter);
    }

    @Override
    public void showCategories(List<Category> categories) {
        categoryAdapter = new CategoryAdapter(getContext(), categories, this::onCategoryClick);
        rvCategories.setAdapter(categoryAdapter);
    }

    @Override
    public void showCountries(List<Area> areas) {
        countryAdapter = new CountryAdapter(getContext(), areas, this::onCountryClick);
        rvCountries.setAdapter(countryAdapter);
    }

    @Override
    public void showLoading() {
        progressForYou.setVisibility(View.VISIBLE);
        progressCategories.setVisibility(View.VISIBLE);
        progressCountries.setVisibility(View.VISIBLE);
        progressMealOfDay.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressForYou.setVisibility(View.GONE);
        progressCategories.setVisibility(View.GONE);
        progressCountries.setVisibility(View.GONE);
        progressMealOfDay.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        AppSnack.showError(requireView(), message);
    }

    @Override
    public void showFavorites(List<MealsItem> favorites) {

    }

    @Override
    public void showEmptyState() {
    }

    @Override
    public void showAddedToFavorites() {
        AppSnack.showSuccess(requireView(), "Added to Favorites ❤️");
    }

    @Override
    public void showRemovedFromFavorites() {
        AppSnack.showInfo(requireView(), "Removed from Favorites");
    }

    @Override
    public void updateFavoriteIcon(boolean isFavorite) {
    }

    @Override
    public void onDestroyView() {
        if (presenter != null) {
            presenter.onDestroy();
        }
        if (favoritesPresenter != null) {
            favoritesPresenter.dispose();
        }
        if (networkDisposable != null && !networkDisposable.isDisposed()) {
            networkDisposable.dispose();
        }
        com.aalaa.foodplanner.data.network.ConnectivityObserver.getInstance(requireContext()).stopListening();
        super.onDestroyView();
    }
}