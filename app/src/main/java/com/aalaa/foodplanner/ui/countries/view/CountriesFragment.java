package com.aalaa.foodplanner.ui.countries.view;

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
import com.aalaa.foodplanner.domain.models.Area;
import com.aalaa.foodplanner.ui.common.AppSnack;
import com.aalaa.foodplanner.ui.countries.presenter.CountriesPresenter;
import com.aalaa.foodplanner.ui.countries.presenter.CountriesPresenterImpl;

import java.util.ArrayList;
import java.util.List;

public class CountriesFragment extends Fragment implements CountriesView, OnCountryClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CountryAdapter adapter;
    private CountriesPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_countries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_all_countries);
        progressBar = view.findViewById(R.id.progressBar);

        adapter = new CountryAdapter(getContext(), new ArrayList<>(), this);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        presenter = new CountriesPresenterImpl(this,
                MealRepositoryImpl.getInstance(MealRemoteDataSource.getInstance()));
        presenter.getCountries();
    }

    @Override
    public void showCountries(List<Area> countries) {
        adapter = new CountryAdapter(getContext(), countries, this);
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
    public void onCountryClick(Area country) {
        CountriesFragmentDirections.ActionCountriesFragmentToMealsListingFragment action = CountriesFragmentDirections
                .actionCountriesFragmentToMealsListingFragment("area", country.getStrArea());
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
