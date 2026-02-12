package com.aalaa.foodplanner.ui.authentication.presenter;

import com.aalaa.foodplanner.ui.authentication.view.LoginView;

public interface LoginPresenter {
    void attachView(LoginView view);

    void detachView();

    void login(String email, String password);

    void loginWithGoogle(String idToken);

    void migrateGuestData();

    void clearAndRestoreData();
}
