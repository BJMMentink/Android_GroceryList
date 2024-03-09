package com.example.grocerylist;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class FileManager {

    private static final String FILENAME = "grocery_list.txt";
    public static final String TAG = "FileManager";
    public static ArrayList<GroceryItem> readGroceryItemsFromFile(Context context) {
        ArrayList<GroceryItem> groceryItems = new ArrayList<>();
        try {
            InputStream inputStream = context.openFileInput(FILENAME);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
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
                Log.d("FileManager", "Data read from file: " + FILENAME);
            }
        } catch (FileNotFoundException e) {
            Log.e("FileManager", "File not found: " + FILENAME, e);
        } catch (IOException e) {
            Log.e("FileManager", "Error reading from file: " + FILENAME, e);
        }
        return groceryItems;
    }

    public static ArrayList<GroceryItem> getShoppingList(ArrayList<GroceryItem> allItems) {
        ArrayList<GroceryItem> shoppingList = new ArrayList<>();
        for (GroceryItem item : allItems) {
            if (item.isOnShoppingList()) {
                shoppingList.add(item);
            }
        }
        return shoppingList;
    }
    public static void showAddItemDialog(final Context context, final ArrayList<GroceryItem> groceryItems, final GroceryListAdapter adapter, final boolean isShoppingList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Item");
        final EditText input = new EditText(context);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newItem = input.getText().toString().trim();
                if (!newItem.isEmpty()) {
                    addGroceryItem(context, groceryItems, adapter, isShoppingList, newItem);
                } else {
                    Toast.makeText(context, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private static void addGroceryItem(Context context, ArrayList<GroceryItem> groceryItems, GroceryListAdapter adapter, boolean isShoppingList, String description) {
        GroceryItem newItem = new GroceryItem(description, isShoppingList, false);
        groceryItems.add(newItem);
        writeGroceryItemsToFile(context, FILENAME, groceryItems, null, false);
        adapter.notifyDataSetChanged();
        Toast.makeText(context, "Item added successfully", Toast.LENGTH_SHORT).show();
    }

    public static void deleteCheckedItems(Context context, ArrayList<GroceryItem> groceryItems, GroceryListAdapter adapter, boolean isShoppingList, RecyclerView recyclerView) {
        ArrayList<GroceryItem> itemsToRemove = new ArrayList<>();
        ArrayList<GroceryItem> itemsToClear = new ArrayList<>();
        int i = 0;
        for (GroceryItem item : groceryItems) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
                CheckBox cbxShoppingCart = viewHolder.itemView.findViewById(R.id.cbxShoppingCart);
                if (cbxShoppingCart.isChecked()) {
                    if (item.isOnShoppingList() && !isShoppingList) {
                        itemsToRemove.add(item);
                    } else if (item.isInCart() && isShoppingList) {
                        itemsToClear.add(item);
                    } else {
                        itemsToRemove = null;
                        itemsToClear = null;
                    }
                }
            }
            i++;
        }
        if (itemsToRemove != null){
            groceryItems.removeAll(itemsToRemove);
        }
        if (itemsToClear != null){
            for (GroceryItem item : itemsToClear) {
                item.setOnShoppingList(false);
            }
        }

        writeGroceryItemsToFile(context, FILENAME, groceryItems, null, false);
        adapter.notifyDataSetChanged();
    }


    public static void writeGroceryItemsToFile(Context context, String filename, ArrayList<GroceryItem> groceryItems, Boolean isShoppingList, Boolean isChecked) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            for (GroceryItem item : groceryItems) {
                String secondParam = "0";
                String thirdParam = "0";
                if (isChecked != null) {
                    if (isShoppingList == null) {
                        secondParam = item.isOnShoppingList() ? "1" : "0";
                        thirdParam = item.isInCart() ? "1" : "0";
                    } else if (isShoppingList) {
                        secondParam = "1";
                        thirdParam = !isChecked ? "0" : (item.isInCart() ? "1" : "0");
                    } else {
                        secondParam = !isChecked ? "0" : (!item.isOnShoppingList() ? "1" : "0");
                        thirdParam = "0";
                    }
                }

                outputStreamWriter.write(item.getDescription() + "|" + secondParam + "|" + thirdParam + "\n");
            }
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<GroceryItem> readFromFile(Context context, String filename) {
        ArrayList<GroceryItem> groceryItems = new ArrayList<>();
        try {
            InputStream inputStream = context.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
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
        } catch (IOException e) {
            Log.e(TAG, "Error reading from file: " + filename, e);
        }
        return groceryItems;
    }

}