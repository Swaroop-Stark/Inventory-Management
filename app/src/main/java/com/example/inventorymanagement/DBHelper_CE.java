package com.example.inventorymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper_CE extends SQLiteOpenHelper {

    public static final String DB_NAME="IMce.db";
    public static final String USER_TABLE="company_employees";
    public static final String LOGIN_ID="login_id";
    public static final String EMPLOYEE_NAME="employee_name";
    public static final String DESIGNATION="designation";
    public static final String PHONE_NO="phone_no";
    public static final String USER_PIN="pin";
    public static final String ACCESS="access";
    public static final String CREATE_TABLE = "CREATE TABLE " + USER_TABLE + "("
            + LOGIN_ID + " TEXT,"
            + EMPLOYEE_NAME + " TEXT,"
            + DESIGNATION + " TEXT,"
            + PHONE_NO + " TEXT,"
            + USER_PIN + " INTEGER,"
            + ACCESS + " TEXT);";

    public DBHelper_CE(Context context) {
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

    public void addEmployee(String loginId,String employeeName , String designation, String phoneNo,int pin, String access)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGIN_ID,loginId);
        values.put(EMPLOYEE_NAME,employeeName);
        values.put(DESIGNATION,designation);
        values.put(PHONE_NO,phoneNo);
        values.put(USER_PIN,pin);
        values.put(ACCESS,access);
        long id=sqLiteDatabase.insert(USER_TABLE, null,values);
        Log.d("DB","Inserted "+id);
    }

    public boolean verifyPin(String loginId,int pin)
    {
        String Query = "select * from "+USER_TABLE+" where "+LOGIN_ID+" = '"+loginId+"' and "+USER_PIN+" = "+pin+"";
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

    public boolean changeUserData(String loginId,String employeeName , String designation, String phoneNo,int pin, String access)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DESIGNATION,designation);
        values.put(PHONE_NO,phoneNo);
        values.put(USER_PIN,pin);
        values.put(ACCESS,access);
        int res=sqLiteDatabase.update(USER_TABLE,values,"login_id=? and employee_name=?",new String[]{loginId,employeeName});
        if (res>0){
            return true;
        }else {
            return false;
        }
    }

    public Cursor getUser(String loginId,String pin)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"' and "+USER_PIN+" = "+pin+"";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }

    public Cursor getUserData(String loginId,String name)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"' and "+EMPLOYEE_NAME+" = '"+name+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }

    public Cursor getEmployees(String loginId)
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String Query ="select * from "+USER_TABLE+ " where "+LOGIN_ID+" = '"+loginId+"'";
        Cursor cursor =sqLiteDatabase.rawQuery(Query,null);
        return cursor;
    }

    public boolean getEmployeesExist(String loginId)
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
