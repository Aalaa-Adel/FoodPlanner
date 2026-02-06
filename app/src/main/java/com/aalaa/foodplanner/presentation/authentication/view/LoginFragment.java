package com.aalaa.foodplanner.presentation.authentication.view;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.aalaa.foodplanner.presentation.authentication.AuthContract;
import com.aalaa.foodplanner.presentation.authentication.presenter.LoginPresenter;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executors;

public class LoginFragment extends Fragment implements AuthContract.LoginView {

    private static final String TAG = "LoginFragment";

    private LoginPresenter presenter;

    private CredentialManager credentialManager;
    private EditText etEmail, etPassword;
    private ImageView btnTogglePassword;
    private Button btnLogin;
    private CardView btnGoogle, btnFacebook, btnTwitter;
    private TextView btnSignup, btnGuest;
    private ProgressBar progressBar;

    private boolean isPasswordVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        credentialManager = CredentialManager.create(requireContext());

        initViews(view);
        setupClickListeners(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new LoginPresenter();
        presenter.attachView(this);
    }

    private void initViews(View view) {
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnTogglePassword = view.findViewById(R.id.btn_toggle_password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnGoogle = view.findViewById(R.id.btn_google);
        btnFacebook = view.findViewById(R.id.btn_facebook);
        btnTwitter = view.findViewById(R.id.btn_twitter);
        btnSignup = view.findViewById(R.id.btn_signup);
        btnGuest = view.findViewById(R.id.btn_guest);
        // progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupClickListeners(View view) {
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            presenter.login(email, password);
        });

        btnSignup.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_login_to_signup));

        btnGuest.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_login_to_main));

        btnGoogle.setOnClickListener(v -> signInWithGoogle());

        btnFacebook.setOnClickListener(v -> Toast.makeText(requireContext(),
                "Facebook Sign-In - Coming Soon",
                Toast.LENGTH_SHORT).show());

        btnTwitter.setOnClickListener(v -> Toast.makeText(requireContext(),
                "Twitter Sign-In - Coming Soon",
                Toast.LENGTH_SHORT).show());
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
                    Log.d(TAG, "User Email: " + googleIdTokenCredential.getId());

                    firebaseAuthWithGoogle(idToken);

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

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    hideLoading();
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        showSuccess("Google Sign-In successful");
                        Navigation.findNavController(requireView()).navigate(R.id.action_login_to_main);
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        showError("Firebase authentication failed: " + task.getException().getMessage());
                    }
                });
    }

    private void handleGoogleSignInError(GetCredentialException e) {
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
    public void showEmailError(String error) {
        // TODO: Implement error showing properly
    }

    @Override
    public void showPasswordError(String error) {
        // TODO: Implement error showing properly
    }

    @Override
    public void clearErrors() {
        // TODO: Implement clear errors
    }

    @Override
    public void onLoginSuccess() {
        Toast.makeText(requireContext(), "Login Success", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView())
                .navigate(R.id.action_login_to_main);
    }

    @Override
    public void showLoading() {
        // progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        btnGoogle.setEnabled(false);
        btnFacebook.setEnabled(false);
        btnTwitter.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        // progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
        btnGoogle.setEnabled(true);
        btnFacebook.setEnabled(true);
        btnTwitter.setEnabled(true);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSuccess(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }
}