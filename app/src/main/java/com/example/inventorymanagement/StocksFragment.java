package com.example.inventorymanagement;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class StocksFragment extends Fragment implements SearchView.OnQueryTextListener {
    public ImageView back,noImg;
    RelativeLayout head;
    public SearchView searchView;
    public Button okBtn;
    public ListView listView;
    public Dialog progressDialog,errorDialog;
    public String loginID="",cname;
    ArrayList<threeParam> tp;
    private threeParamAdapter threeParamAdapter;
    DBHelper_CA dbca;
    DBHelper_CPS dbcps;
    DBHelper_CPT dbcpt;
    DBHelper_CPC dbcpc;
    DBHelper_CStock dbcst;
    public StocksFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocks, container, false);
        initialize(view);
        perform();
        return view;
    }

    public void initialize(View view)
    {
        dbca  = new DBHelper_CA(getActivity());
        dbcps = new DBHelper_CPS(getActivity());
        dbcpt = new DBHelper_CPT(getActivity());
        dbcpc = new DBHelper_CPC(getActivity());
        dbcst = new DBHelper_CStock(getActivity());
        back = (ImageView)view.findViewById(R.id.back);
        searchView = (SearchView)view.findViewById(R.id.searchv);
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
        tp = new ArrayList<threeParam>(1);
        SharedPreferences sp = getActivity().getSharedPreferences("COMPANY", Context.MODE_PRIVATE);
        cname=sp.getString("company_name","Company");
        Cursor res = dbca.getCompany(cname);
        while (res.moveToNext())
        {
            int index = res.getColumnIndex("login_id");
            this.loginID=res.getString(index);
        }
        noImg.setVisibility(View.GONE);
        head.setVisibility(View.VISIBLE);
        fillList();
    }

    public void perform()
    {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
    }

    public void fillList() {
        if (dbcst.getStocksExist(loginID))
        {
            Cursor result = dbcst.getProductNames(loginID);
            if (result != null && result.getCount() > 0) {
                while (result.moveToNext()) {
                    int index = result.getColumnIndex("product_name");
                    String category="",loc="";
                    Cursor result2 = dbcpt.getProductData(loginID,result.getString(index));
                    if (result2 != null && result2.getCount() > 0)
                    {
                        while (result2.moveToNext())
                        {
                            int index2 = result2.getColumnIndex("category");
                            category = result2.getString(index2);
                        }
                        Cursor result3 = dbcpc.getCategoryData(loginID,category);
                        if (result3 != null && result3.getCount() > 0)
                        {
                            while (result3.moveToNext())
                            {
                                int index3 = result3.getColumnIndex("location");
                                loc=result3.getString(index3);
                            }
                        }
                    }
                    tp.add(new threeParam(result.getString(index),result.getString(index+1),loc));
                }
            }
            threeParamAdapter = new threeParamAdapter(getContext(),tp);
            listView.setAdapter(threeParamAdapter);
            listView.setTextFilterEnabled(true);
            setupSearchView();
            head.setVisibility(View.VISIBLE);
            noImg.setVisibility(View.GONE);
        }else
        {
            head.setVisibility(View.GONE);
            noImg.setVisibility(View.VISIBLE);
        }
    }

    private void setupSearchView()
    {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);

        searchView.setQueryHint("Search here");
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (TextUtils.isEmpty(s))
        {
            listView.clearTextFilter();
        }else {
            listView.setFilterText(s);
        }
        return true;
    }
}
