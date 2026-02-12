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
import com.aalaa.foodplanner.data.repository.AuthRepositoryImpl;
import com.aalaa.foodplanner.ui.authentication.presenter.SignUpPresenterImpl;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import java.util.concurrent.Executors;

public class SignUpFragment extends Fragment implements SignUpView {

    private static final String TAG = "SignUpFragment";

    private SignUpPresenterImpl presenter;
    private CredentialManager credentialManager;

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private ImageView btnTogglePassword, btnToggleConfirmPassword;
    private Button btnSignupAction;
    private CardView btnGoogle;
    private TextView btnLogin, btnGuest;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        credentialManager = CredentialManager.create(requireContext());

        initViews(view);
        setupPresenter();
        setupClickListeners(view);

        return view;
    }

    private void initViews(View view) {
        etUsername = view.findViewById(R.id.et_username);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnTogglePassword = view.findViewById(R.id.btn_toggle_password);
        btnToggleConfirmPassword = view.findViewById(R.id.btn_toggle_confirm_password);
        btnSignupAction = view.findViewById(R.id.btn_signup_action);
        btnGoogle = view.findViewById(R.id.btn_google);
        btnLogin = view.findViewById(R.id.btn_login);
        btnGuest = view.findViewById(R.id.btn_guest);
    }

    private void setupPresenter() {
        AuthRepositoryImpl repository = new AuthRepositoryImpl();
        presenter = new SignUpPresenterImpl(repository);
        presenter.attachView(this);
    }

    private void setupClickListeners(View view) {
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
        btnToggleConfirmPassword.setOnClickListener(v -> toggleConfirmPasswordVisibility());

        btnSignupAction.setOnClickListener(v -> signUp());

        btnLogin.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_signup_to_login));

        btnGuest.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_signup_to_main));

        btnGoogle.setOnClickListener(v -> signInWithGoogle());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(
                    android.text.InputType.TYPE_CLASS_TEXT |
                            android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye);
        } else {
            etPassword.setInputType(
                    android.text.InputType.TYPE_CLASS_TEXT |
                            android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye_off);
        }
        isPasswordVisible = !isPasswordVisible;
        etPassword.setSelection(etPassword.getText().length());
    }

    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            etConfirmPassword.setInputType(
                    android.text.InputType.TYPE_CLASS_TEXT |
                            android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye);
        } else {
            etConfirmPassword.setInputType(
                    android.text.InputType.TYPE_CLASS_TEXT |
                            android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_off);
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        etConfirmPassword.setSelection(etConfirmPassword.getText().length());
    }

    private void signUp() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        presenter.signUp(username, email, password, confirmPassword);
    }

    private void signInWithGoogle() {
        showLoading();

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(true)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        CancellationSignal cancellationSignal = new CancellationSignal();

        credentialManager.getCredentialAsync(
                requireContext(),
                request,
                cancellationSignal,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        requireActivity().runOnUiThread(() -> handleSignInResult(result));
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        requireActivity().runOnUiThread(() -> handleSignInError(e));
                    }
                });
    }

    private void handleSignInResult(GetCredentialResponse response) {
        Credential credential = response.getCredential();

        if (credential instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) credential;

            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(
                    customCredential.getType())) {
                try {
                    GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(customCredential.getData());
                    String idToken = googleIdTokenCredential.getIdToken();

                    Log.d(TAG, "Google Sign-In successful");
                    presenter.signUpWithGoogle(idToken);

                } catch (Exception e) {
                    Log.e(TAG, "Error extracting Google ID token", e);
                    hideLoading();
                    showError("Failed to process Google Sign-In");
                }
            } else {
                Log.e(TAG, "Unexpected credential type");
                hideLoading();
            }
        }
    }

    private void handleSignInError(GetCredentialException e) {
        hideLoading();
        Log.e(TAG, "Google Sign-In error", e);

        String errorMessage;
        if (e instanceof NoCredentialException) {
            errorMessage = "No Google accounts found. Please add a Google account.";
        } else if (e.getMessage() != null && e.getMessage().contains("cancelled")) {
            errorMessage = "Sign-in cancelled";
        } else {
            errorMessage = "Google Sign-In failed: " + e.getMessage();
        }

        showError(errorMessage);
    }


    @Override
    public void showUsernameError(String error) {
        etUsername.setError(error);
        etUsername.requestFocus();
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
    public void showConfirmPasswordError(String error) {
        etConfirmPassword.setError(error);
        etConfirmPassword.requestFocus();
    }

    @Override
    public void clearErrors() {
        etUsername.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
    }

    @Override
    public void onSignUpSuccess() {
        AppSnack.showSuccess(requireView(), "Account Created Successfully");
        Navigation.findNavController(requireView())
                .navigate(R.id.action_signup_to_main);
    }

    @Override
    public void showLoading() {
        btnSignupAction.setEnabled(false);
        btnGoogle.setEnabled(false);
        btnSignupAction.setText("Creating Account...");
    }

    @Override
    public void hideLoading() {
        btnSignupAction.setEnabled(true);
        btnGoogle.setEnabled(true);
        btnSignupAction.setText(R.string.sign_up);
    }

    @Override
    public void showError(String message) {
        AppSnack.showError(requireView(), message);
    }

    @Override
    public void showSuccess(String message) {
        AppSnack.showSuccess(requireView(), message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.detachView();
        }
    }
}