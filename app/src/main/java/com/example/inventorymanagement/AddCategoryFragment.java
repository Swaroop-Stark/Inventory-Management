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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class AddCategoryFragment extends Fragment {

    DBHelper_CPC dbcpc;
    DBHelper_CA dbca;
    DBHelper_CD dbcd;
    public ImageView back,noImg;
    RelativeLayout head;
    public Dialog progressDialog, errorDialog, optionsDialog, storageDialog;
    public TextView textOfList;
    public Button okBtn, addBtn, cancelBtn, doneBtn;
    public EditText location, category;
    public String loginID = "", cname = "";
    public ArrayList<String> categories, cLocation;
    public ListView listView;
    MyAdapter2 adapter;

    public AddCategoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);
        initialization(view);
        perform();
        return view;
    }

    public void initialization(View view) {
        dbcpc = new DBHelper_CPC(getActivity());
        dbca = new DBHelper_CA(getActivity());
        dbcd = new DBHelper_CD(getActivity());
        back = (ImageView) view.findViewById(R.id.back);
        addBtn = (Button) view.findViewById(R.id.add);
        head = (RelativeLayout) view.findViewById(R.id.look);
        noImg = (ImageView)view.findViewById(R.id.noImg);
        listView = (ListView) view.findViewById(R.id.list);
        category = (EditText) view.findViewById(R.id.category);
        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        errorDialog = new Dialog(getContext());
        errorDialog.setContentView(R.layout.error_dialog);
        errorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        okBtn = (Button) errorDialog.findViewById(R.id.okBtn);
        optionsDialog = new Dialog(getContext());
        optionsDialog.setContentView(R.layout.option_dialog);
        optionsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        cancelBtn = (Button) optionsDialog.findViewById(R.id.cancelBtn);
        textOfList = (TextView) optionsDialog.findViewById(R.id.textOfList);
        storageDialog = new Dialog(getContext());
        storageDialog.setContentView(R.layout.product_location_dialog);
        storageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        doneBtn = (Button) storageDialog.findViewById(R.id.doneBtn);
        location = (EditText) storageDialog.findViewById(R.id.location);
        categories = new ArrayList<String>(1);
        cLocation = new ArrayList<String>(1);
        SharedPreferences sp = getActivity().getSharedPreferences("COMPANY", Context.MODE_PRIVATE);
        cname = sp.getString("company_name", "Company");
        Cursor res = dbca.getCompany(cname);
        while (res.moveToNext()) {
            int index = res.getColumnIndex("login_id");
            this.loginID = res.getString(index);
        }
        noImg.setVisibility(View.GONE);
        head.setVisibility(View.VISIBLE);
        fillList();
    }

    public void perform() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.cancel();
                getFragmentManager().popBackStackImmediate();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category.getText().length() > 0) {
                    storageDialog.show();
                } else {
                    category.setError("Field empty");
                }
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storageDialog.cancel();
                Cursor c = dbcpc.getCategoryData(loginID,category.getText().toString());
                if (c.getCount()==0)
                {
                    dbcpc.addCategory(loginID,category.getText().toString(),location.getText().toString());
                    Cursor r = dbcpc.getCategories(loginID);
                    categories.clear();
                    cLocation.clear();
                    while (r.moveToNext()) {
                        int index = r.getColumnIndex("category");
                        categories.add(r.getString(index));
                        cLocation.add(r.getString(index + 1));
                    }
                    adapter = new MyAdapter2(getContext(), categories, cLocation);
                    listView.setAdapter(adapter);
                    category.getText().clear();
                    location.getText().clear();
                    head.setVisibility(View.VISIBLE);
                    noImg.setVisibility(View.GONE);
                }else {
                    Toast.makeText(getActivity(), "This category already exists.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsDialog.cancel();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textOfList.setText(categories.get(position)+"\nLocation: "+cLocation.get(position));
                optionsDialog.show();
            }
        });
    }

        public void fillList() {
            internalFunction.loadingStart(progressDialog);
            if (dbcpc.categoryExist(loginID)) {
                Cursor result = dbcpc.getCategories(loginID);
                if (result != null && result.getCount() > 0) {
                    while (result.moveToNext()) {
                        int index = result.getColumnIndex("category");
                        categories.add(result.getString(index));
                        cLocation.add(result.getString(index + 1));
                    }
                }
                adapter = new MyAdapter2(getContext(), categories, cLocation);
                listView.setAdapter(adapter);
                head.setVisibility(View.VISIBLE);
                noImg.setVisibility(View.GONE);
            }else
            {
                head.setVisibility(View.GONE);
                noImg.setVisibility(View.VISIBLE);
            }
            internalFunction.loadingStop(progressDialog);
        }
}

