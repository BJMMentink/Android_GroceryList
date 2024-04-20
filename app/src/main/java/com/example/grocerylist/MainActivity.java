package com.example.grocerylist;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST = 1888;
    public String owner;
    public static final String TAG = "MainActivity";
    private ArrayList<GroceryItem> groceryItems;
    GroceryListAdapter groceryListAdapter;
    RecyclerView groceryList;
    public static boolean isShoppingList = false;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.groceryprefs), Context.MODE_PRIVATE);
            String owner = sharedPreferences.getString("owner", null);
            Log.d(TAG, "onCheckedChanged: " + isChecked);
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) buttonView.getTag();
            int position = viewHolder.getAdapterPosition();
            GroceryItem groceryItem = groceryItems.get(position);
            if (groceryItem != null) {
                if (isShoppingList) {
                    groceryItems.get(position).setInCart(isChecked);
                } else {
                    groceryItems.get(position).setOnShoppingList(isChecked);
                }
                int id = groceryItem.getId();
                RestClient.execPostRequest(groceryItem, getString(R.string.api_grocerylist) + id, MainActivity.this, new VolleyCallback() {
                    @Override
                    public void onSuccess(ArrayList<GroceryItem> results) {
                        groceryItems = results;
                        readFromAPI();
                    }
                });
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Master List");
        setContentView(R.layout.activity_main);
        RecyclerView rvList = findViewById(R.id.rvContainer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);

        groceryItems = new ArrayList<>();

        readFromAPI();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: Here");
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // Handle the photo as needed
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_MasterList && isShoppingList) {
            isShoppingList = false;
            readFromAPI();
            setTitle("Master List");
            return true;
        } else if (itemId == R.id.action_ShoppingList && !isShoppingList) {
            isShoppingList = true;
            readFromAPI();
            setTitle("Shopping List");
            return true;
        } else if (itemId == R.id.action_Clear) {
            clearAllCheckboxes();
            RebindList();
            return true;
        } else if (itemId == R.id.action_Change) {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.groceryprefs), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("owner", null);
            editor.apply();
            readFromAPI();
        } else if (itemId == R.id.action_Delete) {
            GroceryListDataSource ds = new GroceryListDataSource(this);
            if (!isShoppingList) {
                for (GroceryItem groceryItem : groceryItems) {
                    if (groceryItem.isOnShoppingList()) ds.delete(groceryItem);
                }
            } else {
                deleteShopping();
            }
            RebindList();
            return true;
        } else {
            Intent intent = new Intent(MainActivity.this, GroceryEditActivity.class);
            intent.putExtra("itemId", -1);
            Log.d(TAG, "onClick: ");
            startActivity(intent);
            return super.onOptionsItemSelected(item);
        }
       return false;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteShopping() {
        for (GroceryItem groceryItem : groceryItems) {
            if (groceryItem.isInCart()) {
                GroceryListDataSource ds = new GroceryListDataSource(this);
                groceryItem.setOnShoppingList(false);
                groceryItem.setInCart(false);
                ds.update(groceryItem);
                groceryItems = ds.getItemsOnShoppingList(isShoppingList);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAllCheckboxes() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.groceryprefs), Context.MODE_PRIVATE);
        owner = sharedPreferences.getString("owner", null);
        for (GroceryItem groceryItem : groceryItems) {
            if (isShoppingList) {
                groceryItem.setInCart(false);
            } else {
                groceryItem.setOnShoppingList(false);
                groceryItem.setInCart(false);
            }
            RestClient.execPostRequest(groceryItem, getString(R.string.api_grocerylist) + owner, this, new VolleyCallback() {
                @Override
                public void onSuccess(ArrayList<GroceryItem> results) {
                    groceryItems = results;
                    RebindList();
                }
            });
        }
        readFromAPI();
    }

    private void RebindList() {
        Log.d(TAG, "RebindTeams: Start");
        groceryList = findViewById(R.id.rvContainer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        groceryList.setLayoutManager(layoutManager);
        groceryListAdapter = new GroceryListAdapter(groceryItems, false);
        groceryListAdapter.setOnItemCheckedChangeListener(onCheckedChangeListener);
        groceryList.setAdapter(groceryListAdapter);
    }

    private void readFromAPI() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.groceryprefs), Context.MODE_PRIVATE);
        owner = sharedPreferences.getString("owner", null);

        if (owner != null) {
            try {
                Log.d(TAG, "readFromAPI: Start API");
                RestClient.execGetRequest(getString(R.string.api_grocerylist) + owner, owner, isShoppingList, this, new VolleyCallback() {
                    @Override
                    public void onSuccess(ArrayList<GroceryItem> results) {
                        Log.d(TAG, "onSuccess: Got here");
                        groceryItems = results;
                        RebindList();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "readFromAPI: Error " + e.getMessage());
            }
        } else {
            AddOwnerDialog();
            Log.d(TAG, "readFromAPI: " + owner);
        }
    }

    private void AddOwnerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Owner");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newOwner = input.getText().toString().trim();
                if (!newOwner.isEmpty()) {
                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.groceryprefs), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("owner", newOwner);
                    editor.apply();
                    readFromAPI();
                } else {
                    Toast.makeText(MainActivity.this, "Please input your Name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }
}
