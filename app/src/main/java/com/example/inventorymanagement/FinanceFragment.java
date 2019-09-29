package com.example.inventorymanagement;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class FinanceFragment extends Fragment {
    public ImageView back,noImg;
    RelativeLayout head;
    public Button okBtn;
    public TabLayout tabLayout;
    public TextView title1;
    public ListView listView;
    public Dialog progressDialog,errorDialog;
    public ArrayList<String> sNames, cNames, sBill, cBill, sDues, cDues;
    public String loginID="",cname="",task="";
    DBHelper_CA dbac;
    DBHelper_CCB dbccb;
    DBHelper_CP dbcp;
    MyAdapter adapter;
    public FinanceFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finanace, container, false);
        initialize(view);
        perform();
        fill("Supplier name");
        return view;
    }

    public void initialize(View view)
    {
        dbac = new DBHelper_CA(getActivity());
        dbcp = new DBHelper_CP(getActivity());
        dbccb = new DBHelper_CCB(getActivity());
        sNames = new ArrayList<String>(1);
        sBill  = new ArrayList<String>(1);
        sDues  = new ArrayList<String>(1);
        cNames  = new ArrayList<String>(1);
        cBill  = new ArrayList<String>(1);
        cDues  = new ArrayList<String>(1);
        back = (ImageView)view.findViewById(R.id.back);
        title1 = (TextView)view.findViewById(R.id.title1);
        tabLayout = (TabLayout)view.findViewById(R.id.tab);
        listView = (ListView)view.findViewById(R.id.list);
        head = (RelativeLayout) view.findViewById(R.id.look);
        noImg = (ImageView)view.findViewById(R.id.noImg);
        progressDialog=new Dialog(getContext());
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        errorDialog=new Dialog(getContext());
        errorDialog.setContentView(R.layout.error_dialog);
        errorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        okBtn=(Button)errorDialog.findViewById(R.id.okBtn);
        SharedPreferences sp = getActivity().getSharedPreferences("COMPANY", Context.MODE_PRIVATE);
        cname=sp.getString("company_name","Company");
        Cursor res = dbac.getCompany(cname);
        while (res.moveToNext())
        {
            int index = res.getColumnIndex("login_id");
            this.loginID=res.getString(index);
        }
        noImg.setVisibility(View.GONE);
        head.setVisibility(View.VISIBLE);
    }

    public void perform()
    {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().equals("TO SUPPLIER"))
                {
                    fill("Supplier name");

                }else if (tab.getText().toString().equals("FROM CUSTOMER"))
                {
                    fill("Customer name");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void fill(String task)
    {
        if (task.equals("Supplier name"))
        {
            Cursor res = dbcp.getData(loginID,"not payed");
            if (res.getCount()>0) {
                while (res.moveToNext()) {
                    sBill.add(res.getString(res.getColumnIndex("bill_no")));
                    sNames.add(res.getString(res.getColumnIndex("supplier_name")));
                    sDues.add("" + (res.getInt(res.getColumnIndex("total_amount")) - res.getInt(res.getColumnIndex("payed_amount"))));
                }
                adapter = new MyAdapter(getContext(), sNames, sBill, sDues);
                listView.setAdapter(adapter);
                head.setVisibility(View.VISIBLE);
                noImg.setVisibility(View.GONE);
            }else
            {
                head.setVisibility(View.GONE);
                noImg.setVisibility(View.VISIBLE);
            }
        }else {
            Cursor res = dbccb.getData(loginID,"not payed");
            if (res.getCount()>0) {
                while (res.moveToNext()) {
                    cBill.add(res.getString(res.getColumnIndex("bill_no")));
                    cNames.add(res.getString(res.getColumnIndex("customer_name")));
                    cDues.add("" + (res.getInt(res.getColumnIndex("total_amount")) - res.getInt(res.getColumnIndex("payed_amount"))));
                }
                adapter = new MyAdapter(getContext(), cNames, cBill, cDues);
                listView.setAdapter(adapter);
                head.setVisibility(View.VISIBLE);
                noImg.setVisibility(View.GONE);
            }else
            {
                head.setVisibility(View.GONE);
                noImg.setVisibility(View.VISIBLE);
            }
        }
    }
}

