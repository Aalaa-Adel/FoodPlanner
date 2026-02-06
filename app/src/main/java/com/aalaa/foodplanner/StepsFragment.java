package com.aalaa.foodplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class StepsFragment extends Fragment {

    private RecyclerView rvSteps;
    private StepsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        rvSteps = view.findViewById(R.id.rv_steps);

        setupRecyclerView();
        loadSteps();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new StepsAdapter(new ArrayList<>());
        rvSteps.setAdapter(adapter);
    }

    private void loadSteps() {
        // TODO: Load steps from database/API
        // Sample data
        List<String> steps = new ArrayList<>();
        steps.add("Heat olive oil in a large pan over medium heat.");
        steps.add("Add minced garlic and sauté until fragrant, about 1 minute.");
        steps.add("Add sliced mushrooms and cook until golden brown, 5-7 minutes.");
        steps.add("Cook pasta according to package instructions until al dente.");
        steps.add("Drain pasta and add to the mushroom mixture.");
        steps.add("Drizzle truffle oil over the pasta and toss to combine.");
        steps.add("Season with salt and pepper to taste.");
        steps.add("Serve hot, garnished with grated Parmesan cheese.");

        adapter.updateSteps(steps);
    }
}