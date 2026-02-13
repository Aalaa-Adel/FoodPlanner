package com.aalaa.foodplanner.ui.listing.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.domain.models.MealSpecification;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MealsListingAdapter extends RecyclerView.Adapter<MealsListingAdapter.MealViewHolder> {

    private List<MealSpecification> meals;
    private OnMealClickListener listener;
    private OnFavoriteClickListener favoriteClickListener;

    public MealsListingAdapter() {
        this.meals = new ArrayList<>();
    }

    public void setMeals(List<MealSpecification> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    public void setOnMealClickListener(OnMealClickListener listener) {
        this.listener = listener;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteClickListener = listener;
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
        MealSpecification meal = meals.get(position);
        holder.bind(meal);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    // ...

    class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMealImage;
        TextView tvMealName;
        TextView tvCategoryBadge;
        ImageView btnBookmark;
        View btnAdd;
        CardView cardView;

        MealViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMealImage = itemView.findViewById(R.id.meal_image);
            tvMealName = itemView.findViewById(R.id.tv_meal_name);
            tvCategoryBadge = itemView.findViewById(R.id.category_badge);
            btnBookmark = itemView.findViewById(R.id.btn_bookmark);
            btnAdd = itemView.findViewById(R.id.btn_add);
            cardView = (CardView) itemView;
        }

        public void bind(MealSpecification meal) {
            tvMealName.setText(meal.getStrMeal());

            if (tvCategoryBadge != null) {
                tvCategoryBadge.setVisibility(View.GONE);
            }

            Glide.with(itemView.getContext())
                    .load(meal.getStrMealThumb())
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(ivMealImage);

            if (meal.isFavorite()) {
                btnBookmark.setImageResource(R.drawable.ic_heart_filled);
            } else {
                btnBookmark.setImageResource(R.drawable.ic_heart_outline);
            }

            btnBookmark.setOnClickListener(v -> {
                if (favoriteClickListener != null) {
                    favoriteClickListener.onFavoriteClick(meal);
                }
            });

            btnAdd.setOnClickListener(v -> {
                if (favoriteClickListener != null) {
                    favoriteClickListener.onFavoriteClick(meal);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMealClick(meal);
                }
            });
        }
    }
}
