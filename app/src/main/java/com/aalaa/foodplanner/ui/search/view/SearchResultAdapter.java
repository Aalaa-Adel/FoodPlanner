package com.aalaa.foodplanner.ui.search.view;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.data.repository.FavoritesRepositoryImpl;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.bumptech.glide.Glide;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private final Context context;
    private List<MealsItem> meals;
    private final OnSearchMealClickListener clickListener;

    public SearchResultAdapter(Context context, List<MealsItem> meals, OnSearchMealClickListener listener) {
        this.context = context;
        this.meals = meals != null ? meals : new ArrayList<>();
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_meal_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealsItem meal = meals.get(position);

        holder.tvMealName.setText(meal.getStrMeal());

        Glide.with(context)
                .load(meal.getStrMealThumb())
                .placeholder(R.drawable.avatar_placeholder)
                .error(R.drawable.avatar_placeholder)
                .centerCrop()
                .into(holder.ivMealImage);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onMealClick(meal);
            }
        });

        FavoritesRepositoryImpl.getInstance((Application) context.getApplicationContext())
                .isFavorite(meal.getIdMeal())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isFav -> {
                    holder.btnBookmark.setImageResource(isFav ? R.drawable.ic_bookmark : R.drawable.ic_bookmark);
                }, error -> {
                });

        holder.btnBookmark.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onBookmarkClick(meal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void updateData(List<MealsItem> newMeals) {
        this.meals = newMeals != null ? newMeals : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMealImage;
        TextView tvMealName;
        ImageView btnBookmark;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMealImage = itemView.findViewById(R.id.meal_image);
            tvMealName = itemView.findViewById(R.id.tv_meal_name);
            btnBookmark = itemView.findViewById(R.id.btn_bookmark);
        }
    }
}