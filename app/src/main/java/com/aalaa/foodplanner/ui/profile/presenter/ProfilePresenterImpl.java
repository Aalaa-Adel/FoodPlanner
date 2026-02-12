package com.aalaa.foodplanner.ui.profile.presenter;

import android.util.Log;

import com.aalaa.foodplanner.data.local.SessionManager;
import com.aalaa.foodplanner.domain.repository.SyncRepository;
import com.aalaa.foodplanner.domain.usecase.SyncPolicy;
import com.aalaa.foodplanner.ui.profile.view.ProfileView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.ref.WeakReference;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProfilePresenterImpl implements ProfilePresenter {

    private static final String TAG = "ProfilePresenter";

    private WeakReference<ProfileView> viewRef;
    private final SyncRepository syncRepository;
    private final SessionManager sessionManager;
    private final SyncPolicy syncPolicy;
    private final FirebaseAuth firebaseAuth;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public ProfilePresenterImpl(SyncRepository syncRepository, SessionManager sessionManager, SyncPolicy syncPolicy) {
        this.syncRepository = syncRepository;
        this.sessionManager = sessionManager;
        this.syncPolicy = syncPolicy;
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void attachView(ProfileView view) {
        viewRef = new WeakReference<>(view);
    }

    @Override
    public void detachView() {
        disposables.clear();
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }

    private ProfileView view() {
        return viewRef != null ? viewRef.get() : null;
    }

    private boolean isViewAttached() {
        return view() != null;
    }

    @Override
    public void backupData() {
        ProfileView v = view();
        if (v == null)
            return;

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            v.showBackupError("You must be logged in to backup data.");
            return;
        }

        if (!syncPolicy.shouldRunBackup()) {
            v.showBackupError("You are offline. Cannot backup now.");
            return;
        }

        v.showLoading();
        v.setButtonsEnabled(false);

        Disposable d = syncRepository.backupUserData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            ProfileView vv = view();
                            if (vv == null)
                                return;
                            vv.hideLoading();
                            vv.setButtonsEnabled(true);
                            vv.showBackupSuccess();
                            Log.d(TAG, "Backup successful for uid=" + user.getUid());
                        },
                        throwable -> {
                            ProfileView vv = view();
                            if (vv == null)
                                return;
                            vv.hideLoading();
                            vv.setButtonsEnabled(true);
                            vv.showBackupError(safeMsg(throwable,
                                    "Failed to backup data. Check your connection and try again."));
                            Log.e(TAG, "Backup failed", throwable);
                        });

        disposables.add(d);
    }

    @Override
    public void restoreData() {
        ProfileView v = view();
        if (v == null)
            return;

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            v.showRestoreError("You must be logged in to restore data.");
            return;
        }


        if (!syncPolicy.shouldRunBackup()) {
            v.showRestoreError("You are offline. Cannot restore now.");
            return;
        }

        v.showLoading();
        v.setButtonsEnabled(false);

        Disposable d = syncRepository.restoreUserData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            ProfileView vv = view();
                            if (vv == null)
                                return;
                            sessionManager.setRestored(user.getUid());

                            vv.hideLoading();
                            vv.setButtonsEnabled(true);
                            vv.showRestoreSuccess();
                            Log.d(TAG, "Restore successful for uid=" + user.getUid());
                        },
                        throwable -> {
                            ProfileView vv = view();
                            if (vv == null)
                                return;
                            vv.hideLoading();
                            vv.setButtonsEnabled(true);
                            vv.showRestoreError(safeMsg(throwable,
                                    "Failed to restore data. Check your connection and try again."));
                            Log.e(TAG, "Restore failed", throwable);
                        });

        disposables.add(d);
    }

    @Override
    public void logout(boolean shouldSync) {
        ProfileView v = view();
        if (v == null)
            return;

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            sessionManager.clearGuest();
            v.navigateToLogin();
            return;
        }

        v.showLoading();
        v.setButtonsEnabled(false);

        Disposable d;

        if (shouldSync) {
            if (!syncPolicy.shouldRunBackup()) {
                v.hideLoading();
                v.setButtonsEnabled(true);
                v.showBackupError("Offline. Cannot sync. Logout without sync?");
                return;
            }

            d = syncRepository.backupUserData()
                    .andThen(syncRepository.clearLocalData())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            () -> {
                                sessionManager.clearGuest();
                                finishLogout("Logout (sync) successful");
                            },
                            throwable -> {
                                ProfileView vv = view();
                                if (vv == null)
                                    return;
                                vv.hideLoading();
                                vv.setButtonsEnabled(true);
                                vv.showBackupError("Sync failed. You can logout without syncing if you want.");
                                Log.e(TAG, "Logout (sync) failed", throwable);
                            });
        } else {
            d = syncRepository.clearLocalData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            () -> {
                                sessionManager.clearGuest();
                                finishLogout("Logout (no sync) successful");
                            },
                            throwable -> {
                                Log.e(TAG, "Logout (no sync) clearLocal failed", throwable);
                                sessionManager.clearGuest();
                                finishLogout("Logout completed, but local clear had issues.");
                            });
        }

        disposables.add(d);
    }

    private void finishLogout(String logMessage) {
        ProfileView v = view();
        if (v == null)
            return;

        firebaseAuth.signOut();
        v.hideLoading();
        v.setButtonsEnabled(true);
        v.navigateToLogin();

        Log.d(TAG, logMessage);
    }

    private String safeMsg(Throwable t, String fallback) {
        String m = (t != null) ? t.getMessage() : null;
        return (m != null && !m.trim().isEmpty()) ? m : fallback;
    }
}
