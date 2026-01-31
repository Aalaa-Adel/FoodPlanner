package com.aalaa.foodplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class SignUpFragment extends Fragment {

    public SignUpFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView btnLogin = view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_signup_to_login)
        );

        TextView btnGuest = view.findViewById(R.id.btn_guest);
        btnGuest.setOnClickListener(v -> {
        });
    }
}
