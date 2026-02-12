package com.aalaa.foodplanner.domain.models;

public class Category {
    private String strCategory;
    private String strCategoryDescription;
    private String idCategory;

    private String strCategoryThumb;

    public Category(String strCategory, String strCategoryDescription, String idCategory, String strCategoryThumb) {
        this.idCategory = idCategory;
        this.strCategory = strCategory;
        this.strCategoryDescription = strCategoryDescription;
        this.strCategoryThumb = strCategoryThumb;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public void setStrCategoryThumb(String strCategoryThumb) {
        this.strCategoryThumb = strCategoryThumb;
    }

    public String getStrCategoryDescription() {
        return strCategoryDescription;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public String getStrCategoryThumb() {
        return strCategoryThumb;
    }

    public void setStrCategory(String strCategory) {
        this.strCategory = strCategory;
    }

    public void setStrCategoryDescription(String strCategoryDescription) {
        this.strCategoryDescription = strCategoryDescription;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }
}
