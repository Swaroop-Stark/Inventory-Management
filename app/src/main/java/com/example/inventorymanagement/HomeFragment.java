package com.example.inventorymanagement;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {
    ViewPager viewPager;
    SwipeAdapter swipeAdapter;
    private Session session;
    DBHelper_CA dbca;
    DBHelper_CE dbce;
    DBHelper_CPT dbcpt;
    DBHelper_CStock dbcst;
    DBHelper_CD dbcd;
    String loginID="",cname="",status="";
    public Dialog entriesDialog,optionsDialog,logoutDialog,pinDialog;
    public Pinview pinviewtxt;
    public TextView companyName,about,logout,badgeText;
    public Button entriesBtn, billingBtn, financeBtn, stockBtn,yesBtn,noBtn;
    public ImageView addEmployee, addSuppliers, addCategory, addProduct, dots, notificationBell;
    private Timer timer;
    private int current_position = 0;
    public int[] imageResource ={R.mipmap.slide1,R.mipmap.slide2,R.mipmap.slide3,R.mipmap.slide4};
    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initialization(view);
        perform();
        session = new Session(getContext());
        if (!session.loggedIn()){
            logout();
        }
        return view;
    }

    @SuppressLint("WrongViewCast")
    public void initialization(View view)
    {
        dbca = new DBHelper_CA(getContext());
        dbce = new DBHelper_CE(getContext());
        dbcpt = new DBHelper_CPT(getContext());
        dbcst = new DBHelper_CStock(getContext());
        dbcd = new DBHelper_CD(getContext());
        SharedPreferences sp = getActivity().getSharedPreferences("COMPANY",Context.MODE_PRIVATE);
        companyName=(TextView)view.findViewById(R.id.companyName);
        dots=(ImageView)view.findViewById(R.id.dots);
        badgeText = (TextView)view.findViewById(R.id.badge);
        notificationBell = (ImageView)view.findViewById(R.id.notificationBell);
        viewPager = (ViewPager)view.findViewById(R.id.view_pager);
        entriesBtn = (Button)view.findViewById(R.id.entries);
        billingBtn = (Button)view.findViewById(R.id.billing);
        financeBtn = (Button)view.findViewById(R.id.finance);
        stockBtn = (Button)view.findViewById(R.id.stocks);
        pinDialog=new Dialog(getContext());
        pinDialog.setContentView(R.layout.pin_dialog);
        pinDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pinviewtxt=(Pinview)pinDialog.findViewById(R.id.pinview);
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
        entriesDialog = new Dialog(getContext());
        entriesDialog.setContentView(R.layout.entries_dialog);
        entriesDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        addEmployee = (ImageView)entriesDialog.findViewById(R.id.addemployee);
        addSuppliers = (ImageView)entriesDialog.findViewById(R.id.addsuppliers);
        addCategory = (ImageView)entriesDialog.findViewById(R.id.addcategory);
        addProduct = (ImageView)entriesDialog.findViewById(R.id.addproduct);
        cname=sp.getString("company_name","Company");
        Cursor res = dbca.getCompany(cname);
        while (res.moveToNext())
        {
            int index = res.getColumnIndex("login_id");
            this.loginID=res.getString(index);
        }
        companyName.setText(sp.getString("company_name","Company"));
        swipeAdapter = new SwipeAdapter(getContext(),imageResource);
        viewPager.setAdapter(swipeAdapter);
        createSlideShow();
        internalFunction.notificationBadgeNumber(badgeText,dbcpt,dbcst,loginID);
        SharedPreferences sp2 = getActivity().getSharedPreferences("Notification",Context.MODE_PRIVATE);
        status = sp2.getString("status", "on");
    }

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

        entriesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entriesDialog.show();
            }
        });

        billingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinDialog.show();
                pinviewtxt.setPinViewEventListener(new Pinview.PinViewEventListener() {
                    @Override
                    public void onDataEntered(Pinview pinview, boolean fromUser) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(SignupActivity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(pinviewtxt.getWindowToken(), 0);
                        if (dbca.verifyPin(cname,Integer.parseInt(pinview.getValue())))
                        {
                            pinDialog.cancel();
                            for (int i =0;i<pinviewtxt.getChildCount();i++)
                            {
                                EditText child = (EditText)pinviewtxt.getChildAt(i);
                                child.setText("");
                            }
                            BillingFragment fragment = new BillingFragment();
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame,fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }else if (dbce.verifyPin(loginID,Integer.parseInt(pinview.getValue())))
                        {
                            pinDialog.cancel();
                            for (int i =0;i<pinviewtxt.getChildCount();i++)
                            {
                                EditText child = (EditText)pinviewtxt.getChildAt(i);
                                child.setText("");
                            }
                            BillingFragment fragment = new BillingFragment();
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame,fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                        else {
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

        financeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinanceFragment fragment = new FinanceFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        stockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StocksFragment fragment = new StocksFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        addEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entriesDialog.cancel();
                pinDialog.show();
                pinviewtxt.setPinViewEventListener(new Pinview.PinViewEventListener() {
                    @Override
                    public void onDataEntered(Pinview pinview, boolean fromUser) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(SignupActivity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(pinviewtxt.getWindowToken(), 0);
                        if (dbca.verifyPin(cname,Integer.parseInt(pinview.getValue())))
                        {
                            pinDialog.cancel();
                            for (int i =0;i<pinviewtxt.getChildCount();i++)
                            {
                                EditText child = (EditText)pinviewtxt.getChildAt(i);
                                child.setText("");
                            }
                            AddEmployeeFragment fragment = new AddEmployeeFragment();
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame,fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }else if (dbce.verifyPin(loginID,Integer.parseInt(pinview.getValue())))
                        {
                            pinDialog.cancel();
                            for (int i =0;i<pinviewtxt.getChildCount();i++)
                            {
                                EditText child = (EditText)pinviewtxt.getChildAt(i);
                                child.setText("");
                            }
                            Toast.makeText(getActivity(), "Employees are not allowed", Toast.LENGTH_SHORT).show();
                        }
                        else {
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

        addSuppliers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entriesDialog.cancel();
                pinDialog.show();
                pinviewtxt.setPinViewEventListener(new Pinview.PinViewEventListener() {
                    @Override
                    public void onDataEntered(Pinview pinview, boolean fromUser) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(SignupActivity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(pinviewtxt.getWindowToken(), 0);
                        if (dbca.verifyPin(cname,Integer.parseInt(pinview.getValue())))
                        {
                            pinDialog.cancel();
                            for (int i =0;i<pinviewtxt.getChildCount();i++)
                            {
                                EditText child = (EditText)pinviewtxt.getChildAt(i);
                                child.setText("");
                            }
                            AddSuppliersFragment fragment = new AddSuppliersFragment();
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame,fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }else if (dbce.verifyPin(loginID,Integer.parseInt(pinview.getValue())))
                        {
                            pinDialog.cancel();
                            for (int i =0;i<pinviewtxt.getChildCount();i++)
                            {
                                EditText child = (EditText)pinviewtxt.getChildAt(i);
                                child.setText("");
                            }
                            AddSuppliersFragment fragment = new AddSuppliersFragment();
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame,fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                        else {
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

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinDialog.show();
                pinviewtxt.setPinViewEventListener(new Pinview.PinViewEventListener() {
                    @Override
                    public void onDataEntered(Pinview pinview, boolean fromUser) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(SignupActivity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(pinviewtxt.getWindowToken(), 0);
                        if (dbca.verifyPin(cname,Integer.parseInt(pinview.getValue())))
                        {
                            pinDialog.cancel();
                            for (int i =0;i<pinviewtxt.getChildCount();i++)
                            {
                                EditText child = (EditText)pinviewtxt.getChildAt(i);
                                child.setText("");
                            }
                            AddCategoryFragment fragment = new AddCategoryFragment();
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame,fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }else if (dbce.verifyPin(loginID,Integer.parseInt(pinview.getValue())))
                        {
                            pinDialog.cancel();
                            for (int i =0;i<pinviewtxt.getChildCount();i++)
                            {
                                EditText child = (EditText)pinviewtxt.getChildAt(i);
                                child.setText("");
                            }
                            AddCategoryFragment fragment = new AddCategoryFragment();
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame,fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                        else {
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
                entriesDialog.cancel();
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinDialog.show();
                pinviewtxt.setPinViewEventListener(new Pinview.PinViewEventListener() {
                    @Override
                    public void onDataEntered(Pinview pinview, boolean fromUser) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(SignupActivity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(pinviewtxt.getWindowToken(), 0);
                        if (dbca.verifyPin(cname,Integer.parseInt(pinview.getValue())))
                        {
                            pinDialog.cancel();
                            for (int i =0;i<pinviewtxt.getChildCount();i++)
                            {
                                EditText child = (EditText)pinviewtxt.getChildAt(i);
                                child.setText("");
                            }
                            Intent i = new Intent(getActivity(),AddProductActivity.class);
                            startActivity(i);
                        }else if (dbce.verifyPin(loginID,Integer.parseInt(pinview.getValue())))
                        {
                            pinDialog.cancel();
                            for (int i =0;i<pinviewtxt.getChildCount();i++)
                            {
                                EditText child = (EditText)pinviewtxt.getChildAt(i);
                                child.setText("");
                            }
                            Intent i = new Intent(getActivity(),AddProductActivity.class);
                            startActivity(i);
                        }
                        else {
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
                entriesDialog.cancel();
            }
        });
    }

    private void createSlideShow()
    {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                 if (current_position==imageResource.length)
                 {
                     current_position=0;
                 }
                 viewPager.setCurrentItem(current_position++,true);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        },250,2500);
    }

    public void logout(){
        session.setLoggedIn(false);
        startActivity(new Intent(getActivity(),MainActivity.class));
        getActivity().finish();
    }

}
