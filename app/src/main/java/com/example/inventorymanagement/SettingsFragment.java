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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {
    private Session session;
    public CheckBox checkBox;
    public Dialog progressDialog,errorDialog,optionsDialog,logoutDialog,companyDialog,pinDialog;
    public TextView companyName,about,logout,badgeText;
    public ImageView dots, notificationBell;
    public EditText companyOwnerName,mobileNumber,emailId,password;
    public ListView listView;
    public Button okBtn,yesBtn,noBtn,saveBtn;
    public Pinview pinview,pinviewtxt;
    public ArrayList<String> title,subTitle;
    public String loginID = "", cname = "",status="";
    DBHelper_CA dbca;
    DBHelper_CE dbce;
    DBHelper_CD dbcd;
    DBHelper_CPT dbcpt;
    DBHelper_CStock dbcst;
    MyAdapter3 adapter;
    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initialize(view);
        perform();
        session = new Session(getContext());
        if (!session.loggedIn()){
            logout();
        }
        return view;
    }
    public void initialize(View view)
    {
        dbca = new DBHelper_CA(getActivity());
        dbce = new DBHelper_CE(getActivity());
        dbcpt = new DBHelper_CPT(getContext());
        dbcst = new DBHelper_CStock(getContext());
        dbcd = new DBHelper_CD(getActivity());
        title = new ArrayList<String>(1);
        subTitle = new ArrayList<String>(1);
        SharedPreferences sp = getActivity().getSharedPreferences("COMPANY", MODE_PRIVATE);
        companyName=(TextView)view.findViewById(R.id.companyName);
        listView = (ListView)view.findViewById(R.id.list);
        dots=(ImageView)view.findViewById(R.id.dots);
        badgeText = (TextView)view.findViewById(R.id.badge);
        checkBox = (CheckBox)view.findViewById(R.id.notifications);
        notificationBell = (ImageView)view.findViewById(R.id.notificationBell);
        logoutDialog = new Dialog(getContext());
        logoutDialog.setContentView(R.layout.logout_dialog);
        logoutDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        yesBtn = (Button)logoutDialog.findViewById(R.id.yesbtn);
        noBtn = (Button)logoutDialog.findViewById(R.id.nobtn);
        pinDialog=new Dialog(getContext());
        pinDialog.setContentView(R.layout.pin_dialog);
        pinDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pinviewtxt=(Pinview)pinDialog.findViewById(R.id.pinview);
        companyDialog = new Dialog(getActivity());
        companyDialog.setContentView(R.layout.company_dialog);
        companyDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        companyOwnerName = (EditText)companyDialog.findViewById(R.id.ownerName);
        mobileNumber = (EditText)companyDialog.findViewById(R.id.mobileNumber);
        emailId = (EditText)companyDialog.findViewById(R.id.emailId);
        password = (EditText)companyDialog.findViewById(R.id.password);
        pinview = (Pinview) companyDialog.findViewById(R.id.pinview);
        saveBtn = (Button) companyDialog.findViewById(R.id.saveBtn);
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
        title.add("Company details");
        title.add("Employees");
        title.add("Suppliers");
        title.add("Categories");
        subTitle.add("Manage company details");
        subTitle.add("Manage employees");
        subTitle.add("Manage suppliers");
        subTitle.add("Manage categories");
        adapter = new MyAdapter3(getContext(),title,subTitle);
        listView.setAdapter(adapter);
        internalFunction.notificationBadgeNumber(badgeText,dbcpt,dbcst,loginID);
        SharedPreferences sp2 = getActivity().getSharedPreferences("Notification",Context.MODE_PRIVATE);
        status = sp2.getString("status", "on");
    }

    public void perform()
    {
        if (status.equals("on"))
        {
            checkBox.setChecked(true);
            notificationBell.setVisibility(View.VISIBLE);
            badgeText.setVisibility(View.VISIBLE);
        }else {
            checkBox.setChecked(false);
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

        SharedPreferences s = getActivity().getSharedPreferences("Notification",MODE_PRIVATE);
        final SharedPreferences.Editor editor=s.edit();
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    editor.putString("status","on");
                    editor.apply();
                    notificationBell.setVisibility(View.VISIBLE);
                    badgeText.setVisibility(View.VISIBLE);
                }else {
                    editor.putString("status","off");
                    editor.apply();
                    notificationBell.setVisibility(View.INVISIBLE);
                    badgeText.setVisibility(View.INVISIBLE);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView title=(TextView)view.findViewById(R.id.productName);
                final String textTxt = title.getText().toString();
                pinDialog.show();
                pinviewtxt.setPinViewEventListener(new Pinview.PinViewEventListener() {
                    @Override
                    public void onDataEntered(Pinview pinView, boolean fromUser) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(SignupActivity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(pinviewtxt.getWindowToken(), 0);
                        if (dbca.verifyPin(cname,Integer.parseInt(pinView.getValue()))) {
                            pinDialog.cancel();
                            for (int i = 0; i < pinviewtxt.getChildCount(); i++) {
                                EditText child = (EditText) pinviewtxt.getChildAt(i);
                                child.setText("");
                            }
                            if (textTxt.equals("Company details"))
                            {
                                Cursor cursor = dbca.getCompany(cname);
                                while (cursor.moveToNext())
                                {
                                    companyOwnerName.setText(cursor.getString(cursor.getColumnIndex("owner_name")));
                                    mobileNumber.setText(cursor.getString(cursor.getColumnIndex("mobile_number")));
                                    emailId.setText(cursor.getString(cursor.getColumnIndex("email_id")));
                                    password.setText(cursor.getString(cursor.getColumnIndex("password")));
                                    pinview.setValue(cursor.getString(cursor.getColumnIndex("pin")));
                                }
                                companyDialog.show();
                            }else {

                                SharedPreferences s = getActivity().getSharedPreferences("SETTINGS",MODE_PRIVATE);
                                SharedPreferences.Editor editor=s.edit();
                                editor.putString("setting_name",textTxt);
                                editor.apply();
                                SettingChangesFragment fragment = new SettingChangesFragment();
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.frame,fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();

                            }
                        }else if (dbce.verifyPin(loginID,Integer.parseInt(pinView.getValue()))) {
                            pinDialog.cancel();
                            for (int i = 0; i < pinviewtxt.getChildCount(); i++) {
                                EditText child = (EditText) pinviewtxt.getChildAt(i);
                                child.setText("");
                            }
                            Toast.makeText(getActivity(), "Employees are not allowed", Toast.LENGTH_SHORT).show();
                        }else {
                            pinDialog.cancel();
                            for (int i =0;i<pinviewtxt.getChildCount();i++)
                            {
                                EditText child = (EditText)pinviewtxt.getChildAt(i);
                                child.setText("");
                            }
                            Toast.makeText(getActivity(), "Wrong pin", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internalFunction.validate(new EditText[]{companyOwnerName,mobileNumber,emailId,password})) {
                    if (pinview.getValue().length()==4) {
                        dbca.updateData(cname,companyOwnerName.getText().toString(),mobileNumber.getText().toString(),emailId.getText().toString(),password.getText().toString(),Integer.parseInt(pinview.getValue()));
                        companyDialog.cancel();
                    }else {
                        Toast.makeText(getActivity(), "Fill the PIN", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void logout(){
        session.setLoggedIn(false);
        startActivity(new Intent(getActivity(),MainActivity.class));
        getActivity().finish();
    }
}