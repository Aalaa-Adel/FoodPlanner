package com.aalaa.foodplanner.ui.common;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.aalaa.foodplanner.R;
import com.google.android.material.snackbar.Snackbar;

public class AppSnack {

    public static void showSuccess(View view, String msg) {
        show(view, msg, Type.SUCCESS);
    }

    public static void showError(View view, String msg) {
        show(view, msg, Type.ERROR);
    }

    public static void showInfo(View view, String msg) {
        show(view, msg, Type.INFO);
    }

    private enum Type { SUCCESS, ERROR, INFO }

    private static void show(View view, String message, Type type) {
        if (view == null) return;

        Snackbar sb = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View sbView = sb.getView();

        boolean isDark = isDarkMode(view);

        int bg = ContextCompat.getColor(view.getContext(), isDark ? R.color.card_dark : R.color.card_light);
        int text = ContextCompat.getColor(view.getContext(), isDark ? R.color.text_primary_dark : R.color.text_primary_light);
        int accent = ContextCompat.getColor(view.getContext(), R.color.accent_color);

        int strokeColor;
        int iconRes;

        switch (type) {
            case SUCCESS:
                strokeColor = ContextCompat.getColor(view.getContext(), R.color.colorSuccess);
                iconRes = android.R.drawable.checkbox_on_background;
                break;
            case ERROR:
                strokeColor = ContextCompat.getColor(view.getContext(), R.color.colorError);
                iconRes = android.R.drawable.ic_delete;
                break;
            default:
                strokeColor = accent;
                iconRes = android.R.drawable.ic_dialog_info;
                break;
        }

        GradientDrawable shape = new GradientDrawable();
        shape.setColor(bg);
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
