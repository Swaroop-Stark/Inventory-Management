package com.example.inventorymanagement;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.wang.avi.AVLoadingIndicatorView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;


public class DashboardFragment extends Fragment {
    private Session session;
    public Dialog progressDialog,errorDialog,optionsDialog,logoutDialog;
    public BarChart barChart;
    public TextView companyName,about,logout,badgeText,count1,count2,product_name1,product_name2;
    public ImageView dots, notificationBell;
    public Button okBtn,yesBtn,noBtn;
    public String loginID = "", cname = "",status="";
    DBHelper_CA dbca;
    DBHelper_CPT dbcpt;
    DBHelper_CStock dbcst;
    DBHelper_CSALES dbsales;
    DBHelper_CB dbcb;
    ArrayList<Integer> months;

    public DashboardFragment() {

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initialize(view);
        perform();
//        DBConnection db = new DBConnection();
//        db.execute();
        session = new Session(getContext());
        if (!session.loggedIn()){
            logout();
        }
        return view;
    }

    public void initialize(View view)
    {
        dbca = new DBHelper_CA(getActivity());
        dbcpt = new DBHelper_CPT(getContext());
        dbcst = new DBHelper_CStock(getActivity());
        dbsales = new DBHelper_CSALES(getActivity());
        dbcb = new DBHelper_CB(getActivity());
        months=new ArrayList<Integer>(1);
        SharedPreferences sp = getActivity().getSharedPreferences("COMPANY",Context.MODE_PRIVATE);
        companyName=(TextView)view.findViewById(R.id.companyName);
        dots=(ImageView)view.findViewById(R.id.dots);
        badgeText = (TextView)view.findViewById(R.id.badge);
        notificationBell = (ImageView)view.findViewById(R.id.notificationBell);
        barChart = (BarChart)view.findViewById(R.id.barChart);
        count1 = (TextView)view.findViewById(R.id.count1);
        count2 = (TextView)view.findViewById(R.id.count2);
        product_name1 = (TextView) view.findViewById(R.id.productName1);
        product_name2 = (TextView) view.findViewById(R.id.productName2);
        logoutDialog = new Dialog(getContext());
        logoutDialog.setContentView(R.layout.logout_dialog);
        logoutDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        yesBtn = (Button)logoutDialog.findViewById(R.id.yesbtn);
        noBtn = (Button)logoutDialog.findViewById(R.id.nobtn);
        optionsDialog = new Dialog(getContext());
        optionsDialog.setContentView(R.layout.options_dialog);
        optionsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        about=(TextView)optionsDialog.findViewById(R.id.about);
        logout=(TextView)optionsDialog.findViewById(R.id.logout);
        progressDialog=new Dialog(getContext());
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        errorDialog=new Dialog(getContext());
        errorDialog.setContentView(R.layout.error_dialog);
        errorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        okBtn=(Button)errorDialog.findViewById(R.id.okBtn);
        cname= sp.getString("company_name","Company");
        companyName.setText(cname);
        Cursor res = dbca.getCompany(cname);
        while (res.moveToNext()) {
            int index = res.getColumnIndex("login_id");
            this.loginID = res.getString(index);
        }
        internalFunction.notificationBadgeNumber(badgeText,dbcpt,dbcst,loginID);
        SharedPreferences sp2 = getActivity().getSharedPreferences("Notification",Context.MODE_PRIVATE);
        status = sp2.getString("status", "on");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void perform()
    {
        if (status.equals("on"))
        {
            notificationBell.setVisibility(View.VISIBLE);
            badgeText.setVisibility(View.VISIBLE);
        }else {
            notificationBell.setVisibility(View.INVISIBLE);
            badgeText.setVisibility(View.INVISIBLE);
        }

        notificationBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationFragment fragment = new NotificationFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsDialog.show();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.show();
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                Toast.makeText(getActivity(), "Logout", Toast.LENGTH_SHORT).show();
                optionsDialog.cancel();
                logoutDialog.cancel();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.cancel();
            }
        });
        String productName1="Non",productName2="Non";
        int q1=0,q2=0;
        Cursor res = dbsales.getMax(loginID);
        while (res.moveToNext())
        {
            productName1 = res.getString(res.getColumnIndex("product_name"));
            q1 = res.getInt(res.getColumnIndex("quantity"));
        }
        Cursor res1 = dbsales.getMin(loginID);
        while (res1.moveToNext())
        {
            productName2 = res1.getString(res1.getColumnIndex("product_name"));
            q2 = res1.getInt(res1.getColumnIndex("quantity"));
        }
        product_name1.setText(productName1);
        product_name2.setText(productName2);
        count1.setText(""+q1);
        count2.setText(""+q2);
        for (int i=0;i<12;i++)
        {
            Cursor res2 = dbcb.get(loginID,i+1);
            if (res2.getCount()==0)
            {
                months.add(0);
            }else {
                while (res2.moveToNext()) {
                    months.add(res2.getInt(0));
                }
            }
        }
        Cursor c = dbcb.getData(loginID);
        if (c.getCount()!=0) {
            while (c.moveToNext()) {
                Log.d("Product naem", c.getString(c.getColumnIndex("product_name")));
                Log.d("Month", ""+c.getInt(c.getColumnIndex("month")));
            }
        }else {
            Log.d("Data","No data");
        }
        barGraph(months);
    }

    public void logout(){
        session.setLoggedIn(false);
        startActivity(new Intent(getActivity(),MainActivity.class));
        getActivity().finish();
    }

    public void barGraph(ArrayList<Integer> months)
    {
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>(1);
        for (int i = 0 ; i < 12 ; i++)
        {
            barEntries.add(new BarEntry(months.get(i),i));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries,"Data");
        ArrayList<String> dates = new ArrayList<>(1);
        dates.add("Jan");
        dates.add("Feb");
        dates.add("Mar");
        dates.add("Apr");
        dates.add("May");
        dates.add("Jun");
        dates.add("Jul");
        dates.add("Aug");
        dates.add("Sept");
        dates.add("Oct");
        dates.add("Nov");
        dates.add("Dec");
        BarData data = new BarData(dates,barDataSet);
        barChart.setData(data);
    }
}

