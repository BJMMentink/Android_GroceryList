package com.example.grocerylist;

import android.graphics.Bitmap;

public class GroceryItem {

    private int id;
    private String description;
    private boolean isOnShoppingList;
    private boolean isInCart;
    private int imgId;
    private String owner;
    private Bitmap photo;

    private double latitude;
    private double longitude;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOnShoppingList() {
        return isOnShoppingList;
    }

    public void setOnShoppingList(boolean onShoppingList) {
        isOnShoppingList = onShoppingList;
    }

    public boolean isInCart() {
        return isInCart;
    }

    public void setInCart(boolean inCart) {
        isInCart = inCart;
    }
    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public GroceryItem() {
        this.id = -1;
        this.description = "";
        this.isOnShoppingList = true;
        this.isInCart = false;
    }

    public GroceryItem(int id, String description, boolean isOnShoppingList, boolean isInCart) {
        this.id = id;
        this.description = description;
        this.isOnShoppingList = isOnShoppingList;
        this.isInCart = isInCart;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
