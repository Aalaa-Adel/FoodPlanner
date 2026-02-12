package com.aalaa.foodplanner.ui.profile.view;

public interface ProfileView {

    void showBackupSuccess();
    void showBackupError(String message);

    void showRestoreSuccess();
    void showRestoreError(String message);

    void showLoading();
    void hideLoading();

    void setButtonsEnabled(boolean enabled);

    void navigateToLogin();

    void showLogoutDialog();
}
