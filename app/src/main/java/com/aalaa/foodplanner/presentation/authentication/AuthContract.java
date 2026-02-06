package com.aalaa.foodplanner.presentation.authentication;

import com.aalaa.foodplanner.presentation.authentication.base.BaseView;

public interface AuthContract {

    interface LoginView extends BaseView {
        void showEmailError(String error);
        void showPasswordError(String error);
        void clearErrors();
        void onLoginSuccess();
    }

    interface LoginPresenter {
        void attachView(LoginView view);
        void detachView();
        void login(String email, String password);
        void loginWithGoogle(String idToken);
    }

    interface SignUpView extends BaseView {
        void showEmailError(String error);
        void showPasswordError(String error);
        void showConfirmPasswordError(String error);
        void clearErrors();
        void onSignUpSuccess();
    }

    interface SignUpPresenter {
        void attachView(SignUpView view);
        void detachView();
        void signUp(String email, String password, String confirmPassword);
        void signUpWithGoogle(String idToken);
    }
}