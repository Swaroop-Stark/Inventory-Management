package com.example.inventorymanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;

class MyAdapter2 extends ArrayAdapter implements Filterable{
    public ArrayList<String> title,location;
    Context context;
    public MyAdapter2(Context context, ArrayList<String> title1, ArrayList<String> location){
        super(context,R.layout.add_products_dialog, R.id.no, title1);
        this.title=title1;
        this.location=location;
        this.context=context;
    }
    public View getView(int position, final View convertView, ViewGroup parent){
        LayoutInflater inflater =(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.storage_list_layout, parent, false);
        TextView productName=(TextView)row.findViewById(R.id.productName);
        TextView locate=(TextView)row.findViewById(R.id.location);
        productName.setText(title.get(position));
        locate.setText(location.get(position));
        return row;
    }

    @Override
    public Filter getFilter() {
        return super.getFilter();
    }
}