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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class SettingChangesFragment extends Fragment {
    public Dialog progressDialog, errorDialog, employeeDialog, supplierDialog, employeeEditDialog, supplierEditDialog, storageDialog;
    public ImageView back,noImg;
    public TextView titleText,eName,sName,ePhone,sPhone,designation,ePin,address,eName2,sName2;
    public EditText eDesignation,ePhoneNo,sAdress,sPhoneNo,location;
    public Pinview pinview;
    public Switch accessSwitch;
    public FloatingActionButton eCall, sCall, eEdit, sEdit;
    public Button eSave, sSave, doneBtn,okBtn;
    public ListView listView;
    public String loginID = "", cname = "", settingName="",PIN="", access="no access", task="", category="",addr="";
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    DBHelper_CA dbca;
    DBHelper_CE dbce;
    DBHelper_CS dbcs;
    DBHelper_CPC dbcpc;
    DBHelper_CD dbcd;

    public SettingChangesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_changes, container, false);
        initialize(view);
        perform();
        return view;
    }

    public void initialize(View view)
    {
        titleText = (TextView)view.findViewById(R.id.titleText);
        back =(ImageView)view.findViewById(R.id.back);
        listView = (ListView)view.findViewById(R.id.list);
        dbca = new DBHelper_CA(getActivity());
        dbce = new DBHelper_CE(getActivity());
        dbcs = new DBHelper_CS(getActivity());
        dbcpc = new DBHelper_CPC(getActivity());
        dbcd = new DBHelper_CD(getActivity());
        list = new ArrayList<String>(1);
        noImg = (ImageView)view.findViewById(R.id.noImg);
        errorDialog=new Dialog(getContext());
        errorDialog.setContentView(R.layout.error_dialog);
        errorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        okBtn =(Button)errorDialog.findViewById(R.id.okBtn);
        employeeDialog = new Dialog(getActivity());
        employeeDialog.setContentView(R.layout.employee_dialog);
        employeeDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        eName = (TextView)employeeDialog.findViewById(R.id.name);
        ePhone = (TextView)employeeDialog.findViewById(R.id.phoneNo);
        designation = (TextView)employeeDialog.findViewById(R.id.designation);
        ePin = (TextView) employeeDialog.findViewById(R.id.pin);
        eCall = (FloatingActionButton)employeeDialog.findViewById(R.id.call);
        eEdit = (FloatingActionButton)employeeDialog.findViewById(R.id.editBtn);
        supplierDialog = new Dialog(getActivity());
        supplierDialog.setContentView(R.layout.supplier_dialog);
        supplierDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        sName = (TextView)supplierDialog.findViewById(R.id.name);
        sPhone = (TextView)supplierDialog.findViewById(R.id.phoneNo);
        address = (TextView) supplierDialog.findViewById(R.id.address);
        sCall = (FloatingActionButton)supplierDialog.findViewById(R.id.call);
        sEdit = (FloatingActionButton)supplierDialog.findViewById(R.id.editBtn);
        employeeEditDialog = new Dialog(getActivity());
        employeeEditDialog.setContentView(R.layout.employee_edit_dialog);
        employeeEditDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        eName2 = (TextView)employeeEditDialog.findViewById(R.id.name);
        eDesignation = (EditText)employeeEditDialog.findViewById(R.id.empDesignation);
        ePhoneNo = (EditText)employeeEditDialog.findViewById(R.id.empPhoneNumber);
        pinview = (Pinview)employeeEditDialog.findViewById(R.id.pinview);
        accessSwitch = (Switch)employeeEditDialog.findViewById(R.id.switchAccess);
        eSave = (Button)employeeEditDialog.findViewById(R.id.save);
        supplierEditDialog = new Dialog(getActivity());
        supplierEditDialog.setContentView(R.layout.supplier_edit_dialog);
        supplierEditDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        sName2 = (TextView)supplierEditDialog.findViewById(R.id.name);
        sAdress = (EditText)supplierEditDialog.findViewById(R.id.supplierAddress);
        sPhoneNo = (EditText)supplierEditDialog.findViewById(R.id.supplierPhoneNumber);
        sSave = (Button)supplierEditDialog.findViewById(R.id.save);
        storageDialog = new Dialog(getContext());
        storageDialog.setContentView(R.layout.product_location_dialog);
        storageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        doneBtn = (Button) storageDialog.findViewById(R.id.doneBtn);
        location = (EditText) storageDialog.findViewById(R.id.location);
        SharedPreferences sp = getActivity().getSharedPreferences("COMPANY",Context.MODE_PRIVATE);
        cname = sp.getString("company_name", "Company");
        SharedPreferences sps = getActivity().getSharedPreferences("SETTINGS",Context.MODE_PRIVATE);
        settingName = sps.getString("setting_name", "setting");
        titleText.setText(settingName);
        Cursor res = dbca.getCompany(cname);
        while (res.moveToNext()) {
            int index = res.getColumnIndex("login_id");
            this.loginID = res.getString(index);
        }
        progressDialog=new Dialog(getContext());
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        noImg.setVisibility(View.GONE);
        fillList(settingName);
    }

    public void perform()
    {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });


        if (accessSwitch.isChecked())
        {
            access="access";
        }else
        {
            access="no access";
        }
        accessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    access="access";
                }else {
                    access="no access";
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (settingName.equals("Employees"))
                {
                    Cursor cursor = dbce.getUserData(loginID,list.get(position));
                    while (cursor.moveToNext())
                    {
                        int index = cursor.getColumnIndex("designation");
                        eName.setText(list.get(position));
                        designation.setText(cursor.getString(index));
                        ePhone.setText(cursor.getString(index+1));
                        PIN = cursor.getString(index+2);
                        if (PIN.length()<4)
                        {
                            int size = PIN.length();
                            for (int i=0;i<(4-size);i++)
                            {
                                PIN = "0"+PIN;
                            }
                            if (PIN.length()==4)
                            {
                                ePin.setText("PIN : "+PIN+" with "+cursor.getString(index+3));
                            }
                        }else {
                            ePin.setText("PIN : "+PIN+" with "+cursor.getString(index+3));
                        }
                        employeeDialog.show();
                    }
                }else if (settingName.equals("Suppliers"))
                {
                    Cursor cursor = dbcs.getSupplierData(loginID,list.get(position));
                    while (cursor.moveToNext())
                    {
                        int index = cursor.getColumnIndex("address");
                        sName.setText(list.get(position));
                        addr = cursor.getString(index);
                        address.setText("Address : "+addr);
                        sPhone.setText(cursor.getString(index+1));
                        supplierDialog.show();
                    }
                }else if (settingName.equals("Categories"))
                {
                    category = list.get(position);
                    Cursor cursor = dbcpc.getCategoryData(loginID,category);
                    while (cursor.moveToNext())
                    {
                        int index = cursor.getColumnIndex("location");
                        location.setText(cursor.getString(index));
                        storageDialog.show();
                    }
                }
            }
        });

        eEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eName2.setText(eName.getText().toString());
                ePhoneNo.setText(ePhone.getText().toString());
                eDesignation.setText(designation.getText().toString());
                if (PIN.length()<4)
                {
                    int size = PIN.length();
                    for (int i=0;i<(4-size);i++)
                    {
                        PIN = "0"+PIN;
                    }
                    if (PIN.length()==4)
                    {
                        pinview.setValue(PIN);
                    }
                }else {
                    pinview.setValue(PIN);
                }
                employeeEditDialog.show();
            }
        });

        sEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sName2.setText(sName.getText().toString());
                sAdress.setText(addr);
                sPhoneNo.setText(sPhone.getText().toString());
                supplierEditDialog.show();
            }
        });

        eCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        eSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbce.changeUserData(loginID,eName2.getText().toString(),eDesignation.getText().toString(),ePhoneNo.getText().toString(),Integer.parseInt(pinview.getValue()),access);
                employeeEditDialog.cancel();
                employeeDialog.cancel();
            }
        });

        sSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbcs.changeData(loginID,sName2.getText().toString(),sAdress.getText().toString(),sPhoneNo.getText().toString());
                supplierEditDialog.cancel();
                supplierDialog.cancel();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbcpc.changeLocation(loginID,category,location.getText().toString());
                storageDialog.cancel();
            }
        });
    }

    public void fillList(String settingName) {
        if (settingName.equals("Employees"))
        {
            if (dbce.getEmployeesExist(loginID)) {
                Cursor result = dbce.getEmployees(loginID);
                if (result != null && result.getCount() > 0) {
                    while (result.moveToNext()) {
                        int index = result.getColumnIndex("employee_name");
                        list.add(result.getString(index));
                    }
                }
                adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list);
                listView.setAdapter(adapter);
                noImg.setVisibility(View.GONE);
            }else
            {
                noImg.setVisibility(View.VISIBLE);
            }
        }else if (settingName.equals("Suppliers"))
        {
            if (dbcs.getSupplierExist(loginID)) {
                Cursor result = dbcs.getSuppliers(loginID);
                if (result != null && result.getCount() > 0) {
                    while (result.moveToNext()) {
                        int index = result.getColumnIndex("supplier_name");
                        list.add(result.getString(index));
                    }
                }
                adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list);
                listView.setAdapter(adapter);
                noImg.setVisibility(View.GONE);
            }else
            {
                noImg.setVisibility(View.VISIBLE);
            }
        }else if (settingName.equals("Categories"))
        {
            if (dbcpc.categoryExist(loginID)) {
                Cursor result = dbcpc.getCategories(loginID);
                if (result != null && result.getCount() > 0) {
                    while (result.moveToNext()) {
                        int index = result.getColumnIndex("category");
                        list.add(result.getString(index));
                    }
                }
                adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list);
                listView.setAdapter(adapter);
                noImg.setVisibility(View.GONE);
            }else
            {
                noImg.setVisibility(View.VISIBLE);
            }
        }
    }
}
