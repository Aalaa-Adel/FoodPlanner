package com.aalaa.foodplanner;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {

    private List<String> steps;

    public StepsAdapter(List<String> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String step = steps.get(position);

        holder.tvStepNumber.setText(String.valueOf(position + 1));
        holder.tvStepDescription.setText(step);
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public void updateSteps(List<String> newSteps) {
        this.steps = newSteps;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStepNumber;
        TextView tvStepDescription;

        ViewHolder(View itemView) {
            super(itemView);
            tvStepNumber = itemView.findViewById(R.id.tv_step_number);
            tvStepDescription = itemView.findViewById(R.id.tv_step_description);
        }
    }
}