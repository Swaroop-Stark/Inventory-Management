package com.example.inventorymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper_CS extends SQLiteOpenHelper {

    public static final String DB_NAME="IMcmc.db";
    public static final String USER_TABLE="company_suppliers";
    public static final String LOGIN_ID="login_id";
    public static final String SUPPLIER_NAME="supplier_name";
    public static final String ADDRESS="address";
    public static final String PHONE_NO="phone_no";
    public static final String CREATE_TABLE="CREATE TABLE "+USER_TABLE+"("
            +LOGIN_ID+" TEXT,"
            +SUPPLIER_NAME+" TEXT,"
            +ADDRESS+" TEXT,"
            +PHONE_NO+" TEXT);";

    public DBHelper_CS(Context context) {
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

    public void addSupplier(String loginId, String supplierName, String address, String phoneNo)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID,loginId);
        values.put(SUPPLIER_NAME,supplierName);
        values.put(ADDRESS,address);
        values.put(PHONE_NO,phoneNo);
        long id=sqLiteDatabase.insert(USER_TABLE, null,values);
        Log.d("DB","Inserted "+id);
    }


    public boolean changeData(String loginId, String supplierName, String address, String phoneNo)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID,loginId);
        values.put(ADDRESS,address);
        values.put(PHONE_NO,phoneNo);
        int res=sqLiteDatabase.update(USER_TABLE,values,"login_id=? and supplier_name=?",new String[]{loginId,supplierName});
        if (res>0){
            return true;
        }else {
            return false;
        }
    }

    public Cursor getSupplierData(String loginId,String supplierName)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"' and "+SUPPLIER_NAME+" = '"+supplierName+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }

    public Cursor getSuppliers(String loginId)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }

    public boolean getSupplierExist(String loginId)
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

    public int deleteData(String loginID)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(USER_TABLE,"login_id=?",new String[]{loginID});
    }

}
