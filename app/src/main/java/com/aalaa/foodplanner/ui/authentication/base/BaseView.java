package com.aalaa.foodplanner.ui.authentication.base;

public interface BaseView {
    void showLoading();
    void hideLoading();
    void showError(String message);
    void showSuccess(String message);
}