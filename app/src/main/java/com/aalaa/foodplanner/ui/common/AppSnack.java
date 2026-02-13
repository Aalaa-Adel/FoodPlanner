package com.aalaa.foodplanner.ui.common;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.aalaa.foodplanner.R;
import com.google.android.material.snackbar.Snackbar;

public class AppSnack {

    public static void showSuccess(View view, String msg) {
        show(view, msg, SNACK_SUCCESS);
    }

    public static void showError(View view, String msg) {
        show(view, msg, SNACK_ERROR);
    }

    public static void showInfo(View view, String msg) {
        show(view, msg, SNACK_INFO);
    }

    private static final int SNACK_SUCCESS = 0;
    private static final int SNACK_ERROR = 1;
    private static final int SNACK_INFO = 2;

    private static void show(View view, String message, int type) {
        if (view == null)
            return;

        Snackbar sb = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View sbView = sb.getView();

        // Fix: Anchor to bottom navigation so it appears above it
        View nav = view.getRootView().findViewById(R.id.bottomNav);
        if (nav != null) {
            sb.setAnchorView(nav);
        }

        int text = ContextCompat.getColor(view.getContext(), android.R.color.white);
        int strokeColor;
        int iconRes;
        GradientDrawable shape = new GradientDrawable();

        switch (type) {
            case SNACK_SUCCESS:
                strokeColor = ContextCompat.getColor(view.getContext(), R.color.colorSuccess);
                iconRes = android.R.drawable.checkbox_on_background;
                shape.setColor(ContextCompat.getColor(view.getContext(), R.color.colorSuccess));
                break;
            case SNACK_ERROR:
                strokeColor = ContextCompat.getColor(view.getContext(), R.color.colorError);
                iconRes = android.R.drawable.ic_delete;
                shape.setColor(ContextCompat.getColor(view.getContext(), R.color.colorError));
                break;
            default:
                strokeColor = ContextCompat.getColor(view.getContext(), R.color.accent_color);
                iconRes = android.R.drawable.ic_dialog_info;
                shape.setColor(ContextCompat.getColor(view.getContext(), R.color.accent_dark));
                break;
        }

        shape.setCornerRadius(dp(view, 14));
        shape.setStroke((int) dp(view, 1.2f), strokeColor);
        sbView.setBackground(shape);
        sbView.setPadding((int) dp(view, 14), (int) dp(view, 10), (int) dp(view, 14), (int) dp(view, 10));

        TextView tv = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        if (tv != null) {
            tv.setTextColor(text);
            tv.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
            tv.setCompoundDrawablePadding((int) dp(view, 10));
            tv.setMaxLines(3);
        }

        sb.show();
    }

    private static float dp(View v, float dp) {
        return dp * v.getResources().getDisplayMetrics().density;
    }

    private static boolean isDarkMode(View v) {
        int nightModeFlags = v.getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }
}
