package com.example.grocerylist;
public class GroceryItem {

    private int id;
    private String description;
    private boolean isOnShoppingList;
    private boolean isInCart;



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
}
