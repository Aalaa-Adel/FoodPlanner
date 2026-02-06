package com.aalaa.foodplanner;

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

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executors;

public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";

    private FirebaseAuth auth;
    private CredentialManager credentialManager;
    private EditText etEmail, etPassword, etConfirmPassword;
    private ImageView btnTogglePassword, btnToggleConfirmPassword;
    private Button btnSignupAction;
    private CardView btnGoogle, btnFacebook, btnTwitter;
    private TextView btnLogin, btnGuest;
    private ProgressBar progressBar;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        auth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(requireContext());

        initViews(view);
        setupClickListeners(view);

        return view;
    }

    private void initViews(View view) {
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnTogglePassword = view.findViewById(R.id.btn_toggle_password);
        btnToggleConfirmPassword = view.findViewById(R.id.btn_toggle_confirm_password);
        btnSignupAction = view.findViewById(R.id.btn_signup_action);
        btnGoogle = view.findViewById(R.id.btn_google);
        btnFacebook = view.findViewById(R.id.btn_facebook);
        btnTwitter = view.findViewById(R.id.btn_twitter);
        btnLogin = view.findViewById(R.id.btn_login);
        btnGuest = view.findViewById(R.id.btn_guest);

        // Add ProgressBar programmatically or in XML
        // progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupClickListeners(View view) {
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
        btnToggleConfirmPassword.setOnClickListener(v -> toggleConfirmPasswordVisibility());

        btnSignupAction.setOnClickListener(v -> signUpWithEmail());

        btnLogin.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_signup_to_login));

        btnGuest.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_signup_to_home));

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

    private void signUpWithEmail() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(requireContext(),
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(requireContext(),
                    "Passwords do not match",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    showLoading(false);

                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        Toast.makeText(requireContext(),
                                "Account Created Successfully",
                                Toast.LENGTH_SHORT).show();

                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_signup_to_home);

                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(requireContext(),
                                "Sign up failed: " +
                                        task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signInWithGoogle() {
        showLoading(true);

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
                    Log.d(TAG, "User Email: " + googleIdTokenCredential.getId());

                    firebaseAuthWithGoogle(idToken);

                } catch (Exception e) {
                    Log.e(TAG, "Error extracting Google ID token", e);
                    showLoading(false);
                    Toast.makeText(requireContext(),
                            "Failed to process Google Sign-In",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Unexpected credential type");
                showLoading(false);
            }
        }
    }

    private void handleSignInError(GetCredentialException e) {
        showLoading(false);
        Log.e(TAG, "Google Sign-In error", e);

        String errorMessage;
        if (e instanceof NoCredentialException) {
            errorMessage = "No Google accounts found. Please add a Google account.";
        } else if (e.getMessage() != null && e.getMessage().contains("cancelled")) {
            errorMessage = "Sign-in cancelled";
        } else {
            errorMessage = "Google Sign-In failed: " + e.getMessage();
        }

        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    showLoading(false);

                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");

                        if (auth.getCurrentUser() != null) {
                            Log.d(TAG, "User: " + auth.getCurrentUser().getEmail());
                        }

                        Toast.makeText(requireContext(),
                                "Google Sign-In successful",
                                Toast.LENGTH_SHORT).show();

                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_signup_to_home);

                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(requireContext(),
                                "Firebase authentication failed: " +
                                        task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        // TODO: Implement loading state
        // progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSignupAction.setEnabled(!show);
        btnGoogle.setEnabled(!show);
        btnFacebook.setEnabled(!show);
        btnTwitter.setEnabled(!show);
    }
}