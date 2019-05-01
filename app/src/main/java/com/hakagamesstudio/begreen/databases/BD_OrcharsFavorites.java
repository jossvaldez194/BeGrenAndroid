package com.hakagamesstudio.begreen.databases;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hakagamesstudio.begreen.pojos.OrchardFavorites;

import java.util.ArrayList;

public class BD_OrcharsFavorites {

    private SQLiteDatabase db;

    // Table Name
    public static final String TABLE_ORCHARDS = "Orchards_favorites";
    // Table Columns
    private static final String ORCHARDS_ID = "orchards_id";
    private static final String ORCHARDS_USER_ID = "orchards_Userid";
    private static final String ORCHARDS_NAME = "orchards_name";
    private static final String ORCHARDS_FAVORITE = "orchards_favorite";
    private static final String ORCHARDS_IMAGE = "orchards_image";

    public static String createTable() {

        return "CREATE TABLE "+ TABLE_ORCHARDS + "(" +
                ORCHARDS_ID          +" TEXT," +
                ORCHARDS_USER_ID     +" TEXT," +
                ORCHARDS_NAME        +" TEXT," +
                ORCHARDS_FAVORITE    +" TEXT," +
                ORCHARDS_IMAGE       +" TEXT" + ")";
    }

    public void insertUserData(OrchardFavorites user){
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(ORCHARDS_ID,         user.getOrchards_id());
        values.put(ORCHARDS_USER_ID,    user.getOrchards_userId());
        values.put(ORCHARDS_NAME,       user.getOrchards_name());
        values.put(ORCHARDS_FAVORITE,   user.getOrchards_favorite());
        values.put(ORCHARDS_IMAGE,      user.getOrchards_image());
        db.insert(TABLE_ORCHARDS, null, values);

        // close the Database
        DB_Manager.getInstance().closeDatabase();
    }

    public String getOrcharItemFavorite(String orchard_id) {
        String cartID = "false";
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();
        final String getCartID = "SELECT "+ ORCHARDS_FAVORITE +" FROM "+ TABLE_ORCHARDS + " Where " + ORCHARDS_ID + " = " + orchard_id;
        Cursor cur = db.rawQuery(getCartID, null);
        if (cur != null && cur.moveToFirst()) {
            cartID = cur.getString(cur.getColumnIndex(ORCHARDS_FAVORITE));
            cur.close();
        }
        // close cursor and DB
        DB_Manager.getInstance().closeDatabase();
        return cartID;
    }

    public ArrayList<OrchardFavorites> getCartItems(String CUSTOMERS_ID) {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();

        Cursor cursor=  db.rawQuery("SELECT * FROM " + TABLE_ORCHARDS + " WHERE " + ORCHARDS_USER_ID + " = " + CUSTOMERS_ID, null);
        ArrayList<OrchardFavorites> cartList = new ArrayList<>();
        OrchardFavorites orchardFavorites = new OrchardFavorites();

        if (cursor.moveToFirst()) {
                orchardFavorites.setOrchards_id(cursor.getString(0));
                orchardFavorites.setOrchards_userId(cursor.getString(1));
                orchardFavorites.setOrchards_name(cursor.getString(2));
                orchardFavorites.setOrchards_favorite(cursor.getString(3));
                orchardFavorites.setOrchards_image(cursor.getString(4));

                cartList.add(orchardFavorites);
                cursor.moveToNext();
        }

        // close cursor and DB
        cursor.close();
        DB_Manager.getInstance().closeDatabase();
        return cartList;
    }

    //*********** Update the Details of existing User ********//
    public void updateUserData(OrchardFavorites user){
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();

        ContentValues values = new ContentValues();

        values.put(ORCHARDS_ID,          user.getOrchards_id());
        values.put(ORCHARDS_USER_ID,     user.getOrchards_userId());
        values.put(ORCHARDS_NAME,        user.getOrchards_name());
        values.put(ORCHARDS_FAVORITE,    user.getOrchards_favorite());
        values.put(ORCHARDS_IMAGE,       user.getOrchards_image());

        db.update(TABLE_ORCHARDS, values, ORCHARDS_ID +" = ?", new String[]{user.getOrchards_id()});
        // close the Database
        DB_Manager.getInstance().closeDatabase();
    }

    //*********** Delete specific Item from Cart ********//
    public void deleteCartItem(int cart_id) {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();
        db.delete(TABLE_ORCHARDS, ORCHARDS_ID +" = ?", new String[]{String.valueOf(cart_id)});
        // close the Database
        DB_Manager.getInstance().closeDatabase();
    }

}
