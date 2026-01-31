package com.aalaa.foodplanner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class OnboardingActivity extends AppCompatActivity {

    private ImageView img;
    private TextView title;
    private TextView subtitle;
    private AppCompatButton nextBtn;
    private View[] progressDots;

    private int currentIndex = 0;

    private final int[] images = {
            R.drawable.onboarding1,
            R.drawable.onboarding2,
            R.drawable.onboarding3
    };

    private String[] titles;
    private String[] subtitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        titles = new String[]{
                getString(R.string.title_onboarding1),
                getString(R.string.title_onboarding2),
                getString(R.string.title_onboarding3)
        };

        subtitles = new String[]{
                getString(R.string.subtitle_onboarding1),
                getString(R.string.subtitle_onboarding2),
                getString(R.string.subtitle_onboarding3)
        };

        img = findViewById(R.id.onboarding_image);
        title = findViewById(R.id.onboarding_title);
        subtitle = findViewById(R.id.onboarding_subtitle);
        nextBtn = findViewById(R.id.btn_next);
        TextView skip = findViewById(R.id.tv_skip);

        progressDots = new View[]{
                findViewById(R.id.progress_0),
                findViewById(R.id.progress_1),
                findViewById(R.id.progress_2)
        };

        loadSlide();

        nextBtn.setOnClickListener(v -> {
            if(currentIndex < images.length - 1){
                currentIndex++;
                loadSlide();
            } else {
                navigateToAuth();
            }
        });

        skip.setOnClickListener(v -> navigateToAuth());
    }

    private void loadSlide() {
        img.setImageResource(images[currentIndex]);
        title.setText(titles[currentIndex]);
        subtitle.setText(subtitles[currentIndex]);

        if(currentIndex == images.length - 1){
            nextBtn.setText(R.string.get_started);
        } else {
            nextBtn.setText(R.string.next);
        }

        updateProgressDots();
    }

    private void updateProgressDots() {
        for(int i = 0; i < progressDots.length; i++){
            ViewGroup.LayoutParams params = progressDots[i].getLayoutParams();

            if(i == currentIndex){
                params.width = dpToPx(40);
                params.height = dpToPx(6);
                progressDots[i].setBackgroundResource(R.drawable.progress_indicator_active);
            } else {
                params.width = dpToPx(25);
                params.height = dpToPx(6);
                progressDots[i].setBackgroundResource(R.drawable.progress_indicator_inactive);
            }

            progressDots[i].setLayoutParams(params);
        }
    }

    private void navigateToAuth(){
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private int dpToPx(int dp){
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}