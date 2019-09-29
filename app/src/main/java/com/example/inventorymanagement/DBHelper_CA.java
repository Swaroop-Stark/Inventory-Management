package com.example.inventorymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper_CA extends SQLiteOpenHelper {

    public static final String DB_NAME = "IM.db";
    public static final String USER_TABLE = "company_account";
    public static final String COMPANY_NAME = "company_name";
    public static final String COMPANY_TIN = "company_tin";
    public static final String OWNER_NAME = "owner_name";
    public static final String MOBILE_NUMBER = "mobile_number";
    public static final String EMAIL_ID = "email_id";
    public static final String LOGIN_ID = "login_id";
    public static final String PASSWORD = "password";
    public static final String USER_PIN = "pin";
    public static final String CREATE_TABLE = "CREATE TABLE " + USER_TABLE + "("
            + COMPANY_NAME + " TEXT,"
            + COMPANY_TIN + " TEXT,"
            + OWNER_NAME + " TEXT,"
            + MOBILE_NUMBER + " INTEGER,"
            + EMAIL_ID + " TEXT,"
            + LOGIN_ID + " TEXT,"
            + PASSWORD + " TEXT,"
            + USER_PIN + " INTEGER);";

    public DBHelper_CA(Context context) {
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

    public void addCompany(String companyName, String companyTin, String ownerName, String mobileNumber, String emailId, String loginId, String password, int pin) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COMPANY_NAME, companyName);
        values.put(COMPANY_TIN, companyTin);
        values.put(OWNER_NAME, ownerName);
        values.put(MOBILE_NUMBER, mobileNumber);
        values.put(EMAIL_ID, emailId);
        values.put(LOGIN_ID, loginId);
        values.put(PASSWORD, password);
        values.put(USER_PIN, pin);
        long id = sqLiteDatabase.insert(USER_TABLE, null, values);
        Log.d("DB", "Inserted " + id);
    }

    public boolean verifyPin(String companyName, int pin) {
        String Query = "select * from " + USER_TABLE + " where " + COMPANY_NAME + "='" + companyName + "' and " + USER_PIN + " = " + pin + "";
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return true;
        }
        cursor.close();
        sqLiteDatabase.close();
        return false;
    }

    public boolean verifyExist(String companyName, String loginId) {
        String Query = "select * from " + USER_TABLE + " where " + COMPANY_NAME + "='" + companyName + "' and " + LOGIN_ID + " = '" + loginId + "'";
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return true;
        }
        cursor.close();
        sqLiteDatabase.close();
        return false;
    }

    public boolean updateData(String companyName, String ownerName, String mobileNumber, String emailId, String password, int pin) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COMPANY_NAME, companyName);
        values.put(OWNER_NAME, ownerName);
        values.put(MOBILE_NUMBER, mobileNumber);
        values.put(EMAIL_ID, emailId);
        values.put(PASSWORD, password);
        values.put(USER_PIN, pin);
        int res = sqLiteDatabase.update(USER_TABLE, values, "company_name=?", new String[]{companyName});
        if (res > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor getCompany(String companyName) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String Query = "select * from " + USER_TABLE + " where " + COMPANY_NAME + " = " + "'" + companyName + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        return cursor;
    }


    public Cursor getLogin(String login_id,String password) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String Query = "select * from " + USER_TABLE + " where " + LOGIN_ID + " = '"+ login_id + "' and "+PASSWORD+" = '"+password+"'";
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        return cursor;
    }

}