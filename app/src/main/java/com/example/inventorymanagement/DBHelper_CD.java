package com.example.inventorymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper_CD extends SQLiteOpenHelper {

    public static final String DB_NAME = "IMcds.db";
    public static final String USER_TABLE = "company_data";
    public static final String dataType = " INTEGER AUTOINCREMENT, ";
    public static final String COUNT = "count";
    public static final String LOGIN_ID = "login_id";
    public static final String CHANGES = "changes";
    public static final String CREATE_TABLE = "CREATE TABLE " + USER_TABLE + "("
            + COUNT + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + LOGIN_ID + " TEXT,"
            + CHANGES + " TEXT);";

    public DBHelper_CD(Context context) {
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

    public void addChange(String login_id, String changes) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID, login_id);
        values.put(CHANGES, changes);
        long id = sqLiteDatabase.insert(USER_TABLE, null, values);
        Log.d("DB", "Inserted " + id);
    }


    public boolean updateData(int count, String login_id, String changes) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COUNT, count);
        values.put(CHANGES, changes);
        int res = sqLiteDatabase.update(USER_TABLE, values, "login_id=?", new String[]{login_id});
        if (res > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int getData(String login_id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String Query = "select * from " + USER_TABLE + " where " + LOGIN_ID + " = '" + login_id + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        return cursor.getCount();
    }
}