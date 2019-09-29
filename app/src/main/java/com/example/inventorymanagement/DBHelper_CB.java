package com.example.inventorymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper_CB extends SQLiteOpenHelper {

    public static final String DB_NAME = "IMcb.db";
    public static final String USER_TABLE = "company_billing";
    public static final String LOGIN_ID = "login_id";
    public static final String BILL_NO = "bill_no";
    public static final String PRODUCT_NAME = "product_name";
    public static final String QUANTITY = "quantity";
    public static final String DATE = "date";
    public static final String MONTH = "month";
    public static final String CREATE_TABLE = "CREATE TABLE " + USER_TABLE + "("
            + LOGIN_ID + " TEXT,"
            + BILL_NO + " TEXT,"
            + PRODUCT_NAME + " TEXT,"
            + QUANTITY + " INTEGER,"
            + DATE + " TEXT,"
            + MONTH + " INTEGER);";

    public DBHelper_CB(Context context) {
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

    public void addBill(String login_id, String bill_no, String proName, int qty, String date,int month) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID, login_id);
        values.put(BILL_NO, bill_no);
        values.put(PRODUCT_NAME, proName);
        values.put(QUANTITY, qty);
        values.put(DATE, date);
        values.put(MONTH, month);
        long id = sqLiteDatabase.insert(USER_TABLE, null, values);
        Log.d("DB", "Inserted " + id);
    }

    public Cursor getData(String login_id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String Query = "select * from " + USER_TABLE + " where " + LOGIN_ID + " = '" + login_id + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        return cursor;
    }

    public Cursor get(String login_id, int month) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String Query = "select sum("+QUANTITY+") from " + USER_TABLE + " where " + LOGIN_ID + " = '" + login_id + "' and "+MONTH+" = "+month;
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        return cursor;
    }
}