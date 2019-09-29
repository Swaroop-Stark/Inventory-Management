package com.example.inventorymanagement;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

class MyAdapter extends ArrayAdapter{
    public ArrayList<String> title,qty,price;
    Context context;
    public MyAdapter(Context context, ArrayList<String> title1, ArrayList<String> qty, ArrayList<String> price){
        super(context,R.layout.bill_list_layout, R.id.no, title1);
        this.title=title1;
        this.qty=qty;
        this.price=price;
        this.context=context;
    }
    public View getView(int position, final View convertView, ViewGroup parent){
        LayoutInflater inflater =(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.bill_list_layout, parent, false);
        TextView productName=(TextView)row.findViewById(R.id.productName);
        TextView quantity=(TextView)row.findViewById(R.id.quantity);
        TextView mrp=(TextView)row.findViewById(R.id.mrp);
        productName.setText(title.get(position));
        quantity.setText(qty.get(position));
        mrp.setText(price.get(position));
        return row;
    }
}