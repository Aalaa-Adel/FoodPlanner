package com.aalaa.foodplanner.presentation.authentication.presenter;

import android.text.TextUtils;
import android.util.Patterns;

import com.aalaa.foodplanner.data.repository.AuthRepository;
import com.aalaa.foodplanner.data.repository.AuthRepositoryImpl;
import com.aalaa.foodplanner.presentation.authentication.AuthContract;
import com.aalaa.foodplanner.presentation.authentication.base.BasePresenter;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SignUpPresenter extends BasePresenter<AuthContract.SignUpView>
        implements AuthContract.SignUpPresenter {

    private final AuthRepository repository;

    public SignUpPresenter() {
        this.repository = new AuthRepositoryImpl();
    }

    public SignUpPresenter(AuthRepository repository) {
        this.repository = repository;
    }

    @Override
    public void signUp(String email, String password, String confirmPassword) {
        if (!isViewAttached()) return;

        getView().clearErrors();

        if (!validateInput(email, password, confirmPassword)) return;

        getView().showLoading();

        addDisposable(
                repository.signUpWithEmail(email, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> {
                                    if (isViewAttached()) {
                                        getView().hideLoading();
                                        getView().onSignUpSuccess();
                                    }
                                },
                                throwable -> {
                                    if (isViewAttached()) {
                                        getView().hideLoading();
                                        getView().showError(throwable.getMessage());
                                    }
                                }
                        )
        );
    }

    @Override
    public void signUpWithGoogle(String idToken) {
        if (!isViewAttached()) return;

        getView().showLoading();

        addDisposable(
                repository.signInWithGoogle(idToken)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> {
                                    if (isViewAttached()) {
                                        getView().hideLoading();
                                        getView().onSignUpSuccess();
                                    }
                                },
                                throwable -> {
                                    if (isViewAttached()) {
                                        getView().hideLoading();
                                        getView().showError(throwable.getMessage());
                                    }
                                }
                        )
        );
    }

    private boolean validateInput(String email, String password, String confirmPassword) {
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

        if (TextUtils.isEmpty(confirmPassword)) {
            getView().showConfirmPasswordError("Please confirm your password");
            valid = false;
        } else if (!password.equals(confirmPassword)) {
            getView().showConfirmPasswordError("Passwords do not match");
            valid = false;
        }

        return valid;
    }
}