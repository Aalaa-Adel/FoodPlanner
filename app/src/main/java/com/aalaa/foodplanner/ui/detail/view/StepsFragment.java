package com.aalaa.foodplanner.ui.detail.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.domain.models.MealsItem;

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
        Fragment parent = getParentFragment();
        if (parent instanceof RecipeDetailFragment) {
            MealsItem meal = ((RecipeDetailFragment) parent).getCurrentMeal();
            if (meal != null) {
                updateSteps(meal);
            }
        }
    }

    public void updateSteps(MealsItem meal) {
        if (meal != null && meal.getStrInstructions() != null) {
            List<String> steps = parseInstructions(meal.getStrInstructions());
            if (adapter != null) {
                adapter.updateSteps(steps);
            }
        }
    }

    private List<String> parseInstructions(String instructions) {

        if (instructions == null || instructions.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String[] parts = instructions.split("\\r?\\n|\\. ");

        List<String> steps = new ArrayList<>();

        for (String step : parts) {
            if (!step.trim().isEmpty()) {
                steps.add(step.trim());
            }
        }

        return steps;
    }
}