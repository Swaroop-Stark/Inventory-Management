package com.example.inventorymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper_CPS extends SQLiteOpenHelper {

    public static final String DB_NAME="IMcps.db";
    public static final String USER_TABLE="company_products_stock_table";
    public static final String LOGIN_ID="login_id";
    public static final String BILL_NO="bill_no";
    public static final String PRODUCT_CODE="product_code";
    public static final String PRODUCT_NAME="product_name";
    public static final String CP="cp";
    public static final String SP="sp";
    public static final String CREATE_TABLE="CREATE TABLE "+USER_TABLE+"("
            +LOGIN_ID+" TEXT,"
            +BILL_NO+" TEXT,"
            +PRODUCT_CODE+" TEXT,"
            +PRODUCT_NAME+" TEXT,"
            +CP+" INTEGER,"
            +SP+" INTEGER);";

    public DBHelper_CPS(Context context) {
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

    public void addProducts(String loginID,String billNo, String productCode, String productName, int cp, int sp)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID,loginID);
        values.put(BILL_NO,billNo);
        values.put(PRODUCT_CODE,productCode);
        values.put(PRODUCT_NAME,productName);
        values.put(CP,cp);
        values.put(SP,sp);
        long id=sqLiteDatabase.insert(USER_TABLE, null,values);
        Log.d("DB","Inserted "+id);
    }

    public boolean productCodeExistCheck(String loginId, String productCode)
    {
        String Query = "select * from "+USER_TABLE+" where "+LOGIN_ID+" = '"+loginId+"' and "+PRODUCT_CODE+" = '"+productCode+"'";
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

    public boolean changeProduct(String loginId,String billNo, String productCode, String productName, int cp, int sp)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID,loginId);
        values.put(BILL_NO,billNo);
        values.put(PRODUCT_CODE,productCode);
        values.put(PRODUCT_NAME,productName);
        values.put(CP,cp);
        values.put(SP,sp);
        int res=sqLiteDatabase.update(USER_TABLE,values,"login_id=? and product_code=?",new String[]{loginId,productCode});
        if (res>0){
            return true;
        }else {
            return false;
        }
    }

    public boolean getProductCode(String loginId, String productCode)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"' and "+PRODUCT_CODE+" = '"+productCode+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            return true;
        }
        cursor.close();
        sqLiteDatabase.close();
        return false;
    }

    public Cursor getProduct(String loginId, String productCode)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"' and "+PRODUCT_CODE+" = '"+productCode+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }

    public int deleteData(String loginID)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(USER_TABLE,"login_id=?",new String[]{loginID});
    }
}
