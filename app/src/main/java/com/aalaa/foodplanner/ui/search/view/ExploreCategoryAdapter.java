package com.aalaa.foodplanner.ui.search.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aalaa.foodplanner.R;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ExploreCategoryAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<ExploreCategoryAdapter.ViewHolder> {

    private final Context context;
    private List<ExploreItem> items;
    private final OnSearchClickListener clickListener;

    public static class ExploreItem {
        private final String name;
        private final String imageUrl;

        public ExploreItem(String name, String imageUrl) {
            this.name = name;
            this.imageUrl = imageUrl;
        }

        public String getName() { return name; }
        public String getImageUrl() { return imageUrl; }
    }

    public ExploreCategoryAdapter(Context context, List<ExploreItem> items, OnSearchClickListener listener) {
        this.context = context;
        this.items = items != null ? items : new ArrayList<>();
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExploreItem item = items.get(position);
        holder.tvName.setText(item.getName());

        String url = item.getImageUrl();
        boolean hasUrl = (url != null && !url.trim().isEmpty());
        boolean isFlag = hasUrl && url.contains("/flags/");

        if (isFlag) {
            holder.cardFlagIcon.setVisibility(View.VISIBLE);
            holder.cardCircleIcon.setVisibility(View.GONE);

            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.avatar_placeholder)
                    .error(R.drawable.avatar_placeholder)
                    .into(holder.ivFlagIcon);

        } else {
            holder.cardCircleIcon.setVisibility(View.VISIBLE);
            holder.cardFlagIcon.setVisibility(View.GONE);

            if (hasUrl) {
                Glide.with(context)
                        .load(url)
                        .placeholder(R.drawable.avatar_placeholder)
                        .error(R.drawable.avatar_placeholder)
                        .centerCrop()
                        .into(holder.ivCircleIcon);
            } else {
                holder.ivCircleIcon.setImageResource(R.drawable.avatar_placeholder);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onSearchItemClick(item.getName());
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void updateData(List<ExploreItem> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {

        MaterialCardView cardCircleIcon, cardFlagIcon;
        ImageView ivCircleIcon, ivFlagIcon;
        TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardCircleIcon = itemView.findViewById(R.id.card_circle_icon);
            cardFlagIcon = itemView.findViewById(R.id.card_flag_icon);

            ivCircleIcon = itemView.findViewById(R.id.iv_circle_icon);
            ivFlagIcon = itemView.findViewById(R.id.iv_flag_icon);

            tvName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}
