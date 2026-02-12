package com.aalaa.foodplanner.ui.detail.view;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.domain.models.Ingredients;
import com.aalaa.foodplanner.domain.models.MealsItem;

import java.util.ArrayList;
import java.util.List;

public class IngredientsFragment extends Fragment {

    private RecyclerView rvIngredients;
    private IngredientsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);

        rvIngredients = view.findViewById(R.id.rv_ingredients);

        setupRecyclerView();
        loadIngredients();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new IngredientsAdapter(new ArrayList<>());
        rvIngredients.setAdapter(adapter);
    }

    private void loadIngredients() {
        Fragment parent = getParentFragment();
        if (parent instanceof RecipeDetailFragment) {
            MealsItem meal = ((RecipeDetailFragment) parent).getCurrentMeal();
            if (meal != null) {
                updateIngredients(meal);
            }
        }
    }

    public void updateIngredients(MealsItem meal) {
        if (meal == null)
            return;

        List<Pair<String, String>> pairs = meal.getingredients();
        List<Ingredients> result = new ArrayList<>();

        for (Pair<String, String> pair : pairs) {
            if (pair.first != null && !pair.first.trim().isEmpty()) {
                Ingredients ing = new Ingredients();
                ing.setStrIngredient(pair.first);
                ing.setStrDescription(pair.second);
                result.add(ing);
            }
        }
        if (adapter != null) {
            adapter.updateIngredients(result);
        }
    }

    private void loadSampleData() {
    }
}