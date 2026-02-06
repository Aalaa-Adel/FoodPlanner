package com.aalaa.foodplanner.domain.auth.model.model;
public class Ingredients {
    private String idIngredient;
    private String strIngredient;
    private String strDescription;
    private String strThumb;
    private String strType = null;

    public String getIdIngredient() {
        return idIngredient;
    }

    public String getStrIngredient() {
        return strIngredient;
    }

    public String getStrDescription() {
        return strDescription;
    }

    public String getStrThumb() {
        return strThumb;
    }

    public String getStrType() {
        return strType;
    }

    public void setIdIngredient(String idIngredient) {
        this.idIngredient = idIngredient;
    }

    public void setStrIngredient(String strIngredient) {
        this.strIngredient = strIngredient;
    }

    public void setStrDescription(String strDescription) {
        this.strDescription = strDescription;
    }

    public void setStrThumb(String strThumb) {
        this.strThumb = strThumb;
    }

    public void setStrType(String strType) {
        this.strType = strType;
    }
}