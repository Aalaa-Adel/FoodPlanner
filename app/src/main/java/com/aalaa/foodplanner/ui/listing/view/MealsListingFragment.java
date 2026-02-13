package com.aalaa.foodplanner.ui.listing.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.aalaa.foodplanner.data.repository.MealRepositoryImpl;
import com.aalaa.foodplanner.ui.common.AppSnack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.data.datasource.remote.MealRemoteDataSource;
import com.aalaa.foodplanner.domain.models.MealSpecification;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.aalaa.foodplanner.data.repository.FavoritesRepositoryImpl;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.aalaa.foodplanner.ui.listing.presenter.MealsListingPresenterImpl;

import java.util.List;

public class MealsListingFragment extends Fragment
        implements MealsListingView, OnMealClickListener, OnFavoriteClickListener {

    private MealsListingPresenterImpl presenter;
    private RecyclerView rvMeals;
    private MealsListingAdapter adapter;
    private ProgressBar progressBar;

    public static final String TYPE_CATEGORY = "category";
    public static final String TYPE_AREA = "area";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meals_listing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();

        presenter = new MealsListingPresenterImpl(this,
                new MealRepositoryImpl(MealRemoteDataSource.getInstance()),
                FavoritesRepositoryImpl.getInstance(requireActivity().getApplication()));

        MealsListingFragmentArgs args = MealsListingFragmentArgs.fromBundle(getArguments());
        String type = args.getType();
        String value = args.getValue();

        if (TYPE_CATEGORY.equals(type)) {
            presenter.getMealsByCategory(value);
        } else if (TYPE_AREA.equals(type)) {
            presenter.getMealsByArea(value);
        }
    }

    private void initViews(View view) {
        rvMeals = view.findViewById(R.id.rv_meals_listing);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        adapter = new MealsListingAdapter();
        adapter.setOnMealClickListener(this);
        adapter.setOnFavoriteClickListener(this);
        rvMeals.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvMeals.setAdapter(adapter);
    }

    @Override
    public void onMealClick(MealSpecification meal) {
        presenter.getMealById(meal.getIdMeal());
    }

    @Override
    public void onFavoriteClick(MealSpecification meal) {
        FavoritesRepositoryImpl.getInstance(requireActivity().getApplication())
                .isFavorite(meal.getIdMeal())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isFav -> {
                    if (isFav) {
                        presenter.removeFromFavorites(convertToMealsItem(meal));
                        meal.setFavorite(false);
                    } else {
                        presenter.addToFavorites(convertToMealsItem(meal));
                        meal.setFavorite(true);
                    }
                    adapter.notifyDataSetChanged();
                }, error -> showError(error.getMessage()));
    }

    @Override
    public void showAddedToFavorites() {
        AppSnack.showSuccess(requireView(), "Added to Favorites ❤️");
    }

    @Override
    public void showRemovedFromFavorites() {
        AppSnack.showInfo(requireView(), "Removed from Favorites");
    }

    private MealsItem convertToMealsItem(MealSpecification spec) {
        MealsItem item = new MealsItem();
        item.setIdMeal(spec.getIdMeal());
        item.setStrMeal(spec.getStrMeal());
        item.setStrMealThumb(spec.getStrMealThumb());
        return item;
    }

    @Override
    public void showMeals(List<MealSpecification> meals) {
        adapter.setMeals(meals);
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

    @Override
    public void showError(String message) {
        AppSnack.showError(getView() != null ? getView() : requireActivity().findViewById(android.R.id.content),
                message);
    }

    @Override
    public void navigateToMealDetail(MealsItem meal) {
        MealsListingFragmentDirections.ActionListingToDetail action = MealsListingFragmentDirections
                .actionListingToDetail(meal);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
    }
}
