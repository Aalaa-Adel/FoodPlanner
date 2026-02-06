package com.aalaa.foodplanner.data.model;

import java.util.ArrayList;


public class CategoryResponse {
    ArrayList<Category> categories = new ArrayList<Category>();

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }
}