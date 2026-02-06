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
        // TODO: Load ingredients from database/API
        // Sample data
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Fresh Ingredient 1", "100g", null));
        ingredients.add(new Ingredient("Fresh Ingredient 2", "100g", null));
        ingredients.add(new Ingredient("Mushrooms", "200g", "Sliced"));
        ingredients.add(new Ingredient("Pasta", "250g", "Any type"));
        ingredients.add(new Ingredient("Truffle Oil", "2 tbsp", null));
        ingredients.add(new Ingredient("Garlic", "3 cloves", "Minced"));
        ingredients.add(new Ingredient("Parmesan", "50g", "Grated"));

        adapter.updateIngredients(ingredients);
    }
}