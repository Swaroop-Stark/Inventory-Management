package com.example.inventorymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper_CCB extends SQLiteOpenHelper {

    public static final String DB_NAME = "IMccb.db";
    public static final String USER_TABLE = "company_customer_bills";
    public static final String LOGIN_ID = "login_id";
    public static final String BILL_NO = "bill_no";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String PHONE_NO = "phone_no";
    public static final String DATE = "date";
    public static final String DISCOUNT = "discount";
    public static final String TOTAL_AMOUNT = "total_amount";
    public static final String PAYED_AMOUNT = "payed_amount";
    public static final String STATUS = "status";
    public static final String CREATE_TABLE = "CREATE TABLE " + USER_TABLE + "("
            + LOGIN_ID + " TEXT,"
            + BILL_NO + " TEXT,"
            + CUSTOMER_NAME + " TEXT,"
            + PHONE_NO + " TEXT,"
            + DATE + " TEXT,"
            + DISCOUNT + " INTEGER,"
            + TOTAL_AMOUNT + " INTEGER,"
            + PAYED_AMOUNT + " INTEGER,"
            + STATUS + " TEXT);";

    public DBHelper_CCB(Context context) {
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

    public void addCustomerBill(String login_id, String bill_no, String cusName, String phone, String date, int discount, int tamt, int pamt, String status) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID, login_id);
        values.put(BILL_NO, bill_no);
        values.put(CUSTOMER_NAME, cusName);
        values.put(PHONE_NO, phone);
        values.put(DATE, date);
        values.put(DISCOUNT, discount);
        values.put(TOTAL_AMOUNT, tamt);
        values.put(PAYED_AMOUNT, pamt);
        values.put(STATUS, status);
        long id = sqLiteDatabase.insert(USER_TABLE, null, values);
        Log.d("DB", "Inserted " + id);
    }


    public boolean updateData(String login_id, String bill_no, String cusName, int tamt, int pamt, String status) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID, login_id);
        values.put(TOTAL_AMOUNT, tamt);
        values.put(PAYED_AMOUNT, pamt);
        values.put(STATUS, status);
        int res = sqLiteDatabase.update(USER_TABLE, values, "login_id=? and bill_no=? and customer_name=?", new String[]{login_id,bill_no,cusName});
        if (res > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor getData(String login_id, String status) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String Query = "select * from " + USER_TABLE + " where " + LOGIN_ID + " = '" + login_id + "' and "+STATUS+" = '"+status+"'";
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        return cursor;
    }

}