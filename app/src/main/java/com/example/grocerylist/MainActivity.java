package com.example.grocerylist;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
public static final String TAG = "MainActivity";
    private ArrayList<GroceryItem> groceryItems;
    private GroceryListAdapter adapter;
    private boolean isShoppingList = false;
    private CheckBox.OnCheckedChangeListener onCheckedChangeListener = new CheckBox.OnCheckedChangeListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = (int) buttonView.getTag();
            GroceryItem groceryItem = groceryItems.get(position);
            groceryItem.setOnShoppingList(isChecked);
            FileManager.writeGroceryItemsToFile(MainActivity.this, "grocery_list.txt", groceryItems, isShoppingList, isChecked);

            adapter.notifyDataSetChanged();
        }
    };


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        groceryItems = new ArrayList<>();

        groceryItems.addAll(readFromFile(this, "grocery_list.txt"));

        RecyclerView rvList = findViewById(R.id.rvContainer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);

        adapter = new GroceryListAdapter(groceryItems, this, onCheckedChangeListener);
        rvList.setAdapter(adapter);
        onCheckedChangeListener = (buttonView, isChecked) -> {
            int position = (int) buttonView.getTag();
            GroceryItem groceryItem = groceryItems.get(position);
            groceryItem.setOnShoppingList(isChecked);
            FileManager.writeGroceryItemsToFile(MainActivity.this, "grocery_list.txt", groceryItems, isShoppingList, isChecked);

            adapter.notifyDataSetChanged();
        };


        adapter = new GroceryListAdapter(groceryItems, this, onCheckedChangeListener);


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
            groceryItems.clear();
            groceryItems.addAll(FileManager.readGroceryItemsFromFile(this));
            adapter.notifyDataSetChanged();
            setTitle("Master List");
            isShoppingList = false;
            return true;
        } else if (itemId == R.id.action_ShoppingList && !isShoppingList) {
            ArrayList<GroceryItem> shoppingListItems = new ArrayList<>();
            for (GroceryItem Gitem : groceryItems) {
                if (Gitem.isOnShoppingList()) {
                    shoppingListItems.add(Gitem);
                }
            }
            clearAllCheckboxes();
            groceryItems.clear();
            groceryItems.addAll(shoppingListItems);
            recyclerView = findViewById(R.id.rvContainer);
            adapter = new GroceryListAdapter(groceryItems, this, onCheckedChangeListener);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            setTitle("Shopping List");
            isShoppingList = true;
            return true;
        } else if (itemId == R.id.action_Add) {
            // Done
            FileManager.showAddItemDialog(this, groceryItems, adapter, isShoppingList);
            return true;
        } else if (itemId == R.id.action_Clear) {
            ArrayList<GroceryItem> shoppingListItems = new ArrayList<>();
            clearAllCheckboxes();
            for (GroceryItem Gitem : groceryItems) {
                if (isShoppingList){
                    if (Gitem.isOnShoppingList()) {
                        shoppingListItems.add(Gitem);
                    }
                }else {
                    shoppingListItems.add(Gitem);
                }

            }
            groceryItems.clear();
            groceryItems.addAll(shoppingListItems);
            adapter.notifyDataSetChanged();
            return true;
        } else if (itemId == R.id.action_Delete) {
            ArrayList<GroceryItem> shoppingListItems = new ArrayList<>();
            recyclerView = findViewById(R.id.rvContainer);
            FileManager.deleteCheckedItems(this, groceryItems, adapter, isShoppingList, recyclerView);
            adapter.notifyDataSetChanged();
            FileManager.writeGroceryItemsToFile(MainActivity.this, "grocery_list.txt", groceryItems, null, false);

            groceryItems.addAll(shoppingListItems);
            recyclerView = findViewById(R.id.rvContainer);
            adapter = new GroceryListAdapter(groceryItems, this, onCheckedChangeListener);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private void clearAllCheckboxes() {
        RecyclerView rvStuff = findViewById(R.id.rvContainer);
        if (rvStuff != null && rvStuff.getLayoutManager() != null) {
            for (int i = 0; i < rvStuff.getLayoutManager().getItemCount(); i++) {
                RecyclerView.ViewHolder viewHolder = rvStuff.findViewHolderForAdapterPosition(i);
                if (viewHolder != null && viewHolder.itemView != null) {
                    CheckBox cbxShoppingCart = viewHolder.itemView.findViewById(R.id.cbxShoppingCart);
                    if (cbxShoppingCart != null) {
                        cbxShoppingCart.setChecked(false);
                        // Update the corresponding GroceryItem object
                        if (i < groceryItems.size()) {
                            GroceryItem groceryItem = groceryItems.get(i);
                            groceryItem.setOnShoppingList(false);
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
    }



    public static ArrayList<GroceryItem> readFromFile(Context context, String filename) {
        ArrayList<GroceryItem> groceryItems = new ArrayList<>();
        try {
            InputStream inputStream = context.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // Determine the number of lines in the file
            int numLines = 0;
            while (bufferedReader.readLine() != null) {
                numLines++;
            }

            // Reset the reader to the beginning of the file
            inputStream = context.openFileInput(filename);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            // Read each line and parse grocery items
            for (int i = 0; i < numLines; i++) {
                String line = bufferedReader.readLine();
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String description = parts[0];
                    boolean isOnShoppingList = parts[1].equals("1");
                    boolean isInCart = parts[2].equals("1");
                    GroceryItem item = new GroceryItem(description, isOnShoppingList, isInCart);
                    groceryItems.add(item);
                }
            }

            inputStream.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + filename, e);
        } catch (Exception e) {
            Log.e(TAG, "Error reading from file: " + filename, e);
        }
        return groceryItems;
    }

}
