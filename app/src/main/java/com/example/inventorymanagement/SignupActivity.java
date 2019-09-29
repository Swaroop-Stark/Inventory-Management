package com.example.inventorymanagement;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.wang.avi.AVLoadingIndicatorView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import cdflynn.android.library.checkview.CheckView;

public class SignupActivity extends AppCompatActivity {
    public ImageView back;
    public Pinview pinviewtxt;
    public CheckView checkView;
    public EditText companyName,companyTin,companyOwnerName,mobileNumber,emailId,loginId,password,rePassword;
    public CheckBox checkBox;
    public Button submit,okBtn,okBtn2;
    public Dialog pinDialog,progressDialog,errorDialog,requestDialog;
    DBHelper_CA dbca;
    DBHelper_CD dbcd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initialization();
        perform();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignupActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void initialization()
    {
        dbca = new DBHelper_CA(this);
        dbcd = new DBHelper_CD(this);
        checkBox=(CheckBox)findViewById(R.id.checkbox);
        companyName=(EditText) findViewById(R.id.companyName);
        companyTin=(EditText) findViewById(R.id.companyTinNumber);
        companyOwnerName=(EditText) findViewById(R.id.ownerName);
        mobileNumber=(EditText) findViewById(R.id.mobileNumber);
        emailId=(EditText) findViewById(R.id.emailId);
        loginId=(EditText) findViewById(R.id.loginId);
        password=(EditText) findViewById(R.id.password);
        rePassword=(EditText) findViewById(R.id.rePassoword);
        back=(ImageView)findViewById(R.id.back);
        submit=(Button)findViewById(R.id.submit);
        submit.setEnabled(false);
        pinDialog=new Dialog(this);
        pinDialog.setContentView(R.layout.set_pin_dialog);
        pinDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pinviewtxt=(Pinview) pinDialog.findViewById(R.id.pinview);
        progressDialog=new Dialog(this);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        errorDialog=new Dialog(this);
        errorDialog.setContentView(R.layout.error_dialog);
        errorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        okBtn=(Button)errorDialog.findViewById(R.id.okBtn);
        requestDialog=new Dialog(this);
        requestDialog.setContentView(R.layout.request_dialog);
        requestDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        checkView = (CheckView)requestDialog.findViewById(R.id.checkbox);
        okBtn2=(Button)requestDialog.findViewById(R.id.okBtn);
    }

    public void perform()
    {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internalFunction.net(SignupActivity.this)) {
                    if (internalFunction.validate(new EditText[]{companyName, companyTin, companyOwnerName, mobileNumber, emailId, loginId, password, rePassword})) {
                        if (companyTin.getText().toString().length() != 11) {
                            companyTin.setError("Must be 11 digits");
                        } else if (!password.getText().toString().equals(rePassword.getText().toString())) {
                            rePassword.setError("password mismatch");
                        } else {
                            pinDialog.show();
                            pinviewtxt.setPinViewEventListener(new Pinview.PinViewEventListener() {
                                @Override
                                public void onDataEntered(Pinview pinview, boolean fromUser) {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(SignupActivity.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(pinviewtxt.getWindowToken(), 0);
                                    pinDialog.cancel();
                                    Cursor c = dbca.getLogin(loginId.getText().toString(),password.getText().toString());
                                    if (c.getCount()==0)
                                    {
                                        dbca.addCompany(companyName.getText().toString(),companyTin.getText().toString(),companyOwnerName.getText().toString(),mobileNumber.getText().toString(),emailId.getText().toString(),loginId.getText().toString(),password.getText().toString(),Integer.parseInt(pinviewtxt.getValue()));
                                        requestDialog.show();
                                        checkView.check();
                                        requestDialog.setCanceledOnTouchOutside(false);
                                        requestDialog.setCancelable(false);
                                    }else {
                                        Toast.makeText(SignupActivity.this, "already registered", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }else
                {
                    Snackbar.make(findViewById(R.id.idLayout), "No internet", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked())
                {
                    submit.setEnabled(true);
                }else
                {
                    submit.setEnabled(false);
                }
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.cancel();
            }
        });

        okBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkView.uncheck();
                requestDialog.setCanceledOnTouchOutside(false);
                requestDialog.setCancelable(false);
                requestDialog.cancel();
                Intent i = new Intent(SignupActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

}
