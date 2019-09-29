package com.example.inventorymanagement;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotificationFragment extends Fragment {

    public ImageView back,noImg;
    public ListView listView;
    public Button okBtn;
    public String loginID="",cname="";
    public TabLayout tabLayout;
    DBHelper_CA dbac;
    DBHelper_CStock dbcst;
    DBHelper_CPT dbcpt;
    ArrayList<String> expiryList, stockList;
    ArrayAdapter<String> adapter;
    public NotificationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        initialize(view);
        perform();
        return view;
    }

    public void initialize(View view)
    {
        dbac = new DBHelper_CA(getActivity());
        dbcst = new DBHelper_CStock(getActivity());
        dbcpt = new DBHelper_CPT(getActivity());
        back = (ImageView)view.findViewById(R.id.back);
        tabLayout = (TabLayout)view.findViewById(R.id.tab);
        listView = (ListView)view.findViewById(R.id.list);
        noImg = (ImageView)view.findViewById(R.id.noImg);
        expiryList = new ArrayList<String>(1);
        stockList = new ArrayList<String>(1);
        SharedPreferences sp = getActivity().getSharedPreferences("COMPANY", Context.MODE_PRIVATE);
        cname=sp.getString("company_name","Company");
        Cursor res = dbac.getCompany(cname);
        while (res.moveToNext())
        {
            int index = res.getColumnIndex("login_id");
            this.loginID=res.getString(index);
        }
    }

    public void perform()
    {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        Cursor cst = dbcst.getOutOfStock(loginID);
        if (cst.getCount()>0) {
            while (cst.moveToNext()) {
                int index = cst.getColumnIndex("product_name");
                stockList.add(cst.getString(index));
            }
        }
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Cursor cpt = dbcpt.getExpiredProducts(loginID,dateFormat.format(date));
        while (cpt.moveToNext())
        {
            int index = cpt.getColumnIndex("product_name");
            expiryList.add(cpt.getString(index));
        }
        if (expiryList.size()==0)
        {
            noImg.setVisibility(View.VISIBLE);
        }else {
            noImg.setVisibility(View.GONE);
        }
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,expiryList);
        listView.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().equals("EXPIRY"))
                {
                    if (expiryList.size()==0)
                    {
                        noImg.setVisibility(View.VISIBLE);
                    }else {
                        noImg.setVisibility(View.GONE);
                    }
                    adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,expiryList);
                }else if (tab.getText().toString().equals("OUT OF STOCK"))
                {if (stockList.size()==0)
                {
                    noImg.setVisibility(View.VISIBLE);
                }else {
                    noImg.setVisibility(View.GONE);
                }
                    adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,stockList);
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
