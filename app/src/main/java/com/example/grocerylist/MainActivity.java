package com.example.grocerylist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private ArrayList<GroceryItem> groceryItems;
    private GroceryListAdapter adapter;
    GroceryListAdapter groceryListAdapter;
    RecyclerView groceryList;
    public static boolean isShoppingList = false;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d(TAG, "onCheckedChanged: " + isChecked);
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) buttonView.getTag();
            int position = viewHolder.getAdapterPosition();
            GroceryItem groceryItem = groceryItems.get(position);
            if (groceryItem != null){
                if (isShoppingList) {
                    groceryItems.get(position).setInCart(isChecked);
                } else {
                    groceryItems.get(position).setOnShoppingList(isChecked);
                }
            }
            GroceryListDataSource ds = new GroceryListDataSource(MainActivity.this);
            ds = new GroceryListDataSource(MainActivity.this);
            ds.update(groceryItems.get(position), isShoppingList);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Master List");
        setContentView(R.layout.activity_main);
        RecyclerView rvList = findViewById(R.id.rvContainer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);


        groceryItems = new ArrayList<GroceryItem>();
        initDatabase();
        //groceryItems.addAll(FileManager.readGroceryItemsFromFile(this)); // Corrected method name
        if(groceryItems.size() == 0) {
            createList();
        }


        // Pass isShoppingList value and onCheckedChangeListener to adapter


        RebindTeams();
        Log.d(TAG, "onCreate: ");
    }

    private void initDatabase() {
        GroceryListDataSource ds = new GroceryListDataSource(this);
        ds.open(false);
        groceryItems = ds.get();
        Log.d(TAG, "initDatabase: Grocery Items: " + groceryItems.size());
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
            GroceryListDataSource dataSource = new GroceryListDataSource(this);
            groceryItems = dataSource.getItemsOnShoppingList(isShoppingList);
            RebindTeams();
            setTitle("Master List");
            return true;
        } else if (itemId == R.id.action_ShoppingList && !isShoppingList) {
            isShoppingList = true;
            GroceryListDataSource dataSource = new GroceryListDataSource(this);
            groceryItems = dataSource.getItemsOnShoppingList(isShoppingList);
            RebindTeams();
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
    private void RebindTeams() {
        // Rebind the RecyclerView
        Log.d(TAG, "RebindTeams: Start");
        groceryList = findViewById(R.id.rvContainer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        groceryList.setLayoutManager(layoutManager);
        groceryListAdapter = new GroceryListAdapter(groceryItems, false);
        groceryListAdapter.setOnItemCheckedChangeListener(onCheckedChangeListener);
        groceryList.setAdapter(groceryListAdapter);

    }
    private void createList() {
        Log.d(TAG, "createTeams: Start");
        groceryItems = new ArrayList<GroceryItem>();


        GroceryListDataSource ds = new GroceryListDataSource(MainActivity.this);
        ds.open(false);
        groceryItems = ds.get();

        Log.d(TAG, "createTeams: End: " + groceryItems.size());
    }

}
