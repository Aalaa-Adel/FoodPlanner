package com.aalaa.foodplanner;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView appName = findViewById(R.id.appName);
        TextView appTagline = findViewById(R.id.appTagline);
        LottieAnimationView lottie = findViewById(R.id.lottieView);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        appName.startAnimation(fadeIn);
        appTagline.startAnimation(fadeIn);

        ObjectAnimator appNamePulse = ObjectAnimator.ofArgb(
                appName, "textColor",
                Color.WHITE,
                Color.parseColor("#FFE5D9")
        );
        appNamePulse.setDuration(1500);
        appNamePulse.setRepeatMode(ValueAnimator.REVERSE);
        appNamePulse.setRepeatCount(ValueAnimator.INFINITE);
        appNamePulse.start();

        ObjectAnimator taglinePulse = ObjectAnimator.ofArgb(
                appTagline, "textColor",
                Color.WHITE,
                Color.parseColor("#FFE5D9")
        );
        taglinePulse.setDuration(1500);
        taglinePulse.setRepeatMode(ValueAnimator.REVERSE);
        taglinePulse.setRepeatCount(ValueAnimator.INFINITE);
        taglinePulse.start();

        ObjectAnimator lottieScaleX = ObjectAnimator.ofFloat(lottie, "scaleX", 0.95f, 1.05f);
        lottieScaleX.setDuration(1000);
        lottieScaleX.setRepeatMode(ValueAnimator.REVERSE);
        lottieScaleX.setRepeatCount(ValueAnimator.INFINITE);
        lottieScaleX.start();

        ObjectAnimator lottieScaleY = ObjectAnimator.ofFloat(lottie, "scaleY", 0.95f, 1.05f);
        lottieScaleY.setDuration(1000);
        lottieScaleY.setRepeatMode(ValueAnimator.REVERSE);
        lottieScaleY.setRepeatCount(ValueAnimator.INFINITE);
        lottieScaleY.start();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DELAY);
    }
}