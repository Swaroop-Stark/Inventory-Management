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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


public class AddEmployeeFragment extends Fragment {
    DBHelper_CA dbac;
    DBHelper_CE dbce;
    DBHelper_CD dbcd;
    public ImageView back,noImg;
    RelativeLayout head;
    public Dialog progressDialog,errorDialog,optionsDialog;
    public FloatingActionButton add;
    public LinearLayout addingLayout;
    public TextView textOfList;
    public Switch accessSwitch;
    public Button save,okBtn,cancelBtn;
    public EditText empName, empDesignation, empPhoneNumber;
    public Pinview pinview;
    public ListView listView;
    public String access="no access",loginID="",cname="",task="";
    public ArrayList<String> empList,eDesignation;
    MyAdapter2 adapter;
    public AddEmployeeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_employee, container, false);
        initialization(view);
        perform();
        return view;
    }

    public void initialization(View view)
    {
        empList = new ArrayList<String>(1);
        eDesignation = new ArrayList<String>(1);
        dbac =  new DBHelper_CA(getActivity());
        dbce = new DBHelper_CE(getActivity());
        dbcd = new DBHelper_CD(getActivity());
        back = (ImageView)view.findViewById(R.id.back);
        add = (FloatingActionButton)view.findViewById(R.id.fab);
        addingLayout = (LinearLayout)view.findViewById(R.id.addingLayout);
        empName = (EditText)view.findViewById(R.id.empName);
        empDesignation = (EditText)view.findViewById(R.id.empDesignation);
        empPhoneNumber = (EditText)view.findViewById(R.id.empPhoneNumber);
        pinview = (Pinview) view.findViewById(R.id.pinview);
        accessSwitch = (Switch)view.findViewById(R.id.switchAccess);
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
        okBtn =(Button)errorDialog.findViewById(R.id.okBtn);
        optionsDialog=new Dialog(getContext());
        optionsDialog.setContentView(R.layout.option_dialog);
        optionsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        cancelBtn=(Button)optionsDialog.findViewById(R.id.cancelBtn);
        textOfList=(TextView)optionsDialog.findViewById(R.id.textOfList);
        SharedPreferences sp = getActivity().getSharedPreferences("COMPANY",Context.MODE_PRIVATE);
        cname=sp.getString("company_name","Company");
        Cursor res = dbac.getCompany(cname);
        while (res.moveToNext())
        {
            int index = res.getColumnIndex("login_id");
            this.loginID=res.getString(index);
        }
        Log.d("Login_id is ",""+loginID);
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
                if (orientation== Configuration.ORIENTATION_PORTRAIT) {
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
                }else
                {
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

        if (accessSwitch.isChecked())
        {
            access="access";
        }else {
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internalFunction.validate(new EditText[]{empName, empDesignation, empPhoneNumber})) {
                    if (pinview.getValue().length()==4) {
                        Cursor c = dbce.getUserData(loginID,empName.getText().toString());
                        if (c.getCount()==0) {
                            dbce.addEmployee(loginID, empName.getText().toString(), empDesignation.getText().toString(), empPhoneNumber.getText().toString(), Integer.parseInt(pinview.getValue()), access);
                            Cursor r = dbce.getEmployees(loginID);
                            empList.clear();
                            eDesignation.clear();
                            while (r.moveToNext()) {
                                int index = r.getColumnIndex("employee_name");
                                int index2 = r.getColumnIndex("designation");
                                empList.add(r.getString(index));
                                eDesignation.add(r.getString(index2));
                            }
                            adapter = new MyAdapter2(getContext(), empList, eDesignation);
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
                            for (int i = 0; i < pinview.getChildCount(); i++) {
                                EditText child = (EditText) pinview.getChildAt(i);
                                child.setText("");
                            }
                            empPhoneNumber.getText().clear();
                            empDesignation.getText().clear();
                            empName.getText().clear();
                            head.setVisibility(View.VISIBLE);
                            noImg.setVisibility(View.GONE);
                        }else {
                            empName.setError("Already registered");
                        }
                    }
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


    public class SlideAnim extends Animation{
        int iniHeight, finHeight;
        View oView;
        public SlideAnim(View view, int iniHeight, int finHeight)
        {
            this.oView=view;
            this.iniHeight=iniHeight;
            this.finHeight=finHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight;
            if(oView.getHeight()!=finHeight)
            {
                newHeight=(int)(iniHeight+((finHeight-iniHeight)*interpolatedTime));
                oView.getLayoutParams().height=newHeight;
                oView.requestLayout();
            }
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
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
        if (dbce.getEmployeesExist(loginID)) {
            Cursor result = dbce.getEmployees(loginID);
            if (result != null && result.getCount() > 0) {
                while (result.moveToNext()) {
                    int index = result.getColumnIndex("employee_name");
                    int index2 = result.getColumnIndex("designation");
                    empList.add(result.getString(index));
                    eDesignation.add(result.getString(index2));
                }
            }
            adapter = new MyAdapter2(getContext(), empList, eDesignation);
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


