package com.example.inventorymanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class threeParamAdapter extends BaseAdapter implements Filterable {
    public Context context;
    public ArrayList<threeParam> params,orgs;

    public threeParamAdapter(Context context, ArrayList<threeParam> params)
    {
        super();
        this.context = context;
        this.params = params;
    }

    public class threeParamHolder
    {

        TextView productName,quantity,mrp;
    }



    @Override
    public int getCount() {
        return params.size();
    }

    @Override
    public Object getItem(int position) {
        return params.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        threeParamHolder holder= null;
        if (convertView==null)
        {
            convertView= LayoutInflater.from(context).inflate(R.layout.bill_list_layout,parent,false);
            holder = new threeParamHolder();
            holder.productName = (TextView)convertView.findViewById(R.id.productName);
            holder.quantity = (TextView)convertView.findViewById(R.id.quantity);
            holder.mrp = (TextView)convertView.findViewById(R.id.mrp);
            convertView.setTag(holder);
        }else {
            holder=(threeParamHolder)convertView.getTag();
        }
        holder.productName.setText(params.get(position).getParam1());
        holder.quantity.setText(params.get(position).getParam2());
        holder.mrp.setText(params.get(position).getParam3());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oResults = new FilterResults();
                final ArrayList<threeParam> result = new ArrayList<threeParam>(1);
                if (orgs == null)
                {
                    orgs = params;
                }
                if (constraint!=null)
                {
                    if (orgs!= null&& orgs.size()>0)
                    {
                        for (final threeParam g: orgs)
                        {
                            if (g.getParam1().toLowerCase().contains(constraint.toString()))result.add(g);
                        }
                    }
                    oResults.values = result;
                }
                return oResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                params = (ArrayList<threeParam>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
