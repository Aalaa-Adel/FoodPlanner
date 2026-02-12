package com.aalaa.foodplanner.ui.authentication.presenter;

import com.aalaa.foodplanner.ui.authentication.view.SignUpView;

public interface SignUpPresenter {
    void attachView(SignUpView view);
    void detachView();
    void signUp(String username, String email, String password, String confirmPassword);
    void signUpWithGoogle(String idToken);
}
