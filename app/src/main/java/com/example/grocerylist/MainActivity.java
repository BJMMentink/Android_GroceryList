package com.example.grocerylist;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import android.Manifest;
import java.util.ArrayList;




public class MainActivity extends AppCompatActivity {
    public static final int PERMISSION_REQUEST_PHONE = 102;
    public static final int PERMISSION_REQUEST_CAMERA = 103;
    public static final int CAMERA_REQUEST = 1888;
    public String owner;
    Bitmap photo;
    GroceryItem groceryItem;
    public ImageButton imageButton;
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
            if (groceryItem != null){
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Master List");
        setContentView(R.layout.activity_main);
        RecyclerView rvList = findViewById(R.id.rvContainer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);


        groceryItems = new ArrayList<GroceryItem>();

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
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: Here");
                photo = (Bitmap) data.getExtras().get("data");
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, 100, 100, true);
                imageButton = dialogView.findViewById(R.id.imgButton);
                imageButton.setImageBitmap(scaledPhoto);
                if (groceryItem == null) {
                    groceryItem = new GroceryItem();
                }
                groceryItem.setPhoto(scaledPhoto);
                dialogView.requestFocus();
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
        } else if (itemId == R.id.action_Add) {
            showAddItemDialog(-1);
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
                    if (groceryItem.isOnShoppingList())ds.delete(groceryItem);
                }
            }else {
                deleteShopping();
            }
            RebindList();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return false;
    }
    @SuppressLint("NotifyDataSetChanged")
    private void deleteShopping() {
        for (GroceryItem groceryItem : groceryItems) {
            if (groceryItem.isInCart()){
            GroceryListDataSource ds = new GroceryListDataSource(this);
            groceryItem.setOnShoppingList(false);
            groceryItem.setInCart(false);
            ds.update(groceryItem);
            groceryItems = ds.getItemsOnShoppingList(isShoppingList);}
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
        // Rebind the RecyclerView
        Log.d(TAG, "RebindTeams: Start");
        groceryList = findViewById(R.id.rvContainer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        groceryList.setLayoutManager(layoutManager);
        groceryListAdapter = new GroceryListAdapter(groceryItems, false);
        groceryListAdapter.setOnItemCheckedChangeListener(onCheckedChangeListener);
        groceryList.setAdapter(groceryListAdapter);
    }
    private void readFromAPI(){
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
            }catch (Exception e){
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
    public void showAddItemDialog(int itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);
        builder.setTitle(itemId == -1 ? "Add Item" : "Edit Item");
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        groceryItem = new GroceryItem();

        if (itemId != -1) {
            RestClient.execGetOneRequest(getString(R.string.api_grocerylist) + itemId, this, new VolleyCallback() {
                @Override
                public void onSuccess(ArrayList<GroceryItem> results) {
                    GroceryItem item = results.get(0);
                    if (item != null) {
                        etDescription.setText(item.getDescription());
                        Bitmap photo = item.getPhoto();
                        ImageButton imageButton = dialogView.findViewById(R.id.imgButton);
                        if (photo != null) {
                            imageButton.setImageBitmap(photo);
                        }
                    }
                }
            });
        }

        builder.setPositiveButton("Save Item", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String description = etDescription.getText().toString().trim();
                try {
                    if (imageButton.getDrawable() != null) {
                        photo = ((BitmapDrawable) imageButton.getDrawable()).getBitmap();
                    }
                    if (!description.isEmpty()) {
                        GroceryItem newItem = new GroceryItem();
                        newItem.setDescription(description);
                        if (photo != null) {
                            newItem.setPhoto(photo);
                        }
                        if (itemId != -1) {
                            RestClient.execPostRequest(newItem, getString(R.string.api_grocerylist), MainActivity.this, new VolleyCallback() {
                                @Override
                                public void onSuccess(ArrayList<GroceryItem> results) {
                                    readFromAPI();
                                }
                            });
                        } else {
                            GroceryItem item = new GroceryItem();
                            RestClient.execPostRequest(item, getString(R.string.api_grocerylist) + owner, MainActivity.this, new VolleyCallback() {
                                @Override
                                public void onSuccess(ArrayList<GroceryItem> result) {
                                    item.setId(result.get(0).getId());
                                    Log.d(TAG, "onSuccess: Post" + item.getId());
                                }
                            });
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onClick: " + e.getMessage());
                }
            }
        });



        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        initImageButton(dialogView);
    }


    private void initImageButton(View dialogView) {
        imageButton = dialogView.findViewById(R.id.imgButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)){
                        Snackbar.make(findViewById(R.id.dialog_add_item), "Teams requires this permission to take a photo.",
                                Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d(TAG, "onClick: snackBar");
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[] {Manifest.permission.CAMERA},PERMISSION_REQUEST_PHONE);
                            }
                        }).show();
                    }
                    else {
                        Log.d(TAG, "onClick: ");
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[] {Manifest.permission.CAMERA},PERMISSION_REQUEST_PHONE);
                        takePhoto();
                    }
                }
                else{
                    Log.d(TAG, "onClick: ");
                    takePhoto();
                }
            }
        });

    }
    private void takePhoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
}
