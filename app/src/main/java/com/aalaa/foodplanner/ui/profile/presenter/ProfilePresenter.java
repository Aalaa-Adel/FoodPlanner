package com.aalaa.foodplanner.ui.profile.presenter;

import com.aalaa.foodplanner.ui.profile.view.ProfileView;

public interface ProfilePresenter {
    void attachView(ProfileView view);
    void detachView();

    void backupData();
    void restoreData();
    void logout(boolean shouldSync);
}
