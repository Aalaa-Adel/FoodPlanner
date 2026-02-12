package com.aalaa.foodplanner.ui.authentication.presenter;

import android.text.TextUtils;
import android.util.Patterns;

import com.aalaa.foodplanner.domain.repository.AuthRepository;
import com.aalaa.foodplanner.domain.repository.SyncRepository;
import com.aalaa.foodplanner.ui.authentication.base.BasePresenter;
import com.aalaa.foodplanner.ui.authentication.view.LoginView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginPresenterImpl extends BasePresenter<LoginView>
        implements LoginPresenter {

    private final AuthRepository repository;
    private final SyncRepository syncRepository;
    private final com.aalaa.foodplanner.data.local.SessionManager sessionManager;

    public LoginPresenterImpl(AuthRepository repository, SyncRepository syncRepository,
            com.aalaa.foodplanner.data.local.SessionManager sessionManager) {
        this.repository = repository;
        this.syncRepository = syncRepository;
        this.sessionManager = sessionManager;
    }

    @Override
    public void login(String email, String password) {
        if (!isViewAttached())
            return;

        getView().clearErrors();

        if (!validateInput(email, password))
            return;

        getView().showLoading();

        addDisposable(
                repository.signInWithEmail(email, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        // We do NOT restore yet. We check if guest.
                        .subscribe(
                                user -> {
                                    if (!isViewAttached())
                                        return;
                                    getView().hideLoading();

                                    if (sessionManager.isGuest()) {
                                        // Guest just logged in. Ask for migration.
                                        getView().showMigrationDialog();
                                    } else {
                                        // Regular login (was logged out). Restore data.
                                        restoreAndFinish();
                                    }
                                },
                                throwable -> {
                                    if (!isViewAttached())
                                        return;
                                    getView().hideLoading();
                                    String msg = (throwable.getMessage() != null) ? throwable.getMessage()
                                            : "Login failed";
                                    getView().showError(msg);
                                }));
    }

    @Override
    public void loginWithGoogle(String idToken) {
        if (!isViewAttached())
            return;

        getView().showLoading();

        addDisposable(
                repository.signInWithGoogle(idToken)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> {
                                    if (!isViewAttached())
                                        return;
                                    getView().hideLoading();

                                    if (sessionManager.isGuest()) {
                                        getView().showMigrationDialog();
                                    } else {
                                        restoreAndFinish();
                                    }
                                },
                                throwable -> {
                                    if (!isViewAttached())
                                        return;
                                    getView().hideLoading();
                                    String msg = (throwable.getMessage() != null) ? throwable.getMessage()
                                            : "Google login failed";
                                    getView().showError(msg);
                                }));
    }

    @Override
    public void migrateGuestData() {
        if (!isViewAttached())
            return;
        getView().showLoading();

        // 1. Backup local data to cloud (merging with whatever is there, likely empty)
        // 2. Clear IsGuest flag
        // 3. Mark as restored (since we just pushed our data, we assume we are
        // consistent)
        // OR maybe we should pull as well? Backup then Restore?
        // Let's do Backup then Restore.

        addDisposable(
                syncRepository.backupUserData()
                        .andThen(syncRepository.restoreUserData())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    sessionManager.clearGuest();
                                    // Mark as restored so we don't restore again
                                    com.google.firebase.auth.FirebaseUser u = com.google.firebase.auth.FirebaseAuth
                                            .getInstance().getCurrentUser();
                                    if (u != null)
                                        sessionManager.setRestored(u.getUid());

                                    if (isViewAttached()) {
                                        getView().hideLoading();
                                        getView().onLoginSuccess();
                                    }
                                },
                                throwable -> {
                                    if (isViewAttached()) {
                                        getView().hideLoading();
                                        getView().showError("Migration failed: " + throwable.getMessage());
                                        // Force clear guest anyway? No, let them retry.
                                    }
                                }));
    }

    @Override
    public void clearAndRestoreData() {
        if (!isViewAttached())
            return;
        getView().showLoading();

        addDisposable(
                syncRepository.clearLocalData()
                        .andThen(syncRepository.restoreUserData())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    sessionManager.clearGuest();
                                    com.google.firebase.auth.FirebaseUser u = com.google.firebase.auth.FirebaseAuth
                                            .getInstance().getCurrentUser();
                                    if (u != null)
                                        sessionManager.setRestored(u.getUid());

                                    if (isViewAttached()) {
                                        getView().hideLoading();
                                        getView().onLoginSuccess();
                                    }
                                },
                                throwable -> {
                                    if (isViewAttached()) {
                                        getView().hideLoading();
                                        getView().showError("Restore failed: " + throwable.getMessage());
                                    }
                                }));
    }

    private void restoreAndFinish() {
        if (!isViewAttached())
            return;
        getView().showLoading();
        addDisposable(
                syncRepository.restoreUserData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    com.google.firebase.auth.FirebaseUser u = com.google.firebase.auth.FirebaseAuth
                                            .getInstance().getCurrentUser();
                                    if (u != null)
                                        sessionManager.setRestored(u.getUid());

                                    if (isViewAttached()) {
                                        getView().hideLoading();
                                        getView().onLoginSuccess();
                                    }
                                },
                                throwable -> {
                                    if (isViewAttached()) {
                                        getView().hideLoading();
                                        // Even if restore fails, we are logged in.
                                        // Maybe just warn?
                                        getView()
                                                .showError("Login success, but sync failed: " + throwable.getMessage());
                                        getView().onLoginSuccess();
                                    }
                                }));
    }

    private boolean validateInput(String email, String password) {
        if (!isViewAttached())
            return false;

        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            getView().showEmailError("Email is required");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            getView().showEmailError("Invalid email address");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            getView().showPasswordError("Password is required");
            valid = false;
        } else if (password.length() < 6) {
            getView().showPasswordError("Password must be at least 6 characters");
            valid = false;
        }

        return valid;
    }
}
