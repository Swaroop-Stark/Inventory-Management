package com.example.inventorymanagement;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


public class AddSuppliersFragment extends Fragment {

    DBHelper_CS dbcs;
    DBHelper_CA dbca;
    DBHelper_CD dbcd;
    public ImageView back,noImg;
    RelativeLayout head;
    public Dialog progressDialog,errorDialog,optionsDialog;
    public FloatingActionButton add;
    public LinearLayout addingLayout;
    public EditText supName, supAddress,supNumber;
    public TextView textOfList;
    public Button save,okBtn,cancelBtn;
    public ListView listView;
    public String loginID="",cname="";
    public ArrayList<String> supList,phoneList;
    MyAdapter2 adapter;

    public AddSuppliersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_suppliers, container, false);
        initialization(view);
        perform();
        return view;
    }

    public void initialization(View view)
    {
        supList = new ArrayList<String >(1);
        phoneList = new ArrayList<String >(1);
        dbcs = new DBHelper_CS(getActivity());
        dbca = new DBHelper_CA(getActivity());
        dbcd = new DBHelper_CD(getActivity());
        back = (ImageView)view.findViewById(R.id.back);
        add = (FloatingActionButton)view.findViewById(R.id.fab);
        addingLayout = (LinearLayout)view.findViewById(R.id.addingLayout);
        supName=(EditText)view.findViewById(R.id.supplierName);
        supAddress=(EditText)view.findViewById(R.id.supplierAddress);
        supNumber=(EditText)view.findViewById(R.id.supplierPhoneNumber);
        save =(Button)view.findViewById(R.id.save);
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
        optionsDialog=new Dialog(getContext());
        optionsDialog.setContentView(R.layout.option_dialog);
        optionsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        cancelBtn=(Button)optionsDialog.findViewById(R.id.cancelBtn);
        textOfList=(TextView)optionsDialog.findViewById(R.id.textOfList);
        SharedPreferences sp = getActivity().getSharedPreferences("COMPANY",Context.MODE_PRIVATE);
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

        add.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                int orientation = getContext().getResources().getConfiguration().orientation;
                if (orientation== Configuration.ORIENTATION_PORTRAIT)
                {
                    if (addingLayout.getHeight()==0)
                    {
                        Animation animation = new SlideAnim(addingLayout,0,500);
                        animation.setInterpolator(new AccelerateInterpolator());
                        animation.setDuration(300);
                        addingLayout.setAnimation(animation);
                        addingLayout.startAnimation(animation);
                        add.setImageResource(R.drawable.ic_close_black_24dp);
                    }else
                    {
                        Animation animation = new SlideAnim(addingLayout,500,0);
                        animation.setInterpolator(new AccelerateInterpolator());
                        animation.setDuration(300);
                        addingLayout.setAnimation(animation);
                        addingLayout.startAnimation(animation);
                        add.setImageResource(R.drawable.ic_add_black_24dp);
                    }
                }else {
                    if (addingLayout.getWidth()==0)
                    {
                        Animation animation = new SlideAnim2(addingLayout,0,700);
                        animation.setInterpolator(new AccelerateInterpolator());
                        animation.setDuration(300);
                        addingLayout.setAnimation(animation);
                        addingLayout.startAnimation(animation);
                        add.setImageResource(R.drawable.ic_close_black_24dp);
                    }else
                    {
                        Animation animation = new SlideAnim2(addingLayout,700,0);
                        animation.setInterpolator(new AccelerateInterpolator());
                        animation.setDuration(300);
                        addingLayout.setAnimation(animation);
                        addingLayout.startAnimation(animation);
                        add.setImageResource(R.drawable.ic_add_black_24dp);
                    }
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internalFunction.validate(new EditText[]{supName, supAddress,supNumber})) {
                    Cursor c = dbcs.getSupplierData(loginID,supName.getText().toString());
                    if (c.getCount()==0) {
                        dbcs.addSupplier(loginID, supName.getText().toString(), supAddress.getText().toString(), supNumber.getText().toString());
                        Cursor r = dbcs.getSuppliers(loginID);
                        supList.clear();
                        phoneList.clear();
                        while (r.moveToNext()) {
                            int index = r.getColumnIndex("supplier_name");
                            int index2 = r.getColumnIndex("phone_no");
                            supList.add(r.getString(index));
                            phoneList.add(r.getString(index2));
                        }
                        adapter = new MyAdapter2(getContext(), supList, phoneList);
                        listView.setAdapter(adapter);
                        int orientation = getContext().getResources().getConfiguration().orientation;
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            if (addingLayout.getHeight() == 0) {
                                Animation animation = new SlideAnim(addingLayout, 0, 700);
                                animation.setInterpolator(new AccelerateInterpolator());
                                animation.setDuration(300);
                                addingLayout.setAnimation(animation);
                                addingLayout.startAnimation(animation);
                                add.setImageResource(R.drawable.ic_close_black_24dp);
                            } else {
                                Animation animation = new SlideAnim(addingLayout, 700, 0);
                                animation.setInterpolator(new AccelerateInterpolator());
                                animation.setDuration(300);
                                addingLayout.setAnimation(animation);
                                addingLayout.startAnimation(animation);
                                add.setImageResource(R.drawable.ic_add_black_24dp);
                            }
                        } else {
                            if (addingLayout.getWidth() == 0) {
                                Animation animation = new SlideAnim2(addingLayout, 0, 700);
                                animation.setInterpolator(new AccelerateInterpolator());
                                animation.setDuration(300);
                                addingLayout.setAnimation(animation);
                                addingLayout.startAnimation(animation);
                                add.setImageResource(R.drawable.ic_close_black_24dp);
                            } else {
                                Animation animation = new SlideAnim2(addingLayout, 700, 0);
                                animation.setInterpolator(new AccelerateInterpolator());
                                animation.setDuration(300);
                                addingLayout.setAnimation(animation);
                                addingLayout.startAnimation(animation);
                                add.setImageResource(R.drawable.ic_add_black_24dp);
                            }
                        }
                        supNumber.getText().clear();
                        supAddress.getText().clear();
                        supName.getText().clear();
                        head.setVisibility(View.VISIBLE);
                        noImg.setVisibility(View.GONE);
                    }else{
                        supName.setError("Already registered");
                    }
//                    DBConnection db = new DBConnection();
//                    db.execute();
                }
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.cancel();
                getFragmentManager().popBackStackImmediate();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsDialog.cancel();
            }
        });

    }

    public class SlideAnim extends Animation {
        int iniHeight, finHeight;
        View oView;

        public SlideAnim(View view, int iniHeight, int finHeight) {
            this.oView = view;
            this.iniHeight = iniHeight;
            this.finHeight = finHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight;
            if (oView.getHeight() != finHeight) {
                newHeight = (int) (iniHeight + ((finHeight - iniHeight) * interpolatedTime));
                oView.getLayoutParams().height = newHeight;
                oView.requestLayout();
            }
        }
    }
    public class SlideAnim2 extends Animation {
        int iniHeight, finHeight;
        View oView;

        public SlideAnim2(View view, int iniHeight, int finHeight) {
            this.oView = view;
            this.iniHeight = iniHeight;
            this.finHeight = finHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight;
            if (oView.getWidth() != finHeight) {
                newHeight = (int) (iniHeight + ((finHeight - iniHeight) * interpolatedTime));
                oView.getLayoutParams().width = newHeight;
                oView.requestLayout();
            }
        }
    }

    public void fillList() {
        if (dbcs.getSupplierExist(loginID)) {
            Cursor result = dbcs.getSuppliers(loginID);
            if (result != null && result.getCount() > 0) {
                while (result.moveToNext()) {
                    int index = result.getColumnIndex("supplier_name");
                    int index2 = result.getColumnIndex("phone_no");
                    supList.add(result.getString(index));
                    phoneList.add(result.getString(index2));
                }
            }
            adapter = new MyAdapter2(getContext(), supList, phoneList);
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

