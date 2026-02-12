package com.aalaa.foodplanner.ui.categories.view;

import com.aalaa.foodplanner.domain.models.Category;
import java.util.List;

public interface CategoriesView {
    void showCategories(List<Category> categories);

    void showError(String error);

    void showLoading();

    void hideLoading();
}
