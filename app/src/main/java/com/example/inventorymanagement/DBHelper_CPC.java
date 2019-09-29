package com.example.inventorymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;

public class DBHelper_CPC extends SQLiteOpenHelper {

    public static final String DB_NAME="IMcpc.db";
    public static final String USER_TABLE="company_products_category";
    public static final String LOGIN_ID="login_id";
    public static final String CATEGORY="category";
    public static final String LOCATION="location";
    public static final String CREATE_TABLE="CREATE TABLE "+USER_TABLE+"("
            +LOGIN_ID+" TEXT,"
            +CATEGORY+" TEXT,"
            +LOCATION+" TEXT);";

    public DBHelper_CPC(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+USER_TABLE);
        onCreate(db);
    }

    public void addCategory(String loginId,String category , String location)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID,loginId);
        values.put(CATEGORY,category);
        values.put(LOCATION,location);
        long id=sqLiteDatabase.insert(USER_TABLE, null,values);
        Log.d("DB","Inserted "+id);
    }

    public boolean categoryExist(String loginId)
    {
        String Query = "select * from "+USER_TABLE+" where "+LOGIN_ID+" = '"+loginId+"'";
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            return true;
        }
        cursor.close();
        sqLiteDatabase.close();
        return false;
    }

    public boolean changeLocation(String loginId, String category, String location)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOCATION,location);
        int res=sqLiteDatabase.update(USER_TABLE,values,"login_id=? and category=?",new String[]{loginId,category});
        if (res>0){
            return true;
        }else {
            return false;
        }
    }

    public Cursor getCategoryData(String loginId,String category)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"' and "+ CATEGORY +" = '"+category+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }

    public Cursor getCategories(String loginId)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }

    public int deleteData(String loginID)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(USER_TABLE,"login_id=?",new String[]{loginID});
    }
}
