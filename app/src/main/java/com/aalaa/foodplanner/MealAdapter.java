package com.aalaa.foodplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.data.model.MealsItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<MealsItem> meals;
    private OnMealClickListener listener;

    public interface OnMealClickListener {
        void onMealClick(MealsItem meal);
    }

    public MealAdapter() {
        this.meals = new ArrayList<>();
    }

    public void setMeals(List<MealsItem> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    public void setOnMealClickListener(OnMealClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_card, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MealsItem meal = meals.get(position);
        holder.bind(meal);
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMealImage;
        TextView tvMealName;
        TextView tvCategoryBadge;
        CardView cardView;

        MealViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMealImage = itemView.findViewById(R.id.meal_image);
            tvMealName = itemView.findViewById(R.id.tv_meal_name);
            tvCategoryBadge = itemView.findViewById(R.id.category_badge);
            cardView = (CardView) itemView;
        }

        public void bind(MealsItem meal) {
            tvMealName.setText(meal.getStrMeal());

            if (tvCategoryBadge != null && meal.getStrCategory() != null) {
                tvCategoryBadge.setText(meal.getStrCategory());
            }

            Glide.with(itemView.getContext())
                    .load(meal.getStrMealThumb())
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(ivMealImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMealClick(meal);
                }
            });
        }
    }
}