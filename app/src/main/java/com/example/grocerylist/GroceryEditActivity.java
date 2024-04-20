package com.example.grocerylist;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class GroceryEditActivity extends AppCompatActivity {
    public static final String TAG = "GroceryEditActivity";
    public static final int PERMISSION_REQUEST_PHONE = 102;
    public static final int CAMERA_REQUEST = 1888;
    GroceryItem groceryItem;
    int itemId = -1;

    ArrayList<GroceryItem> groceryItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_edit);

        Log.d(TAG, "onCreate: Start");

        Bundle extras = getIntent().getExtras();
        itemId = extras.getInt("itemId");

        if(itemId != -1)
        {
            initGroceryItem(itemId);
            this.setTitle("Edit Grocery Item: " + itemId);
        }
        else {
            groceryItem = new GroceryItem();
            this.setTitle("Add Grocery Item");
        }
        initTextChanged(R.id.etDescription);
        initSaveButton();

        initImageButton();
        Log.d(TAG, "onCreate: End");

    }
    protected  void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST)
        {
            if(resultCode == RESULT_OK)
            {
                Log.d(TAG, "onActivityResult: Here");
                Bitmap photo= (Bitmap)data.getExtras().get("data");
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, 144, 144, true);
                ImageButton imageButton = findViewById(R.id.imgButton);
                imageButton.setImageBitmap(scaledPhoto);
                groceryItem.setPhoto(scaledPhoto);
            }
        }
    }

    private void initImageButton() {
        ImageButton imageGroceryItem = findViewById(R.id.imgButton);

        imageGroceryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= 23)
                {
                    // Check for the manifest permission
                    if(ContextCompat.checkSelfPermission(GroceryEditActivity.this, Manifest.permission.CAMERA) != PERMISSION_GRANTED){
                        if(ActivityCompat.shouldShowRequestPermissionRationale(GroceryEditActivity.this, Manifest.permission.CAMERA)){
                            Snackbar.make(findViewById(R.id.activity_grocery_edit), "Grocery List requires this permission to take a photo.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d(TAG, "onClick: snackBar");
                                    ActivityCompat.requestPermissions(GroceryEditActivity.this,
                                            new String[] {Manifest.permission.CAMERA},PERMISSION_REQUEST_PHONE);
                                }
                            }).show();
                        }
                        else {
                            Log.d(TAG, "onClick: ");
                            ActivityCompat.requestPermissions(GroceryEditActivity.this,
                                    new String[] {Manifest.permission.CAMERA},PERMISSION_REQUEST_PHONE);
                            takePhoto();
                        }
                    }
                    else{
                        Log.d(TAG, "onClick: ");
                        takePhoto();
                    }
                }
                else {
                    // Only rely on the previous permissions
                    takePhoto();
                }
            }
        });

    }

    private void takePhoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void readFromAPI(int listId)
    {
        try{
            Log.d(TAG, "readFromAPI: Start");
            RestClient.execGetOneRequest(getString(R.string.api_grocerylist) + listId,
                    this,
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(ArrayList<GroceryItem> results) {
                            Log.d(TAG, "onSuccess: Got Here!");
                            groceryItem = results.get(0);
                            rebindGroceryItem();
                            finish();
                        }
                     });
        }
        catch(Exception e){
            Log.e(TAG, "readFromAPI: Error: " + e.getMessage());
        }
    }
    private void initSaveButton() {
        Button btnSave = findViewById(R.id.btnSave);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.groceryprefs), Context.MODE_PRIVATE);
        String owner = sharedPreferences.getString("owner", null);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemId == -1)
                {
                    Log.d(TAG, "onClick: " );
                    RestClient.execPostRequest(groceryItem, getString(R.string.api_grocerylist),
                            GroceryEditActivity.this,
                            new VolleyCallback() {
                                @Override
                                public void onSuccess(ArrayList<GroceryItem> result) {
                                    groceryItem.setId(result.get(0).getId());
                                    Log.d(TAG, "onSuccess: Post" + groceryItem.getId());
                                    finish();
                                }
                            });
                }
                else {
                    String description = findViewById(R.id.etDescription).toString();
                    groceryItem.setDescription(description);
                    RestClient.execPutRequest(groceryItem, getString(R.string.api_grocerylist) + owner + "/" + itemId,
                            GroceryEditActivity.this,
                            new VolleyCallback() {
                                @Override
                                public void onSuccess(ArrayList<GroceryItem> result) {
                                    Log.d(TAG, "onSuccess: Put" + groceryItem.getId());
                                    finish();
                                }
                            });
                }
            }

        });

    }

    private void initGroceryItem(int itemId) {
        readFromAPI(itemId);
    }

    private void rebindGroceryItem() {
        EditText editDescription = findViewById(R.id.etDescription);
        ImageButton imageButtonPhoto = findViewById(R.id.imgButton);

        editDescription.setText(groceryItem.getDescription());

        if(groceryItem.getPhoto() == null)
        {
            Log.d(TAG, "rebindGroceryItem: Null photo");
            groceryItem.setPhoto(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher_background));
        }
        imageButtonPhoto.setImageBitmap(groceryItem.getPhoto());
    }
    private void initTextChanged(int controlId)
    {
        EditText editText = findViewById(controlId);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                groceryItem.setDescription(s.toString());
            }
        });
    }

}
