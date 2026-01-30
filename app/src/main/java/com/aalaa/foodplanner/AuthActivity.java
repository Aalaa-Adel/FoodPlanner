package com.aalaa.foodplanner;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Button login = findViewById(R.id.btnLogin);
        Button signUp = findViewById(R.id.btnSignUp);
        Button guest = findViewById(R.id.btnGuest);

        login.setOnClickListener(v -> {
            // TODO: Open Login screen
        });

        signUp.setOnClickListener(v -> {
            // TODO: Open SignUp screen
        });

        guest.setOnClickListener(v -> {
            // Open HomeActivity as guest
            startActivity(new Intent(AuthActivity.this, HomeActivity.class));
            finish();
        });
    }
}
