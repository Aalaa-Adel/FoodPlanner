package com.aalaa.foodplanner.ui.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class FlagUtils {

    @NonNull
    public static String getFlagUrl(@Nullable String areaTitle) {
        if (areaTitle == null) return "";

        String code = getCountryCode(areaTitle);
        if (code.isEmpty()) return "";

        String size = code.equals("tn") ? "64" : "128";

        return "https://www.themealdb.com/images/icons/flags/big/" + size + "/" + code + ".png";
    }

    @NonNull
    private static String getCountryCode(@NonNull String title) {
        switch (title) {
            case "American":
                return "us";
            case "British":
                return "gb";
            case "Algerian":
                return "dz";
            case "Argentinian":
                return "ar";
            case "Australian":
                return "au";
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
            case "Norwegian":
                return "no";
            case "Polish":
                return "pl";
            case "Portuguese":
                return "pt";
            case "Russian":
                return "ru";
            case "Saudi Arabian":
                return "sa";
            case "Slovakian":
                return "sk";
            case "Spanish":
                return "es";
            case "Syrian":
                return "sy";
            case "Thai":
                return "th";
            case "Tunisian":
                return "tn";
            case "Turkish":
                return "tr";
            case "Ukrainian":
                return "ua";
            case "Uruguayan":
                return "uy";
            case "Vietnamese":
                return "vn";
            case "Venezuelan":
                return "ve";
            default:
                return "";
        }
    }
}


