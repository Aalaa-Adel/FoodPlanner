package com.aalaa.foodplanner.ui.detail.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.domain.models.Ingredients;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private List<Ingredients> ingredients;

    public IngredientsAdapter(List<Ingredients> ingredients) {
        this.ingredients = ingredients != null ? ingredients : Collections.emptyList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredients ingredient = ingredients.get(position);

        holder.tvIngredientName.setText(ingredient.getStrIngredient());
        holder.tvIngredientQuantity.setText(ingredient.getStrDescription());

        String imageUrl = "https://www.themealdb.com/images/ingredients/" + ingredient.getStrIngredient() + ".png";

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.avatar_placeholder)
                .error(R.drawable.avatar_placeholder)
                .into(holder.ivIngredientImage);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void updateIngredients(List<Ingredients> newIngredients) {
        this.ingredients = newIngredients != null ? newIngredients : Collections.emptyList();
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