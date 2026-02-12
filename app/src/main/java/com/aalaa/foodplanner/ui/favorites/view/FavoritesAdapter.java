package com.aalaa.foodplanner.ui.favorites.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private Context context;
    private List<MealsItem> favorites;
    private OnFavouriteClickListener listener;

    public FavoritesAdapter(Context context, OnFavouriteClickListener listener) {
        this.context = context;
        this.favorites = new ArrayList<>();
        this.listener = listener;
    }

    public void setFavorites(List<MealsItem> favorites) {
        this.favorites = favorites;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealsItem favoriteMealEntity = favorites.get(position);

        holder.tvMealName.setText(favoriteMealEntity.getStrMeal());

        String categoryArea = "";
        if (favoriteMealEntity.getStrCategory() != null)
            categoryArea += favoriteMealEntity.getStrCategory();
        if (favoriteMealEntity.getStrArea() != null) {
            if (!categoryArea.isEmpty())
                categoryArea += " | ";
            categoryArea += favoriteMealEntity.getStrArea();
        }
        holder.tvCategoryArea.setText(categoryArea);

        Glide.with(context)
                .load(favoriteMealEntity.getStrMealThumb())
                .placeholder(R.drawable.avatar_placeholder)
                .error(R.drawable.avatar_placeholder)
                .into(holder.ivMealThumb);

        holder.btnFavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveClick(favoriteMealEntity);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMealClick(favoriteMealEntity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favorites != null ? favorites.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivMealThumb;
        TextView tvMealName;
        TextView tvCategoryArea;
        ImageView btnFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMealThumb = itemView.findViewById(R.id.iv_meal_thumb);
            tvMealName = itemView.findViewById(R.id.tv_meal_name);
            tvCategoryArea = itemView.findViewById(R.id.tv_category_area);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
        }
    }
}