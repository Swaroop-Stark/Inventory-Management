package com.example.inventorymanagement;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {
    private Session session;
    public ImageView imgs;
    public TextView title,description;
    public EditText loginId,password;
    public Button info, signUp,logIn;
    public Dialog progressDialog,errorDialog,infoDialog;
    public Button okBtn,nextBtn;
    public int[] images={R.mipmap.devices_img,R.mipmap.info_image,R.mipmap.multi_user,R.mipmap.server_img,R.mipmap.barcode_qr,R.mipmap.graph_img,R.mipmap.notification_img};
    public String[] titles;
    public String[] descriptions;
    boolean firstStart;
    DBHelper_CA dbca;
    int count=0;
    public SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new Session(this);
        initialization();
        perform();
        if (session.loggedIn()){
            startActivity(new Intent(MainActivity.this,SelectionActivity.class));
            finish();
        }
        SharedPreferences setting = getSharedPreferences("PREFS", 0);
        firstStart = setting.getBoolean("first_time_start", true);
        try {
            if (firstStart){
                SharedPreferences.Editor editor =setting.edit();
                editor.putBoolean("first_time_start", false);
                editor.commit();
                info();
            }
        }catch (Exception e){
            Toast.makeText(this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public void initialization() {
        dbca = new DBHelper_CA(this);
        loginId = (EditText) findViewById(R.id.loginId);
        password = (EditText) findViewById(R.id.password);
        info = (Button) findViewById(R.id.info);
        signUp = (Button) findViewById(R.id.signup);
        logIn = (Button) findViewById(R.id.login);
        infoDialog=new Dialog(this);
        infoDialog.setContentView(R.layout.info_dialog);
        infoDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        imgs = (ImageView)infoDialog.findViewById(R.id.imgs);
        title = (TextView)infoDialog.findViewById(R.id.title);
        description = (TextView)infoDialog.findViewById(R.id.description);
        nextBtn= (Button)infoDialog.findViewById(R.id.next);
        progressDialog=new Dialog(this);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        errorDialog=new Dialog(this);
        errorDialog.setContentView(R.layout.error_dialog);
        errorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        okBtn=(Button)errorDialog.findViewById(R.id.okBtn);
        Resources resources =getResources();
        titles=resources.getStringArray(R.array.titles);
        descriptions=resources.getStringArray(R.array.descriptions);
    }

    public void perform()
    {
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internalFunction.net(MainActivity.this)) {
                    if (internalFunction.validate(new EditText[]{loginId, password})) {
//                        DBConnection db = new DBConnection();
//                        db.execute();
                        Cursor c = dbca.getLogin(loginId.getText().toString(),password.getText().toString());
                        if (c.getCount()!=0)
                        {
                            c.moveToNext();
                            sp=getSharedPreferences("COMPANY",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("company_name",c.getString(c.getColumnIndex("company_name")));
                            editor.apply();
                            session.setLoggedIn(true);
                            Intent intent = new Intent(MainActivity.this, SelectionActivity.class);
                            startActivity(intent);
                            finish();
                        }else
                        {
                            Toast.makeText(MainActivity.this, "Incorrect credentials", Toast.LENGTH_LONG).show();
                        }
                    }
                }else {
                    Snackbar.make(findViewById(R.id.loginLay), "No internet", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.cancel();
            }
        });
    }

    public void info()
    {
        infoDialog.show();
        infoDialog.setCanceledOnTouchOutside(false);
        imgs.setImageResource(images[count]);
        title.setText(titles[count]);
        description.setText(descriptions[count]);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count==6)
                {
                    infoDialog.cancel();
                    count=0;
                    nextBtn.setText("Next");
                }else if (count==5)
                {
                    count++;
                    imgs.setImageResource(images[count]);
                    title.setText(titles[count]);
                    description.setText(descriptions[count]);
                    nextBtn.setText("Done");
                }else
                {
                    count++;
                    imgs.setImageResource(images[count]);
                    title.setText(titles[count]);
                    description.setText(descriptions[count]);
                }
            }
        });
    }
}
