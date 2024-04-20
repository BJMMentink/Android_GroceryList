package com.example.grocerylist;

import com.android.volley.VolleyError;

import java.util.ArrayList;

public interface VolleyCallback {
    void onSuccess(ArrayList<GroceryItem> result);
    default void onError(VolleyError error) {
    }
}
