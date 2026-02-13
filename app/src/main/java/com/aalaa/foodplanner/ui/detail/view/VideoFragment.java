package com.aalaa.foodplanner.ui.detail.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.bumptech.glide.Glide;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class VideoFragment extends Fragment {

    private YouTubePlayerView youtubePlayerView;
    private YouTubePlayer activePlayer;

    private ImageView ivThumb;
    private ImageView btnPlayOverlay;
    private TextView tvVideoDescription;

    private String currentVideoId;
    private boolean isPlayerReady = false;
    private boolean pendingPlay = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_video, container, false);

        youtubePlayerView = view.findViewById(R.id.youtube_player_view);
        ivThumb = view.findViewById(R.id.iv_video_thumb);
        btnPlayOverlay = view.findViewById(R.id.btn_play_overlay);
        tvVideoDescription = view.findViewById(R.id.tv_video_description);

        getLifecycle().addObserver(youtubePlayerView);

        IFramePlayerOptions options = new IFramePlayerOptions.Builder(requireContext())
                .controls(1)
                .fullscreen(0)
                .ccLoadPolicy(0)
                .build();

        youtubePlayerView.setVisibility(View.INVISIBLE);

        youtubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                android.util.Log.d("VideoFragment", "YouTube player ready");
                activePlayer = youTubePlayer;
                isPlayerReady = true;
                if (pendingPlay && currentVideoId != null) {
                    android.util.Log.d("VideoFragment", "Executing pending play for ID: " + currentVideoId);
                    activePlayer.loadVideo(currentVideoId, 0f);
                    pendingPlay = false;
                }
            }

            @Override
            public void onError(@NonNull YouTubePlayer youTubePlayer,
                    @NonNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerError error) {
                android.util.Log.e("VideoFragment", "YouTube player error: " + error.name());
                isPlayerReady = false;
            }
        }, options);

        btnPlayOverlay.setOnClickListener(v -> playVideoWithSound());

        loadVideoData();

        return view;
    }

    private void loadVideoData() {
        Fragment parent = getParentFragment();
        if (parent instanceof RecipeDetailFragment) {
            MealsItem meal = ((RecipeDetailFragment) parent).getCurrentMeal();
            if (meal != null)
                updateVideo(meal);
        }
    }

    public void updateVideo(MealsItem meal) {
        if (meal == null)
            return;

        String name = meal.getStrMeal();
        if (tvVideoDescription != null) {
            tvVideoDescription.setText(
                    "Watch the complete step-by-step cooking tutorial for " + (name != null ? name : "this recipe"));
        }

        String thumb = meal.getStrMealThumb();
        if (thumb != null && !thumb.trim().isEmpty()) {
            Glide.with(requireContext()).load(thumb).into(ivThumb);
        }

        String youtubeUrl = meal.getStrYoutube();
        if (youtubeUrl == null || youtubeUrl.trim().isEmpty()) {
            hideAllVideoUI();
            if (tvVideoDescription != null)
                tvVideoDescription.setText("No video tutorial available for this recipe.");
            return;
        }

        currentVideoId = extractYouTubeId(youtubeUrl);
        if (currentVideoId == null) {
            hideAllVideoUI();
            if (tvVideoDescription != null)
                tvVideoDescription.setText("No valid video found for this recipe.");
            return;
        }

        showOverlay();
    }

    private void playVideoWithSound() {
        if (currentVideoId == null) {
            android.util.Log.w("VideoFragment", "playVideoWithSound called but currentVideoId is null");
            return;
        }

        youtubePlayerView.setVisibility(View.VISIBLE);
        ivThumb.setVisibility(View.GONE);
        btnPlayOverlay.setVisibility(View.GONE);

        if (isPlayerReady && activePlayer != null) {
            android.util.Log.d("VideoFragment", "Playing video immediately: " + currentVideoId);
            activePlayer.loadVideo(currentVideoId, 0f);
        } else {
            android.util.Log.d("VideoFragment", "Player not ready, setting pendingPlay flag");
            pendingPlay = true;
        }
    }

    private void showOverlay() {
        youtubePlayerView.setVisibility(View.INVISIBLE);
        ivThumb.setVisibility(View.VISIBLE);
        btnPlayOverlay.setVisibility(View.VISIBLE);
    }

    private void hideAllVideoUI() {
        youtubePlayerView.setVisibility(View.GONE);
        ivThumb.setVisibility(View.GONE);
        btnPlayOverlay.setVisibility(View.GONE);
    }

    private String extractYouTubeId(String url) {
        if (url == null || url.trim().isEmpty())
            return null;

        // Comprehensive regex for various YouTube URL patterns:
        // - standard: youtube.com/watch?v=ID
        // - shortened: youtu.be/ID
        // - embed: youtube.com/embed/ID
        // - shorts: youtube.com/shorts/ID
        // - direct ID (11 chars)
        String pattern = "(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=|shorts\\/)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})";

        java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        }

        // Fallback for just the ID if the full URL wasn't matched but looks like an ID
        if (url.length() == 11 && url.matches("[a-zA-Z0-9_-]{11}")) {
            return url;
        }

        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (activePlayer != null)
            activePlayer.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (youtubePlayerView != null)
            youtubePlayerView.release();
        activePlayer = null;
        isPlayerReady = false;
    }
}
