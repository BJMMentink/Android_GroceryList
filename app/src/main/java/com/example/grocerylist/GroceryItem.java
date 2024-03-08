package com.example.grocerylist;

public class GroceryItem {
    private String description;
    private boolean isOnShoppingList;
    private boolean isInCart;
    private boolean isMainScreen;

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
    public boolean isMainScreen() {
        return isMainScreen;
    }

    public void setMainScreen(boolean mainScreen) {
        isMainScreen = mainScreen;
    }
}
