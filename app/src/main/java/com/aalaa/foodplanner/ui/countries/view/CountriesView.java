package com.aalaa.foodplanner.ui.countries.view;

import com.aalaa.foodplanner.domain.models.Area;
import java.util.List;

public interface CountriesView {
    void showCountries(List<Area> countries);

    void showError(String error);

    void showLoading();

    void hideLoading();
}
