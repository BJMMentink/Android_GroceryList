package com.example.grocerylist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class RestClient {
    public static final String TAG = "RestClient";
    public static String OWNER;

    public static void execGetRequest(String url,
                                      String owner,
                                      boolean isShoppingList,
                                      Context context,
                                      VolleyCallback volleyCallback)
    {
        Log.d(TAG, "execGetRequest: Start");
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ArrayList<GroceryItem> items = new ArrayList<GroceryItem>();
        Log.d(TAG, "execGetRequest: " + url);
        OWNER = owner;
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "onResponse: " + response);

                            try {
                                JSONArray Aitems = new JSONArray(response);
                                for(int i = 0; i < Aitems.length(); i++)
                                {
                                    JSONObject object = Aitems.getJSONObject(i);
                                    GroceryItem item = new GroceryItem();
                                    item.setId(object.getInt("id"));
                                    item.setDescription(object.getString("item"));
                                    int isOnShoppingList = object.getInt("isOnShoppingList");
                                    int isInCart = object.getInt("isInCart");
                                    boolean onShoppingList = isOnShoppingList == 1;
                                    boolean inCart = isInCart == 1;
                                    item.setOnShoppingList(onShoppingList);
                                    item.setInCart(inCart);
                                    item.setOwner(owner);
                                    // had to look this up
                                    String photoBase64 = object.getString("photo");
                                    byte[] decodedBytes = Base64.decode(photoBase64, Base64.DEFAULT);
                                    Bitmap photoBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                    item.setPhoto(photoBitmap);

                                    item.setLatitude(object.getDouble("latitude"));
                                    item.setLongitude(object.getDouble("longitude"));
                                    if (isShoppingList){
                                        if (item.isOnShoppingList()) items.add(item);
                                    }else{
                                        items.add(item);
                                    }



                                }
                            } catch (Exception e) {
                                Log.d(TAG, "onResponse: " + e.getMessage());
                                throw new RuntimeException(e);
                            }
                            volleyCallback.onSuccess(items);
                            Log.d(TAG, "onResponse: Items " + items.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        }
                    });

            // Important!!!
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static void executeRequest(GroceryItem item,
                                       String url,
                                       Context context,
                                       VolleyCallback volleyCallback,
                                       int method) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", item.getId()); // Make sure id is included if needed
            jsonObject.put("item", item.getDescription());
            jsonObject.put("isOnShoppingList", item.isOnShoppingList());
            jsonObject.put("isInCart", item.isInCart());
            jsonObject.put("owner", OWNER);
            jsonObject.put("latitude", item.getLatitude());
            jsonObject.put("longitude", item.getLongitude());

            if (item.getPhoto() != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = Bitmap.createScaledBitmap(item.getPhoto(), 144, 144, false);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String jsonPhoto = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
                jsonObject.put("photo", jsonPhoto);
            } else {
                jsonObject.put("photo", JSONObject.NULL); // Use JSONObject.NULL for null value
            }
            final String requestBody = jsonObject.toString();
            Log.d(TAG, "executeRequest: " + requestBody);

            JsonObjectRequest request = new JsonObjectRequest(method, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse: " + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.getMessage());
                }
            })
            {
                @Override
                public byte[] getBody(){
                    Log.i(TAG, "getBody: " + jsonObject.toString());
                    return jsonObject.toString().getBytes(StandardCharsets.UTF_8);
                }
            };

            requestQueue.add(request);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void execDeleteRequest(GroceryItem item,
                                         String url,
                                         Context context,
                                         VolleyCallback volleyCallback)
    {
        try {
            executeRequest(item, url, context, volleyCallback, Request.Method.DELETE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void execPutRequest(GroceryItem item,
                                      String url,
                                      Context context,
                                      VolleyCallback volleyCallback)
    {
        try {
            executeRequest(item, url, context, volleyCallback, Request.Method.PUT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void execPostRequest(GroceryItem item,
                                       String url,
                                       Context context,
                                       VolleyCallback volleyCallback)
    {
        try {
            if (item.getId() == -1) {
                executeRequest(item, url, context, volleyCallback, Request.Method.POST);
            } else {
                Log.d(TAG, "execPostRequest: Item already has an id, cannot execute POST request.");
            }
        } catch (Exception e) {
            Log.d(TAG, "execPostRequest: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void execGetOneRequest(String url,
                                         Context context,
                                         VolleyCallback volleyCallback)
    {
        Log.d(TAG, "execGetOneRequest: Start");
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ArrayList<GroceryItem> items = new ArrayList<GroceryItem>();
        Log.d(TAG, "execGetOneRequest: " + url);

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "onResponse: " + response);

                            try {
                                JSONObject object = new JSONObject(response);
                                GroceryItem item = new GroceryItem();
                                item.setId(object.getInt("id"));
                                item.setDescription(object.getString("item"));
                                int isOnShoppingList = object.getInt("isOnShoppingList");
                                int isInCart = object.getInt("isInCart");
                                boolean onShoppingList = isOnShoppingList == 1;
                                boolean inCart = isInCart == 1;
                                item.setOnShoppingList(onShoppingList);
                                item.setInCart(inCart);
                                item.setOwner(OWNER);

                                item.setLatitude(object.getDouble("latitude"));
                                item.setLongitude(object.getDouble("longitude"));

                                String jsonPhoto = object.getString("photo");

                                if(jsonPhoto != null)
                                {
                                    byte[] bytePhoto = null;
                                    bytePhoto = Base64.decode(jsonPhoto, Base64.DEFAULT);
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);
                                    item.setPhoto(bmp);
                                }

                                items.add(item);

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            volleyCallback.onSuccess(items);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        }
                    });

            // Important!!!
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            Log.d(TAG, "execGetOneRequest: Error" + e.getMessage());
        }
    }
}
