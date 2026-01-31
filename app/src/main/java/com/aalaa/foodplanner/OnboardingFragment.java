package com.aalaa.foodplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class OnboardingFragment extends Fragment {

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);

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

        img = view.findViewById(R.id.onboarding_image);
        title = view.findViewById(R.id.onboarding_title);
        subtitle = view.findViewById(R.id.onboarding_subtitle);
        nextBtn = view.findViewById(R.id.btn_next);
        TextView skip = view.findViewById(R.id.tv_skip);

        progressDots = new View[]{
                view.findViewById(R.id.progress_0),
                view.findViewById(R.id.progress_1),
                view.findViewById(R.id.progress_2)
        };

        loadSlide();

        nextBtn.setOnClickListener(v -> {
            if (currentIndex < images.length - 1) {
                currentIndex++;
                loadSlide();
            } else {
                navigateToAuth();
            }
        });

        skip.setOnClickListener(v -> navigateToAuth());

        return view;
    }

    private void loadSlide() {

        img.setImageResource(images[currentIndex]);
        title.setText(titles[currentIndex]);
        subtitle.setText(subtitles[currentIndex]);

        nextBtn.setText(
                currentIndex == images.length - 1
                        ? R.string.get_started
                        : R.string.next
        );

        updateProgressDots();
    }

    private void updateProgressDots() {

        for (int i = 0; i < progressDots.length; i++) {

            ViewGroup.LayoutParams params = progressDots[i].getLayoutParams();

            params.width = dpToPx(i == currentIndex ? 40 : 25);
            params.height = dpToPx(6);

            progressDots[i].setBackgroundResource(
                    i == currentIndex
                            ? R.drawable.progress_indicator_active
                            : R.drawable.progress_indicator_inactive
            );

            progressDots[i].setLayoutParams(params);
        }
    }

    private void navigateToAuth() {

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_onboarding_to_login);
    }

    private int dpToPx(int dp) {
        return (int) (dp * requireContext().getResources().getDisplayMetrics().density);
    }
}
