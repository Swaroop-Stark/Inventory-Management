package com.example.inventorymanagement;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class internalFunction {


    public static boolean net(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            return true;
        }else
        {
            return false;
        }
    }

    public static void loadingStart(Dialog progressDialog)
    {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void loadingStop(Dialog progressDialog)
    {
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setCancelable(true);
        progressDialog.cancel();
    }

    public static boolean validate(EditText[] fields)
    {
        for (int i=0;i<fields.length;i++)
        {
            EditText currentEditText = fields[i];
            if(currentEditText.getText().toString().length()==0)
            {
                currentEditText.setError("Field empty");
                return false;
            }
        }
        return true;
    }

    public static boolean validate(EditText[] fields, AutoCompleteTextView[] fields2)
    {
        int j=0;
        for (int i=0;i<fields.length;i++)
        {
            EditText currentEditText = fields[i];
            if(currentEditText.getText().toString().length()==0)
            {
                currentEditText.setError("Field empty");
                j++;
            }
        }
        for (int i=0;i<fields2.length;i++)
        {
            AutoCompleteTextView currentEditText = fields2[i];
            if(currentEditText.getText().toString().length()==0)
            {
                currentEditText.setError("Field empty");
                j++;
            }
        }
        if (j==0) {
            return true;
        }else {
            return false;
        }
    }
    public static void notificationBadgeNumber(TextView textView, DBHelper_CPT dbcpt,DBHelper_CStock dbcst,String loginID)
    {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int count = dbcpt.getExpiryDatesCount(loginID,dateFormat.format(date))+dbcst.getOutOfStockCount(loginID);
        if (count>0)
        {
            textView.setVisibility(View.VISIBLE);
            textView.setText(""+count);
        }else {
            textView.setVisibility(View.GONE);
        }
    }

    private class DBConnection extends AsyncTask<String,String,String>
    {
        Context context;
        String task;
        String[] arr;
        int[] intArr;

        public DBConnection(Context context, String task, String[] arr)
        {
            this.arr=arr;
            this.task=task;
            this.context = context;
        }

        public DBConnection(String task,String[] arr, int[] intArr)
        {
            this.arr=arr;
            this.intArr=intArr;
            this.task=task;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
