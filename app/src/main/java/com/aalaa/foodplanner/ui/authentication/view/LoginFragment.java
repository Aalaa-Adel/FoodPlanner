package com.aalaa.foodplanner.ui.authentication.view;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.aalaa.foodplanner.ui.common.AppSnack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.data.datasource.remote.MealRemoteDataSource;
import com.aalaa.foodplanner.data.repository.AuthRepositoryImpl;
import com.aalaa.foodplanner.data.repository.FavoritesRepositoryImpl;
import com.aalaa.foodplanner.data.repository.MealRepositoryImpl;
import com.aalaa.foodplanner.data.repository.PlanRepositoryImpl;
import com.aalaa.foodplanner.data.repository.SyncRepositoryImpl;
import com.aalaa.foodplanner.ui.authentication.presenter.LoginPresenterImpl;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import java.util.concurrent.Executors;

public class LoginFragment extends Fragment implements LoginView {

    private static final String TAG = "LoginFragment";

    private LoginPresenterImpl presenter;
    private CredentialManager credentialManager;
    private EditText etEmail, etPassword;
    private ImageView btnTogglePassword;
    private Button btnLogin;
    private CardView btnGoogle;
    private TextView btnSignup, btnGuest;
    private boolean isPasswordVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        credentialManager = CredentialManager.create(requireContext());
        initViews(view);
        setupPresenter();
        setupClickListeners(view);
        return view;
    }

    private void initViews(View view) {
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnTogglePassword = view.findViewById(R.id.btn_toggle_password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnGoogle = view.findViewById(R.id.btn_google);
        btnSignup = view.findViewById(R.id.btn_signup);
        btnGuest = view.findViewById(R.id.btn_guest);
    }

    private void setupPresenter() {
        FavoritesRepositoryImpl favoritesRepo = FavoritesRepositoryImpl.getInstance(requireActivity().getApplication());
        PlanRepositoryImpl planRepo = PlanRepositoryImpl.getInstance(requireActivity().getApplication());
        MealRemoteDataSource remoteSource = MealRemoteDataSource.getInstance();
        MealRepositoryImpl mealRepo = MealRepositoryImpl.getInstance(remoteSource);
        com.aalaa.foodplanner.datasource.db.AppDatabase db = com.aalaa.foodplanner.datasource.db.AppDatabase
                .getInstance(requireActivity().getApplication());
        SyncRepositoryImpl syncRepo = SyncRepositoryImpl.getInstance(favoritesRepo, planRepo, mealRepo,
                db.pendingActionDao());
        AuthRepositoryImpl authRepo = new AuthRepositoryImpl();
        com.aalaa.foodplanner.data.local.SessionManager sessionManager = com.aalaa.foodplanner.data.local.SessionManager
                .getInstance(requireContext());
        presenter = new LoginPresenterImpl(authRepo, syncRepo, sessionManager);
        presenter.attachView(this);
    }

    private void setupClickListeners(View view) {
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            presenter.login(email, password);
        });
        btnSignup.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_login_to_signup));
        btnGuest.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_login_to_main));
        btnGoogle.setOnClickListener(v -> signInWithGoogle());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(
                    android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye);
        } else {
            etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                    | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye_off);
        }
        isPasswordVisible = !isPasswordVisible;
        etPassword.setSelection(etPassword.getText().length());
    }

    private void signInWithGoogle() {
        showLoading();
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(true)
                .build();
        GetCredentialRequest request = new GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build();
        CancellationSignal cancellationSignal = new CancellationSignal();
        credentialManager.getCredentialAsync(requireContext(), request, cancellationSignal,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        requireActivity().runOnUiThread(() -> handleGoogleSignInResult(result));
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        requireActivity().runOnUiThread(() -> handleGoogleSignInError(e));
                    }
                });
    }

    private void handleGoogleSignInResult(GetCredentialResponse response) {
        Credential credential = response.getCredential();
        if (credential instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) credential;
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(customCredential.getType())) {
                try {
                    GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(customCredential.getData());
                    String idToken = googleIdTokenCredential.getIdToken();
                    Log.d(TAG, "Google Sign-In successful");
                    presenter.loginWithGoogle(idToken);
                } catch (Exception e) {
                    Log.e(TAG, "Error extracting Google ID token", e);
                    hideLoading();
                    showError("Failed to process Google Sign-In");
                }
            } else {
                hideLoading();
            }
        }
    }

    private void handleGoogleSignInError(GetCredentialException e) {
        hideLoading();
        Log.e(TAG, "Google Sign-In error", e);
        String errorMessage = (e instanceof NoCredentialException) ? "No Google accounts found"
                : "Google Sign-In failed";
        showError(errorMessage);
    }

    @Override
    public void showEmailError(String error) {
        etEmail.setError(error);
        etEmail.requestFocus();
    }

    @Override
    public void showPasswordError(String error) {
        etPassword.setError(error);
        etPassword.requestFocus();
    }

    @Override
    public void clearErrors() {
        etEmail.setError(null);
        etPassword.setError(null);
    }

    @Override
    public void onLoginSuccess() {
        AppSnack.showSuccess(requireView(), "Login Success");
        Navigation.findNavController(requireView()).navigate(R.id.action_login_to_main);
    }

    @Override
    public void showLoading() {
        btnLogin.setEnabled(false);
        btnGoogle.setEnabled(false);
        btnLogin.setText("Logging in...");
    }

    @Override
    public void hideLoading() {
        btnLogin.setEnabled(true);
        btnGoogle.setEnabled(true);
        btnLogin.setText(R.string.login);
    }

    @Override
    public void showError(String message) {
        AppSnack.showError(requireView(), message);
    }

    @Override
    public void showMigrationDialog() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Guest Data Found")
                .setMessage(
                        "You have local data from your guest session. do you want to keep it and merge with your account?")
                .setPositiveButton("Yes (Merge)", (dialog, which) -> presenter.migrateGuestData())
                .setNegativeButton("No (Clear Local)", (dialog, which) -> presenter.clearAndRestoreData())
                .setCancelable(false)
                .show();
    }

    @Override
    public void showSuccess(String message) {
        AppSnack.showSuccess(requireView(), message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null)
            presenter.detachView();
    }
}