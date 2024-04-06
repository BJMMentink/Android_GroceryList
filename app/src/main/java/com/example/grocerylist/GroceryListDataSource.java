package com.example.grocerylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class GroceryListDataSource {
    SQLiteDatabase database;
    DatabaseHelper dbHelper;
    public static final String TAG = "GroceryListDataSource";
    public GroceryListDataSource(Context context)
    {
        dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASE_NAME,
                null,
                DatabaseHelper.DATABASE_VERSION);
        open();
    }
    public void open() throws SQLException{
        open(false);
    }

    public void open(boolean refresh) throws SQLException{
        database = dbHelper.getWritableDatabase();
        Log.d(TAG, "open: " + database.isOpen());
        if(refresh) refreshData();
    }
    public void close(){
        dbHelper.close();
    }
    public void refreshData() {
        Log.d(TAG, "refreshData: Start");
        try {
            // Delete all existing items in tblGroceryList
            deleteAll();

            // Insert new grocery items
            ArrayList<GroceryItem> groceryItems = new ArrayList<>();
            groceryItems.add(new GroceryItem(1, "Soup", true, true));
            groceryItems.add(new GroceryItem(2, "Onions", true, false));
            groceryItems.add(new GroceryItem(3, "Cheese", false, false));

            for (GroceryItem groceryItem : groceryItems) {
                insert(groceryItem);
            }

            Log.d(TAG, "refreshData: End");
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing data", e);
        }
    }

    public GroceryItem get(int id)
    {
        ArrayList<GroceryItem> groceryItems = new ArrayList<GroceryItem>();
        GroceryItem groceryItem = null;

        try{
            String query = "Select * from tblTeam where id = " + id;
            Cursor cursor = database.rawQuery(query, null);

            //Cursor cursor = database.query("tblTeam",null, null, null, null, null, null);

            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                groceryItem = new GroceryItem();
                groceryItem.setId(cursor.getInt(0));
                groceryItem.setDescription(cursor.getString(1));
                boolean list = cursor.getInt(2) == 1;
                groceryItem.setOnShoppingList(list);
                boolean cart = cursor.getInt(3) == 1;
                groceryItem.setInCart(cart);
                //if (groceryItem.getImgId()==0){
                //   groceryItem.setImgId(R.drawable.photoicon);
                //}
                groceryItems.add(groceryItem);
                Log.d(TAG, "get: " + groceryItem.toString());
                cursor.moveToNext();
            }
        }
        catch(Exception e)
        {
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }
        return groceryItem;
    }
    public ArrayList<GroceryItem> get(){
        ArrayList<GroceryItem> groceryItems = new ArrayList<GroceryItem>();
        try {
            String sql = "Select * from tblGroceryList";
            Cursor cursor = database.rawQuery(sql, null);
            GroceryItem groceryItem;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                groceryItem = new GroceryItem();
                groceryItem.setId(cursor.getInt(0));
                groceryItem.setDescription(cursor.getString(1));
                boolean list = cursor.getInt(2) == 1;
                groceryItem.setOnShoppingList(list);
                boolean cart = cursor.getInt(3) == 1;
                groceryItem.setInCart(cart);
                //if (groceryItem.getImgId()==0){
                //   groceryItem.setImgId(R.drawable.photoicon);
                //}
                groceryItems.add(groceryItem);
                Log.d(TAG, "get: " + groceryItem.toString());
                cursor.moveToNext();
            }
            cursor.close();
        }catch (Exception e){
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }
        return groceryItems;
    }
    public int deleteAll()
    {
        try{
            return database.delete("tblGroceryList", null, null);
        }
        catch(Exception e)
        {
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    public int delete(GroceryItem groceryItem)
    {
        Log.d(TAG, "delete: Start");
        try{
            int id = groceryItem.getId();
            if(id < 1)
                return 0;
            Log.d(TAG, "delete: " + id);
            return delete(id);
        }
        catch(Exception e)
        {
            Log.d(TAG, "Delete: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    public int delete(int id)
    {
        try{
            Log.d(TAG, "delete: Start : " + id);
            Log.d(TAG, "delete: database" + database.isOpen());
            return database.delete("tblTeam", "id = " + id, null);
        }
        catch(Exception e)
        {
            Log.d(TAG, "Delete: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int getNewId(){
        int newId = -1;
        try {
            // get the highest id in the table ++
            String sql = "Select max(id) from tblGroceryList";
            Cursor cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
            newId = cursor.getInt(0) + 1;
            cursor.close();
        }catch (Exception e){
            Log.d(TAG, "getNewId: " + e);
        }
        return newId;
    }
    public int insert(GroceryItem groceryItem)
    {
        Log.d(TAG, "insert: Start");
        int rowsaffected = 0;

        try{
            ContentValues values = new ContentValues();
            values.put("item", groceryItem.getDescription());
            values.put("isOnShoppingList", groceryItem.isOnShoppingList());
            values.put("isInCart", groceryItem.isInCart());

            rowsaffected = (int)database.insert("tblGroceryList", null, values);
        }
        catch(Exception e)
        {
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }
        return rowsaffected;

    }
    public ArrayList<GroceryItem> getItemsOnShoppingList(boolean isShoppingList) {
        ArrayList<GroceryItem> shoppingListItems = new ArrayList<>();
        ArrayList<GroceryItem> groceryItems = new ArrayList<GroceryItem>();
        GroceryItem groceryItem = null;

        try{
            Cursor cursor;
            if(isShoppingList) {String query = "SELECT * FROM tblGroceryList WHERE isOnShoppingList = 1";
                cursor = database.rawQuery(query, null);}
            else { String query = "SELECT * FROM tblGroceryList";
                cursor = database.rawQuery(query, null);}

            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                groceryItem = new GroceryItem();
                groceryItem.setId(cursor.getInt(0));
                groceryItem.setDescription(cursor.getString(1));
                boolean list = cursor.getInt(2) == 1;
                groceryItem.setOnShoppingList(list);
                boolean cart = cursor.getInt(3) == 1;
                groceryItem.setInCart(cart);
                //if (groceryItem.getImgId()==0){
                //   groceryItem.setImgId(R.drawable.photoicon);
                //}
                shoppingListItems.add(groceryItem);
                Log.d(TAG, "get: " + groceryItem.toString());
                cursor.moveToNext();
            }
        }
        catch(Exception e)
        {
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }
            return shoppingListItems;

    }

    public int update(GroceryItem groceryItem, boolean isShoppingList) {
        Log.d(TAG, "update: Start");
        int rowsAffected = 0;

        try {
            // Create a ContentValues object to hold the updated values
            ContentValues values = new ContentValues();
            values.put("id", groceryItem.getId());
            if (groceryItem.isOnShoppingList()){
                values.put("isInCart", groceryItem.isInCart());
            }else {
                groceryItem.setInCart(false);
                values.put("isInCart", groceryItem.isInCart());
            }
            values.put("isOnShoppingList", groceryItem.isOnShoppingList());
            values.put("item", groceryItem.getDescription());

            // Define the WHERE clause to update the row with the given ID
            String whereClause = "id = " + groceryItem.getId();

            // Perform the update operation
            rowsAffected = (int)database.update("tblGroceryList", values, whereClause, null);
            String sqlStatement = "UPDATE tblGroceryList SET " +
                    "description='" + groceryItem.getDescription() + "', " +
                    "isOnShoppingList=" + (groceryItem.isOnShoppingList()) + ", " +
                    "isInCart=" + (groceryItem.isInCart()) +
                    " WHERE id = " + groceryItem.getId();
            Log.d(TAG, "SQL Statement: " + sqlStatement);

            Log.d(TAG, "update: End - Rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.e(TAG, "Error updating grocery item: " + e.getMessage());
            e.printStackTrace();
        }
        return rowsAffected;
    }



}
