package com.aalaa.foodplanner.ui.favorites.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.aalaa.foodplanner.ui.common.AppSnack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.data.db.AppDatabase;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.aalaa.foodplanner.data.repository.FavoritesRepositoryImpl;
import com.aalaa.foodplanner.ui.favorites.presenter.FavoritesPresenterImp;

import java.util.List;

public class FavoritesFragment extends Fragment
        implements FavView, OnFavouriteClickListener {

    private FavoritesPresenterImp presenter;
    private FavoritesAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = AppDatabase.getInstance(requireContext());
        FavoritesRepositoryImpl favoritesRepo = FavoritesRepositoryImpl.getInstance(requireActivity().getApplication());
        presenter = new FavoritesPresenterImp(favoritesRepo, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        presenter.loadFavorites();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_favorites);
        emptyState = view.findViewById(R.id.layout_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        adapter = new FavoritesAdapter(requireContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showFavorites(List<MealsItem> favorites) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        adapter.setFavorites(favorites);
    }

    @Override
    public void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String error) {
        progressBar.setVisibility(View.GONE);
        AppSnack.showError(requireView(), error);
    }

    @Override
    public void showAddedToFavorites() {
        AppSnack.showSuccess(requireView(), "Added to favorites");
    }

    @Override
    public void showRemovedFromFavorites() {
        AppSnack.showInfo(requireView(), "Removed from favorites");
        presenter.loadFavorites();
    }

    @Override
    public void updateFavoriteIcon(boolean isFavorite) {
    }

    @Override
    public void onRemoveClick(MealsItem meal) {
        presenter.removeFromFavorites(meal);
    }

    @Override
    public void onMealClick(MealsItem meal) {
        if (getView() != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putSerializable("meal", meal);
                Navigation.findNavController(getView()).navigate(R.id.recipeDetailFragment, bundle);
            } catch (Exception e) {
                e.printStackTrace();
                AppSnack.showError(requireView(), "Cannot navigate to details");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.dispose();
        }
    }
}