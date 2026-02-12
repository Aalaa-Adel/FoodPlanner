package com.aalaa.foodplanner.ui.categories.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.data.datasource.remote.MealRemoteDataSource;
import com.aalaa.foodplanner.data.repository.MealRepositoryImpl;
import com.aalaa.foodplanner.domain.models.Category;
import com.aalaa.foodplanner.ui.categories.presenter.CategoriesPresenter;
import com.aalaa.foodplanner.ui.categories.presenter.CategoriesPresenterImpl;
import com.aalaa.foodplanner.ui.common.AppSnack;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment implements CategoriesView, OnCategoryClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CategoryAdapter adapter;
    private CategoriesPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_all_categories);
        progressBar = view.findViewById(R.id.progressBar);

        // Initialize with empty list
        adapter = new CategoryAdapter(getContext(), new ArrayList<>(), this);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        presenter = new CategoriesPresenterImpl(this,
                MealRepositoryImpl.getInstance(MealRemoteDataSource.getInstance()));
        presenter.getCategories();
    }

    @Override
    public void showCategories(List<Category> categories) {
        adapter = new CategoryAdapter(getContext(), categories, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showError(String error) {
        AppSnack.showError(getView() != null ? getView() : requireActivity().findViewById(android.R.id.content), error);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onCategoryClick(Category category) {
        CategoriesFragmentDirections.ActionCategoriesFragmentToMealsListingFragment action = CategoriesFragmentDirections
                .actionCategoriesFragmentToMealsListingFragment("category", category.getStrCategory());
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.clear();
        }
    }
}
