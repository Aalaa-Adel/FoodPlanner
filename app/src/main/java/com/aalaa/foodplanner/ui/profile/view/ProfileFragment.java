package com.aalaa.foodplanner.ui.profile.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.aalaa.foodplanner.ui.common.AppSnack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.data.datasource.remote.MealRemoteDataSource;
import com.aalaa.foodplanner.data.repository.FavoritesRepositoryImpl;
import com.aalaa.foodplanner.data.repository.MealRepositoryImpl;
import com.aalaa.foodplanner.data.repository.PlanRepositoryImpl;
import com.aalaa.foodplanner.data.repository.SyncRepositoryImpl;
import com.aalaa.foodplanner.domain.repository.SyncRepository;
import com.aalaa.foodplanner.ui.profile.presenter.ProfilePresenter;
import com.aalaa.foodplanner.ui.profile.presenter.ProfilePresenterImpl;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment implements ProfileView {

    private TextView tvUserName, tvUserEmail, tvCurrentLanguage;
    private MaterialButton btnBackup, btnRestore, btnLogout;
    private FrameLayout loadingOverlay;
    private com.google.android.material.switchmaterial.SwitchMaterial switchDarkMode;
    private android.widget.LinearLayout layoutLanguage;

    private com.aalaa.foodplanner.data.local.SharedPreferencesHelper prefs;

    private ProfilePresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        prefs = new com.aalaa.foodplanner.data.local.SharedPreferencesHelper(requireContext());

        SyncRepository syncRepository = provideSyncRepository();
        com.aalaa.foodplanner.data.local.SessionManager sessionManager = com.aalaa.foodplanner.data.local.SessionManager
                .getInstance(requireContext());
        com.aalaa.foodplanner.data.network.ConnectivityObserver connectivityObserver = com.aalaa.foodplanner.data.network.ConnectivityObserver
                .getInstance(requireContext());
        com.aalaa.foodplanner.domain.usecase.SyncPolicy syncPolicy = new com.aalaa.foodplanner.domain.usecase.SyncPolicy(
                sessionManager, connectivityObserver);

        presenter = new ProfilePresenterImpl(syncRepository, sessionManager, syncPolicy);
        presenter.attachView(this);

        bindUserInfo();
        setupClickListeners();
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);

        btnBackup = view.findViewById(R.id.btn_backup);
        btnRestore = view.findViewById(R.id.btn_restore);
        btnLogout = view.findViewById(R.id.btn_logout);

        loadingOverlay = view.findViewById(R.id.loading_overlay);

        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        layoutLanguage = view.findViewById(R.id.layout_language);
        tvCurrentLanguage = view.findViewById(R.id.tv_current_language);
    }

    private void bindUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            tvUserName.setText("Guest");
            tvUserEmail.setText("");
            return;
        }

        String name = user.getDisplayName();
        String email = user.getEmail();

        tvUserName.setText(name != null && !name.trim().isEmpty() ? name : "User");
        tvUserEmail.setText(email != null ? email : "");

        switchDarkMode.setChecked(prefs.isDarkMode());

        String langCode = prefs.getLanguage();
        tvCurrentLanguage.setText(langCode.equals("ar") ? "العربية" : "English");
    }

    private void setupClickListeners() {
        btnBackup.setOnClickListener(v -> presenter.backupData());
        btnRestore.setOnClickListener(v -> presenter.restoreData());
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.setDarkMode(isChecked);
            if (isChecked) {
                androidx.appcompat.app.AppCompatDelegate
                        .setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                androidx.appcompat.app.AppCompatDelegate
                        .setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        layoutLanguage.setOnClickListener(v -> showLanguageDialog());
    }

    private void showLanguageDialog() {
        String[] languages = { "English", "العربية" };
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Language")
                .setItems(languages, (dialog, which) -> {
                    String selectedLang = which == 0 ? "en" : "ar";
                    if (!selectedLang.equals(prefs.getLanguage())) {
                        com.aalaa.foodplanner.ui.common.LanguageHelper.setLocale(requireContext(), selectedLang);
                        requireActivity().recreate();
                    }
                })
                .show();
    }

    private SyncRepository provideSyncRepository() {
        FavoritesRepositoryImpl favoritesRepo = FavoritesRepositoryImpl.getInstance(requireActivity().getApplication());

        PlanRepositoryImpl planRepo = PlanRepositoryImpl.getInstance(requireActivity().getApplication());

        MealRepositoryImpl mealRepo = new MealRepositoryImpl(MealRemoteDataSource.getInstance());

        com.aalaa.foodplanner.datasource.db.AppDatabase db = com.aalaa.foodplanner.datasource.db.AppDatabase
                .getInstance(requireActivity().getApplication());
        return SyncRepositoryImpl.getInstance(favoritesRepo, planRepo, mealRepo, db.pendingActionDao());
    }

    @Override
    public void showBackupSuccess() {
        AppSnack.showSuccess(requireView(), "Backup done ✅");
    }

    @Override
    public void showBackupError(String message) {
        AppSnack.showError(requireView(), message);
    }

    @Override
    public void showRestoreSuccess() {
        AppSnack.showSuccess(requireView(), "Restore done ✅");
    }

    @Override
    public void showRestoreError(String message) {
        AppSnack.showError(requireView(), message);
    }

    @Override
    public void showLoading() {
        if (loadingOverlay != null)
            loadingOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        if (loadingOverlay != null)
            loadingOverlay.setVisibility(View.GONE);
    }

    @Override
    public void setButtonsEnabled(boolean enabled) {
        if (btnBackup != null)
            btnBackup.setEnabled(enabled);
        if (btnRestore != null)
            btnRestore.setEnabled(enabled);
        if (btnLogout != null)
            btnLogout.setEnabled(enabled);
    }

    @Override
    public void navigateToLogin() {
        AppSnack.showInfo(requireView(), "Logged out ✅");
        androidx.navigation.Navigation.findNavController(requireView())
                .navigate(R.id.auth_graph, null,
                        new androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.main_graph, true)
                                .build());
    }

    @Override
    public void showLogoutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Do you want to sync (backup) before logout?")
                .setPositiveButton("Sync & Logout", (dialog, which) -> presenter.logout(true))
                .setNegativeButton("Logout only", (dialog, which) -> presenter.logout(false))
                .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null)
            presenter.detachView();

        tvUserName = null;
        tvUserEmail = null;
        btnBackup = null;
        btnRestore = null;
        btnLogout = null;
        loadingOverlay = null;
    }
}
