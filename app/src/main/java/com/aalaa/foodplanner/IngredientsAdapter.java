package com.aalaa.foodplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private List<Ingredient> ingredients;

    public IngredientsAdapter(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);

        holder.tvIngredientName.setText(ingredient.getName());
        holder.tvIngredientQuantity.setText(ingredient.getQuantity());

        if (ingredient.getNote() != null && !ingredient.getNote().isEmpty()) {
            holder.tvIngredientNote.setText(ingredient.getNote());
            holder.tvIngredientNote.setVisibility(View.VISIBLE);
        } else {
            holder.tvIngredientNote.setVisibility(View.GONE);
        }

        // TODO: Load ingredient image
        // Glide.with(holder.ivIngredientImage.getContext())
        //      .load(ingredient.getImageUrl())
        //      .into(holder.ivIngredientImage);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void updateIngredients(List<Ingredient> newIngredients) {
        this.ingredients = newIngredients;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIngredientImage;
        TextView tvIngredientName;
        TextView tvIngredientNote;
        TextView tvIngredientQuantity;

        ViewHolder(View itemView) {
            super(itemView);
            ivIngredientImage = itemView.findViewById(R.id.iv_ingredient_image);
            tvIngredientName = itemView.findViewById(R.id.tv_ingredient_name);
            tvIngredientNote = itemView.findViewById(R.id.tv_ingredient_note);
            tvIngredientQuantity = itemView.findViewById(R.id.tv_ingredient_quantity);
        }
    }
}