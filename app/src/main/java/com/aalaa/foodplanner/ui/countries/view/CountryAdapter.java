package com.aalaa.foodplanner.ui.countries.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.domain.models.Area;
import com.aalaa.foodplanner.ui.search.FlagUtils;
import com.aalaa.foodplanner.ui.search.view.ExploreCategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

    private final Context context;
    private final List<Area> countries;
    private final OnCountryClickListener listener;

    public CountryAdapter(Context context, List<Area> countries, OnCountryClickListener listener) {
        this.context = context;
        this.countries = countries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Area country = countries.get(position);
        String demonym = country.getStrArea();
        String code = getCountryCode(demonym);
        String name = getCountryName(demonym);

        holder.tvCountryCode.setText(code.toUpperCase());
        holder.tvCountryName.setText(name);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCountryClick(country);
            }
        });
    }

    @Override
    public int getItemCount() {
        return countries != null ? countries.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCountryName;
        TextView tvCountryCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCountryName = itemView.findViewById(R.id.tv_country_name);
            tvCountryCode = itemView.findViewById(R.id.tv_country_code);
        }
    }

    private String getCountryCode(String demonym) {
        if (demonym == null)
            return "UNK";
        switch (demonym) {
            case "American":
                return "us";
            case "British":
                return "gb";
            case "Canadian":
                return "ca";
            case "Chinese":
                return "cn";
            case "Croatian":
                return "hr";
            case "Dutch":
                return "nl";
            case "Egyptian":
                return "eg";
            case "Filipino":
                return "ph";
            case "French":
                return "fr";
            case "Greek":
                return "gr";
            case "Indian":
                return "in";
            case "Irish":
                return "ie";
            case "Italian":
                return "it";
            case "Jamaican":
                return "jm";
            case "Japanese":
                return "jp";
            case "Kenyan":
                return "ke";
            case "Malaysian":
                return "my";
            case "Mexican":
                return "mx";
            case "Moroccan":
                return "ma";
            case "Polish":
                return "pl";
            case "Portuguese":
                return "pt";
            case "Russian":
                return "ru";
            case "Spanish":
                return "es";
            case "Thai":
                return "th";
            case "Tunisian":
                return "tn";
            case "Turkish":
                return "tr";
            case "Vietnamese":
                return "vn";
            default:
                return demonym.substring(0, Math.min(2, demonym.length()));
        }
    }

    private String getCountryName(String demonym) {
        if (demonym == null)
            return "Unknown";
        switch (demonym) {
            case "American":
                return "USA";
            case "British":
                return "UK";
            case "Canadian":
                return "Canada";
            case "Chinese":
                return "China";
            case "Croatian":
                return "Croatia";
            case "Dutch":
                return "Netherlands";
            case "Egyptian":
                return "Egypt";
            case "Filipino":
                return "Philippines";
            case "French":
                return "France";
            case "Greek":
                return "Greece";
            case "Indian":
                return "India";
            case "Irish":
                return "Ireland";
            case "Italian":
                return "Italy";
            case "Jamaican":
                return "Jamaica";
            case "Japanese":
                return "Japan";
            case "Kenyan":
                return "Kenya";
            case "Malaysian":
                return "Malaysia";
            case "Mexican":
                return "Mexico";
            case "Moroccan":
                return "Morocco";
            case "Polish":
                return "Poland";
            case "Portuguese":
                return "Portugal";
            case "Russian":
                return "Russia";
            case "Spanish":
                return "Spain";
            case "Thai":
                return "Thailand";
            case "Tunisian":
                return "Tunisia";
            case "Turkish":
                return "Turkey";
            case "Vietnamese":
                return "Vietnam";
            default:
                return demonym;
        }
    }
}
