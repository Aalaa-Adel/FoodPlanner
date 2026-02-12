package com.aalaa.foodplanner.ui.authentication.view;

import com.aalaa.foodplanner.ui.authentication.base.BaseView;

public interface SignUpView extends BaseView {
    void showUsernameError(String error);
    void showEmailError(String error);
    void showPasswordError(String error);
    void showConfirmPasswordError(String error);
    void clearErrors();
    void onSignUpSuccess();
}
