package com.aalaa.foodplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.data.model.Area;
import com.aalaa.foodplanner.data.model.Category;
import com.aalaa.foodplanner.data.model.MealsItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {

    private List<Area> areas;
    private OnCountryClickListener listener;

    public interface OnCountryClickListener {
        void onCountryClick(Area area);
    }
    public CountryAdapter(){
        this.areas = new ArrayList<>();
    }
    public void setAreas(List<Area> areas) {
        this.areas = areas;
        notifyDataSetChanged();
    }


    public void setOnCountryClickListener(OnCountryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_country, parent, false);
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryAdapter.CountryViewHolder holder, int position) {
        Area area = areas.get(position);
        holder.bind(area);
    }

    @Override
    public int getItemCount() {
        return areas == null ? 0 : areas.size();
    }

    class CountryViewHolder extends RecyclerView.ViewHolder{
        private ImageView countryImage;
        private TextView countryName;
        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);
         //   countryImage = itemView.findViewById(R.id.countryImage);
            countryName = itemView.findViewById(R.id.countryName);
        }

        public void bind(Area area) {
            countryName.setText(area.getStrArea());
//            Glide.with(itemView)
//                    .load(area.getStrAreaThumb())
//                    .into(countryImage);
//
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCountryClick(area);
                }
            });
       }
    }
}

