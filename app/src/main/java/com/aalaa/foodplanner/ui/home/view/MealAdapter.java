package com.aalaa.foodplanner.ui.home.view;

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
import com.aalaa.foodplanner.ui.listing.view.OnMealClickListener;
import com.bumptech.glide.Glide;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {

    private final Context context;
    private final List<MealsItem> meals;
    private final OnHomeMealClickListener listener;

    public MealAdapter(Context context, List<MealsItem> meals, OnHomeMealClickListener listener) {
        this.context = context;
        this.meals = meals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealsItem meal = meals.get(position);

        holder.tvMealName.setText(meal.getStrMeal());

        if (meal.getStrCategory() != null) {
            holder.categoryBadge.setText(meal.getStrCategory());
        }

        Glide.with(context)
                .load(meal.getStrMealThumb())
                .placeholder(R.drawable.avatar_placeholder)
                .into(holder.mealImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMealClick(meal);
            }
        });

        holder.btnBookmark.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookmarkClick(meal, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImage;
        TextView tvMealName;
        TextView categoryBadge;
        ImageView btnBookmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.meal_image);
            tvMealName = itemView.findViewById(R.id.tv_meal_name);
            categoryBadge = itemView.findViewById(R.id.category_badge);
            btnBookmark = itemView.findViewById(R.id.btn_bookmark);
        }
    }
}