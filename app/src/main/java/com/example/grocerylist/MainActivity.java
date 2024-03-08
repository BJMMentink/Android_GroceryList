package com.example.grocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public abstract class MainActivity extends AppCompatActivity implements GroceryListAdapter.OnItemClickListener {

    private ArrayList<GroceryItem> groceryItems;
    private RecyclerView recyclerView;
    private GroceryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rvContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        groceryItems = new ArrayList<>();
        adapter = new GroceryListAdapter(groceryItems);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_MasterList) {
            // Implement logic to show master list
            return true;
        } else if (itemId == R.id.action_ShoppingList) {
            // Implement logic to show shopping list
            return true;
        } else if (itemId == R.id.action_Add) {
            showAddItemDialog();
            return true;
        } else if (itemId == R.id.action_Clear) {
            clearAllCheckboxes();
            return true;
        } else if (itemId == R.id.action_Delete) {
            deleteCheckedItems();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Item");
        builder.setPositiveButton("Add", (dialog, which) -> {
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void clearAllCheckboxes() {
        // Implement logic to clear all checkboxes
    }

    private void deleteCheckedItems() {
        // Implement logic to delete checked items
    }


    public void onItemClick(int position) {
        GroceryItem clickedItem = groceryItems.get(position);

        Toast.makeText(this, "Clicked item: " + clickedItem.getDescription(), Toast.LENGTH_SHORT).show();

        if (clickedItem.isOnShoppingList()) {
            clickedItem.setOnShoppingList(false);
        }else{
            clickedItem.setOnShoppingList(true);
        }
        if (!clickedItem.isInCart()) {
            clickedItem.setInCart(true);
        }else {
            clickedItem.setInCart(false);
        }
    }
    public void onBindViewHolder(@NonNull GroceryListAdapter.GroceryViewHolder holder, int position) {
        GroceryItem groceryItem = groceryItems.get(position);
        holder.descriptionTextView.setText(groceryItem.getDescription());
        holder.shoppingCheckBox.setChecked(groceryItem.isOnShoppingList());
        holder.shoppingCheckBox.setChecked(groceryItem.isInCart());
        if (groceryItem.isOnShoppingList()) {
            holder.shoppingCheckBox.setChecked(true);
        } else {
            holder.shoppingCheckBox.setChecked(false);
        }
        if (groceryItem.isInCart()) {
            holder.shoppingCheckBox.setChecked(true);
        } else {
            holder.shoppingCheckBox.setChecked(false);
        }
    }

}

