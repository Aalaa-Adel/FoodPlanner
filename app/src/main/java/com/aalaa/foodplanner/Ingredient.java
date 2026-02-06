package com.aalaa.foodplanner;


public class Ingredient {
    private String name;
    private String quantity;
    private String note;
    private String imageUrl;

    public Ingredient(String name, String quantity, String note) {
        this.name = name;
        this.quantity = quantity;
        this.note = note;
    }

    public Ingredient(String name, String quantity, String note, String imageUrl) {
        this.name = name;
        this.quantity = quantity;
        this.note = note;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}