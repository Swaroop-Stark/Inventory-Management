package com.example.inventorymanagement;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Swaroop on 29-08-2018.
 */

public class Session {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;
    public Session(Context context){
        this.context=context;
        preferences=context.getSharedPreferences("Bmi",context.MODE_PRIVATE);
        editor=preferences.edit();
    }
    public void setLoggedIn(boolean loggedin){
        editor.putBoolean("loggedInMode",loggedin);
        editor.commit();
    }
    public boolean loggedIn(){
        return preferences.getBoolean("loggedInMode",false);
    }
}
