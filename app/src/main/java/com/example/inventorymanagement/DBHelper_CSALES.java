package com.example.inventorymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper_CSALES extends SQLiteOpenHelper {

    public static final String DB_NAME = "IMcsales.db";
    public static final String USER_TABLE = "company_sales";
    public static final String LOGIN_ID = "login_id";
    public static final String PRODUCT_NAME = "product_name";
    public static final String SOLD_AMOUNT = "quantity";
    public static final String CREATE_TABLE = "CREATE TABLE " + USER_TABLE + "("
            + LOGIN_ID + " TEXT,"
            + PRODUCT_NAME + " TEXT,"
            + SOLD_AMOUNT + " INTEGER);";

    public DBHelper_CSALES(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    public void addSales(String login_id, String bill_no, String proName, int qty) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID, login_id);
        values.put(PRODUCT_NAME, proName);
        values.put(SOLD_AMOUNT, qty);
        long id = sqLiteDatabase.insert(USER_TABLE, null, values);
        Log.d("DB", "Inserted " + id);
    }


    public boolean updateData(String login_id, String proName, int qty) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SOLD_AMOUNT, qty);
        int res = sqLiteDatabase.update(USER_TABLE, values, "login_id=? and product_name=?", new String[]{login_id,proName});
        if (res > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor getData(String login_id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String Query = "select * from " + USER_TABLE + " where " + LOGIN_ID + " = '" + login_id + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        return cursor;
    }

    public boolean getStock(String login_id, String product_name) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String Query = "select * from " + USER_TABLE + " where " + LOGIN_ID + " = '" + login_id + "' and "+PRODUCT_NAME+" = '"+product_name+"'";
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor getMin(String login_id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String Query = "select * from " + USER_TABLE + " where "+SOLD_AMOUNT+" = (select min("+SOLD_AMOUNT+") from "+USER_TABLE+") and " + LOGIN_ID + " = '" + login_id + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        return cursor;
    }

    public Cursor getMax(String login_id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String Query = "select * from " + USER_TABLE + " where "+SOLD_AMOUNT+" = (select max("+SOLD_AMOUNT+") from "+USER_TABLE+") and " + LOGIN_ID + " = '" + login_id + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        return cursor;
    }
}