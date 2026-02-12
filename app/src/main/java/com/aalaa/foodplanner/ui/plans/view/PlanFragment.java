package com.aalaa.foodplanner.ui.plans.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.aalaa.foodplanner.ui.common.AppSnack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.data.repository.PlanRepositoryImpl;
import com.aalaa.foodplanner.datasource.db.PlanEntity;
import com.aalaa.foodplanner.domain.models.MealsItem;
import com.aalaa.foodplanner.ui.plans.presenter.PlanPresenterImpl;

import java.util.List;

public class PlanFragment extends Fragment implements PlanView, OnPlanClickListener {

    private PlanPresenterImpl presenter;
    private PlanAdapter adapter;
    private CalendarAdapter calendarAdapter;
    private RecyclerView recyclerView;
    private RecyclerView rvCalendar;
    private LinearLayout emptyState;
    private ProgressBar progressBar;
    
    private List<PlanEntity> allPlans;
    private String selectedDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PlanRepositoryImpl repository = PlanRepositoryImpl.getInstance(requireActivity().getApplication());
        presenter = new PlanPresenterImpl(repository, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plans, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        presenter.loadPlans();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_plans);
        rvCalendar = view.findViewById(R.id.rv_calendar);
        emptyState = view.findViewById(R.id.layout_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        adapter = new PlanAdapter(requireContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        
        setupCalendar();
    }

    private void setupCalendar() {
        List<java.util.Calendar> days = new java.util.ArrayList<>();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        
        // Initial selected date (Today)
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
        selectedDate = sdf.format(calendar.getTime());
        
        // Generate next 30 days
        for (int i = 0; i < 30; i++) {
            java.util.Calendar day = (java.util.Calendar) calendar.clone();
            days.add(day);
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1);
        }
        
        calendarAdapter = new CalendarAdapter(requireContext(), days, date -> {
            selectedDate = sdf.format(date.getTime());
            filterPlans();
        });
        
        rvCalendar.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCalendar.setAdapter(calendarAdapter);
    }
    
    private void filterPlans() {
        if (allPlans == null) return;
        
        List<PlanEntity> filtered = new java.util.ArrayList<>();
        for (PlanEntity plan : allPlans) {
            if (plan.getDate().equals(selectedDate)) {
                filtered.add(plan);
            }
        }
        
        adapter.setPlans(filtered);
        
        if (filtered.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void showPlans(List<PlanEntity> plans) {
        progressBar.setVisibility(View.GONE);
        allPlans = plans;
        filterPlans();
    }

    @Override
    public void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        allPlans = new java.util.ArrayList<>();
        filterPlans();
    }

    @Override
    public void showError(String error) {
        progressBar.setVisibility(View.GONE);
        AppSnack.showError(requireView(), error);
    }

    @Override
    public void showAddedToPlan() {
        AppSnack.showSuccess(requireView(), "Added to plan");
        presenter.loadPlans();
    }

    @Override
    public void showRemovedFromPlan() {
        AppSnack.showInfo(requireView(), "Removed from plan");
        presenter.loadPlans();
    }

    @Override
    public void onRemoveClick(PlanEntity plan) {
        presenter.removeFromPlan(plan);
    }

    @Override
    public void onMealClick(PlanEntity plan) {
        if (getView() != null) {
            try {
                MealsItem meal = plan.getMeal();
                Bundle bundle = new Bundle();
                bundle.putSerializable("meal", meal);
                Navigation.findNavController(getView()).navigate(R.id.recipeDetailFragment, bundle);
            } catch (Exception e) {
                e.printStackTrace();
                AppSnack.showError(requireView(), "Cannot navigate to details");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.dispose();
        }
    }
}
