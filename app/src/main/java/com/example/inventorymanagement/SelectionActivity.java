package com.example.inventorymanagement;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SelectionActivity extends AppCompatActivity {
    public Dialog exit_dialog,noInternetDialog;
    public Button yesBtn, noBtn,retry;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment selectedFragment = null;
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.navigation_dashboard:
                    selectedFragment = new DashboardFragment();
                    break;
                case R.id.setting:
                    selectedFragment = new SettingsFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame,selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        initialization();
        net();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new HomeFragment()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        net();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount()>0)
        {
            getSupportFragmentManager().popBackStackImmediate();
        }else {
            exit_dialog.show();
        }
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit_dialog.cancel();
            }
        });
    }

    public void initialization()
    {
        exit_dialog = new Dialog(this);
        exit_dialog.setContentView(R.layout.exit_dialog);
        exit_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        yesBtn = (Button)exit_dialog.findViewById(R.id.yesbtn);
        noBtn = (Button)exit_dialog.findViewById(R.id.nobtn);
        noInternetDialog = new Dialog(this);
        noInternetDialog.setContentView(R.layout.no_internet_dialog);
        noInternetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        retry = (Button)noInternetDialog.findViewById(R.id.retry);
    }

    public void net()
    {
        if (internalFunction.net(SelectionActivity.this)){
            noInternetDialog.setCanceledOnTouchOutside(true);
            noInternetDialog.setCancelable(true);
            noInternetDialog.cancel();
        }else
        {
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            noInternetDialog.setCanceledOnTouchOutside(false);
            noInternetDialog.setCancelable(false);
            noInternetDialog.show();
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    net();
                }
            });
        }
    }
}
