package com.example.inventorymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper_CStock extends SQLiteOpenHelper {

    public static final String DB_NAME="IMcstock.db";
    public static final String USER_TABLE="company_stock";
    public static final String LOGIN_ID="login_id";
    public static final String PRODUCT_NAME ="product_name";
    public static final String QUANTITY ="quantity";
    public static final String CREATE_TABLE="CREATE TABLE "+USER_TABLE+"("
            +LOGIN_ID+" TEXT,"
            +PRODUCT_NAME+" TEXT,"
            +QUANTITY+" INTEGER);";

    public DBHelper_CStock(Context context) {
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

    public void addProduct(String loginId,String product_name, int quantity)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID,loginId);
        values.put(PRODUCT_NAME,product_name);
        values.put(QUANTITY,quantity);
        long id=sqLiteDatabase.insert(USER_TABLE, null,values);
        Log.d("DB","Inserted "+id);
    }

    public boolean updateQuantity(String loginId, String productName, int quantity)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID,loginId);
        values.put(QUANTITY,quantity);
        int res=sqLiteDatabase.update(USER_TABLE,values,"login_id=? and product_name=?",new String[]{loginId,productName});
        if (res>0){
            return true;
        }else {
            return false;
        }
    }

    public boolean productExist(String loginId,String product_name)
    {
        String Query = "select * from "+USER_TABLE+" where "+LOGIN_ID+" = '"+loginId+"' and "+PRODUCT_NAME+" = '"+product_name+"'";
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

    public boolean getStocksExist(String loginId)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            return true;
        }
        cursor.close();
        sqLiteDatabase.close();
        return false;
    }

    public Cursor getProductNames(String loginId)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }


    public Cursor getQuantity(String loginId,String product_name)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"' and "+PRODUCT_NAME+" = '"+product_name+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }

    public int getOutOfStockCount(String loginId)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select "+QUANTITY+" from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"' and "+QUANTITY+" = 0";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor.getCount();
    }

    public Cursor getOutOfStock(String loginId)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"' and "+QUANTITY+" = 0";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }

    public int deleteData(String loginID)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(USER_TABLE,"login_id=?",new String[]{loginID});
    }
}
