package com.example.grocerylist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private ArrayList<GroceryItem> groceryItems;
    private GroceryListAdapter adapter;
    public static boolean isShoppingList = false;
    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @SuppressLint("NotifyDataSetChanged")
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = (int) buttonView.getTag();
            if (position < 0 || position >= groceryItems.size()) {
                Log.e(TAG, "Invalid position: " + position);
                return;
            }
            GroceryItem groceryItem = groceryItems.get(position);
            if (isShoppingList) {
                groceryItem.setInCart(isChecked);
            } else {
                groceryItem.setOnShoppingList(isChecked);
            }
            FileManager.writeGroceryItemsToFile(MainActivity.this, "grocery_list.txt", groceryItems, isShoppingList);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        groceryItems = new ArrayList<>();

        groceryItems.addAll(FileManager.readGroceryItemsFromFile(this)); // Corrected method name

        RecyclerView rvList = findViewById(R.id.rvContainer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);

        // Pass isShoppingList value and onCheckedChangeListener to adapter
        adapter = new GroceryListAdapter(groceryItems, isShoppingList, onCheckedChangeListener);
        rvList.setAdapter(adapter);

        setTitle("Master List");
        Log.d(TAG, "onCreate: ");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        RecyclerView recyclerView;
        if (itemId == R.id.action_MasterList && isShoppingList) {
            isShoppingList = false;
            groceryItems.clear();
            groceryItems.addAll(FileManager.readGroceryItemsFromFile(this));
            adapter.notifyDataSetChanged();
            setTitle("Master List");
            return true;
        } else if (itemId == R.id.action_ShoppingList && !isShoppingList) {
            isShoppingList = true;
            ArrayList<GroceryItem> shoppingListItems = new ArrayList<>();
            for (GroceryItem groceryItem : groceryItems) {
                if (groceryItem.isOnShoppingList()){
                    shoppingListItems.add(groceryItem);
                }
            }
            groceryItems.clear();
            groceryItems.addAll(shoppingListItems);
            adapter.notifyDataSetChanged();
            setTitle("Shopping List");
            return true;
        } else if (itemId == R.id.action_Add) {
            FileManager.showAddItemDialog(this, groceryItems, adapter, isShoppingList);
            return true;
        } else if (itemId == R.id.action_Clear) {
            clearAllCheckboxes();
            return true;
        } else if (itemId == R.id.action_Delete) {
            recyclerView = findViewById(R.id.rvContainer);
            FileManager.deleteCheckedItems(this, groceryItems, adapter, isShoppingList, recyclerView);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAllCheckboxes() {
        for (GroceryItem groceryItem : groceryItems) {
            if (isShoppingList) {
                groceryItem.setInCart(false);
            } else {
                groceryItem.setOnShoppingList(false);
                groceryItem.setInCart(false);
            }
        }
        adapter.notifyDataSetChanged();
        FileManager.writeGroceryItemsToFile(MainActivity.this, "grocery_list.txt", groceryItems, isShoppingList);
    }

}
