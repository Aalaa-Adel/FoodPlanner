package com.aalaa.foodplanner.ui.authentication.view;

import com.aalaa.foodplanner.ui.authentication.base.BaseView;

public interface LoginView extends BaseView {
    void showEmailError(String error);

    void showPasswordError(String error);

    void clearErrors();

    void onLoginSuccess();

    void showMigrationDialog();
}
