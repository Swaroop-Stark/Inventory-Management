package com.example.inventorymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ExpandableListView;

public class DBHelper_CPT extends SQLiteOpenHelper {

    public static final String DB_NAME="IMcpt.db";
    public static final String USER_TABLE="company_products_table";
    public static final String LOGIN_ID="login_id";
    public static final String BILL_NO="bill_no";
    public static final String SUPPLIER_NAME="supplier_name";
    public static final String PRODUCT_NAME="product_name";
    public static final String CATEGORY="category";
    public static final String QUANTITY="quantity";
    public static final String UNIT="unit";
    public static final String EXPIRY_DATE="expiry_date";
    public static final String CREATE_TABLE="CREATE TABLE "+USER_TABLE+"("
            +LOGIN_ID+" TEXT,"
            +BILL_NO+" TEXT,"
            +SUPPLIER_NAME+" TEXT,"
            +PRODUCT_NAME+" TEXT,"
            +CATEGORY+" TEXT,"
            +QUANTITY+" INTEGER,"
            +UNIT+" TEXT,"
            +EXPIRY_DATE+" TEXT);";

    public DBHelper_CPT(Context context) {
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

    public void addData(String loginId,String billNo, String supplierName, String productName, String category, int quantity, String unit, String expiryDate)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID,loginId);
        values.put(BILL_NO,billNo);
        values.put(SUPPLIER_NAME,supplierName);
        values.put(PRODUCT_NAME,productName);
        values.put(CATEGORY,category);
        values.put(QUANTITY,quantity);
        values.put(UNIT,unit);
        values.put(EXPIRY_DATE,expiryDate);
        long id=sqLiteDatabase.insert(USER_TABLE, null,values);
        Log.d("DB","Inserted "+id);
    }

    public boolean changeData(String loginId,String billNo, String supplierName, String productName, String category, int quantity, String unit, String expiryDate)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID,loginId);
        values.put(BILL_NO,billNo);
        values.put(SUPPLIER_NAME,supplierName);
        values.put(PRODUCT_NAME,productName);
        values.put(CATEGORY,category);
        values.put(QUANTITY,quantity);
        values.put(UNIT,unit);
        values.put(EXPIRY_DATE,expiryDate);
        int res=sqLiteDatabase.update(USER_TABLE,values,"login_id=? and bill_no=?",new String[]{loginId,billNo});
        if (res>0){
            return true;
        }else {
            return false;
        }
    }

    public Cursor getProductData(String loginId, String productName)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"' and "+PRODUCT_NAME+" = '"+productName+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }


    public Cursor getData(String loginId)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }

    public int getExpiryDatesCount(String loginId, String date)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select "+EXPIRY_DATE+" from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"' and "+ EXPIRY_DATE +" = '"+date+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor.getCount();
    }

    public Cursor getExpiredProducts(String loginId, String date)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"' and "+ EXPIRY_DATE +" = '"+date+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }

    public int deleteData(String loginID)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(USER_TABLE,"login_id=?",new String[]{loginID});
    }

}
