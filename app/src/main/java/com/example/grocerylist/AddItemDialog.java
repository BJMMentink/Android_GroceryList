package com.example.grocerylist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddItemDialog {

    private static final String TAG = "AddItemDialog";
    public static void showDialog(final Context context, final ArrayList<GroceryItem> groceryItems) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_add_item, null);
        final EditText etDescription = view.findViewById(R.id.etDescription);

        builder.setView(view)
                .setTitle("Add Item")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String description = etDescription.getText().toString().trim();
                        if (!description.isEmpty()) {
                            // Create a new GroceryItem object
                            GroceryItem newItem = new GroceryItem();
                            newItem.setDescription(description);
                            newItem.setOnShoppingList(true);
                            newItem.setInCart(false);
                            groceryItems.add(newItem);
                            writeGroceryItemsToFile(context, groceryItems);
                            Toast.makeText(context, "Item added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private static void writeGroceryItemsToFile(Context context, ArrayList<GroceryItem> groceryItems) {
        ArrayList<String> dataToWrite = new ArrayList<>();
        for (GroceryItem item : groceryItems) {
            String line = item.getDescription() + "|" + (item.isOnShoppingList() ? "1" : "0") + "|" + (item.isInCart() ? "1" : "0");
            dataToWrite.add(line);
        }
        FileManager.writeToFile(context, "grocery_list.txt", dataToWrite);
    }

}

