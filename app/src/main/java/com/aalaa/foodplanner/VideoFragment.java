package com.aalaa.foodplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class VideoFragment extends Fragment {

    private ImageView ivVideoThumbnail;
    private CardView btnPlayVideo;
    private TextView tvVideoDuration;
    private TextView tvVideoDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        ivVideoThumbnail = view.findViewById(R.id.iv_video_thumbnail);
        btnPlayVideo = view.findViewById(R.id.btn_play_video);
        tvVideoDuration = view.findViewById(R.id.tv_video_duration);
        tvVideoDescription = view.findViewById(R.id.tv_video_description);

        setupClickListeners();
        loadVideoData();

        return view;
    }

    private void setupClickListeners() {
        btnPlayVideo.setOnClickListener(v -> playVideo());
    }

    private void loadVideoData() {
        // TODO: Load video data from database/API
        // Sample data
        tvVideoDuration.setText("3:45");
        tvVideoDescription.setText("Watch the complete step-by-step cooking tutorial for this delicious Truffle Mushroom Pasta recipe.");
    }

    private void playVideo() {
        // TODO: Implement video playback
        android.widget.Toast.makeText(getContext(),
                "Playing video...",
                android.widget.Toast.LENGTH_SHORT).show();
    }
}