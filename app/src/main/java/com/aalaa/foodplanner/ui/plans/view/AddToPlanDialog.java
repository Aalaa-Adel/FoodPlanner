package com.aalaa.foodplanner.ui.plans.view;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aalaa.foodplanner.R;
import com.aalaa.foodplanner.domain.models.MealsItem;

import java.util.Calendar;

public class AddToPlanDialog extends Dialog {

    private final MealsItem meal;
    private final OnPlanSelectedListener listener;

    private TextView tvSelectedDate;
    private RadioGroup radioGroupSlot;
    private Button btnCancel;
    private Button btnAdd;
    private Button btnPickDate;

    private String selectedDate = "";

    public interface OnPlanSelectedListener {
        void onPlanSelected(MealsItem meal, String date, String slot);
    }

    public AddToPlanDialog(@NonNull Context context, MealsItem meal, OnPlanSelectedListener listener) {
        super(context);
        this.meal = meal;
        this.listener = listener;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_to_plan_calendar);

        tvSelectedDate = findViewById(R.id.tv_selected_date);
        btnPickDate = findViewById(R.id.btn_pick_date);
        radioGroupSlot = findViewById(R.id.radio_group_slot);
        btnCancel = findViewById(R.id.btn_cancel);
        btnAdd = findViewById(R.id.btn_add);

        ((RadioButton) findViewById(R.id.radio_breakfast)).setChecked(true);

        btnPickDate.setOnClickListener(v -> openDatePicker());

        btnCancel.setOnClickListener(v -> dismiss());

        btnAdd.setOnClickListener(v -> {
            String slot = getSelectedSlot();
            if (selectedDate.isEmpty()) {
                tvSelectedDate.setError("Please select a date");
                return;
            }
            if (listener != null) {
                listener.onPlanSelected(meal, selectedDate, slot);
                dismiss();
            }
        });
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, y, m, d) -> {
                    selectedDate = String.format(java.util.Locale.US, "%04d-%02d-%02d", y, m + 1, d);
                    tvSelectedDate.setText(selectedDate);
                }, year, month, day);

        datePickerDialog
                .getDatePicker()
                .setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private String getSelectedSlot() {
        int selectedSlotId = radioGroupSlot.getCheckedRadioButtonId();
        RadioButton selectedSlotButton = findViewById(selectedSlotId);
        return selectedSlotButton != null ? selectedSlotButton.getText().toString() : "Breakfast";
    }
}
