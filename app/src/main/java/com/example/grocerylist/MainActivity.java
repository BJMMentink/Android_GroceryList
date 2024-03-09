package com.example.grocerylist;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
    private RecyclerView recyclerView;
    private ArrayList<GroceryItem> groceryItems;
    private GroceryListAdapter adapter;
    private boolean isShoppingList = false;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            GroceryItem groceryItem = groceryItems.get(position);
            String description = groceryItem.getDescription();
            boolean isOnShoppingList = groceryItem.isOnShoppingList();
            boolean isInCart = groceryItem.isInCart();
            Log.d(TAG, "onClick: Description: " + description +
                    ", isOnShoppingList: " + isOnShoppingList +
                    ", isInCart: " + isInCart);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groceryItems = readFromFile(this, "grocery_list.txt");

        RecyclerView rvList = findViewById(R.id.rvContainer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);

        adapter = new GroceryListAdapter(groceryItems, this);
        rvList.setAdapter(adapter);
        adapter.setOnItemClickListener(onClickListener);

        Log.d(TAG, "onCreate: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_MasterList && isShoppingList) {
            // Load and display the Master List with checkboxes
            groceryItems = FileManager.readGroceryItemsFromFile(this);
            adapter.updateList(groceryItems);
            setTitle("Master List");
            isShoppingList = false;
            return true;
        } else if (itemId == R.id.action_ShoppingList && !isShoppingList) {
            // Load and display the Shopping List without checkboxes checked
            groceryItems = FileManager.readGroceryItemsFromFile(this);
            adapter.updateList(groceryItems);
            setTitle("Shopping List");
            isShoppingList = true;
            return true;
        } else if (itemId == R.id.action_Add) {
            // Show a dialog for adding a new item
            FileManager.showAddItemDialog(this, groceryItems, adapter, isShoppingList);
            return true;
        } else if (itemId == R.id.action_Clear) {
            // Clear all checkboxes
            FileManager.clearAllCheckboxes(groceryItems);
            adapter.notifyDataSetChanged();
            return true;
        } else if (itemId == R.id.action_Delete) {
            // Delete checked items
            FileManager.deleteCheckedItems(this, groceryItems, adapter, isShoppingList);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
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
