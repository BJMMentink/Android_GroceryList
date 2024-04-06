package com.example.grocerylist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "groceryList.db";
    public static final String CREATE_GROCERY_LIST_SQL=
            "create table tblGroceryList "
                    + "(id integer primary key autoincrement, "
                    + "item text not null, "
                    + "isOnShoppingList boolian not null, "
                    //+ "imgId boolian not null, "
                    + "isInCart  boolian not null) ";

    public static final int DATABASE_VERSION = 1;


    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: ");
        db.execSQL(CREATE_GROCERY_LIST_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: ");
        db.execSQL("DROP TABLE IF EXISTS tblGroceryList");
        onCreate(db);
    }
}
