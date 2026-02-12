package com.aalaa.foodplanner.ui.common;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import com.aalaa.foodplanner.data.local.SharedPreferencesHelper;
import com.aalaa.foodplanner.data.local.SharedPreferencesKeysConfig;

import java.util.Locale;

public class LanguageHelper {

    public static Context onAttach(Context context) {
        String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        return setLocale(context, lang);
    }

    public static String getLanguage(Context context) {
        return getPersistedData(context, Locale.getDefault().getLanguage());
    }

    public static Context setLocale(Context context, String language) {
        persist(context, language);

        return updateResources(context, language);
    }

    private static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferencesHelper helper = new SharedPreferencesHelper(context);
        return helper.getLanguage();
    }

    private static void persist(Context context, String language) {
        SharedPreferencesHelper helper = new SharedPreferencesHelper(context);
        helper.setLanguage(language);
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            return context.createConfigurationContext(config);
        } else {
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        }
    }
}
