package com.aalaa.foodplanner.ui.plans.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.data.db.PlanEntity;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    private Context context;
    private List<PlanEntity> plans;
    private OnPlanClickListener listener;

    public PlanAdapter(Context context, OnPlanClickListener listener) {
        this.context = context;
        this.plans = new ArrayList<>();
        this.listener = listener;
    }

    public void setPlans(List<PlanEntity> plans) {
        this.plans = plans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plan_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlanEntity plan = plans.get(position);

        holder.tvMealName.setText(plan.getMeal().getStrMeal());

        String planInfo = plan.getDate() + " - " + plan.getSlot();
        holder.tvPlanInfo.setText(planInfo);

        String categoryArea = "";
        if (plan.getMeal().getStrCategory() != null)
            categoryArea += plan.getMeal().getStrCategory();
        if (plan.getMeal().getStrArea() != null) {
            if (!categoryArea.isEmpty())
                categoryArea += " | ";
            categoryArea += plan.getMeal().getStrArea();
        }
        holder.tvCategoryArea.setText(categoryArea);

        Glide.with(context)
                .load(plan.getMeal().getStrMealThumb())
                .placeholder(R.drawable.avatar_placeholder)
                .error(R.drawable.avatar_placeholder)
                .into(holder.ivMealThumb);

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveClick(plan);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMealClick(plan);
            }
        });
    }

    @Override
    public int getItemCount() {
        return plans != null ? plans.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivMealThumb;
        TextView tvMealName;
        TextView tvPlanInfo;
        TextView tvCategoryArea;
        ImageView btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMealThumb = itemView.findViewById(R.id.iv_meal_thumb);
            tvMealName = itemView.findViewById(R.id.tv_meal_name);
            tvPlanInfo = itemView.findViewById(R.id.tv_plan_info);
            tvCategoryArea = itemView.findViewById(R.id.tv_category_area);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}
