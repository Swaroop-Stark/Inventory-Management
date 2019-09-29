package com.example.inventorymanagement;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import cdflynn.android.library.checkview.CheckView;

public class BillingFragment extends Fragment {
    DBHelper_CPS dbcps;
    DBHelper_CPT dbcpt;
    DBHelper_CA dbca;
    DBHelper_CStock dbcst;
    DBHelper_CD dbcd;
    DBHelper_CSALES dbcsales;
    DBHelper_CB dbcb;
    DBHelper_CCB dbccb;
    public CheckView checkView;
    public ImageView back;
    public Dialog progressDialog,errorDialog,customerDialog,optionsDialog,quantityDialog,paymentDetailsDialog,billDialog,requestDialog;
    public TextView textOfList,total,ptotal,billno,btotal,bamtpayed,bdiscount;
    public Spinner unitSpinner;
    public Button okBtn,okBtn2,noBtn,addProBtn,scan,done,billBtn,cancelBtn,qOkBtn,pOkBtn,bOkBtn;
    public RadioButton payed,notPayed;
    public EditText customerName,customerPhoneNumber,discount,qQty,amountPayed;
    public static EditText productNo;
    public ArrayList<String> productNames, productCodes,qty,price;
    public String productname="", task="",loginID="",cname="",pay="",unit="";
    int mrp=0;
    public ListView listView,listView2;
    public ArrayAdapter<String> adapter2;
    MyAdapter adapter;
    public BillingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_billing, container, false);
        initialize(view);
        perform();
        return view;
    }

    public void initialize(View view)
    {
        dbcps = new DBHelper_CPS(getActivity());
        dbcpt = new DBHelper_CPT(getActivity());
        dbca = new DBHelper_CA(getActivity());
        dbcst = new DBHelper_CStock(getActivity());
        dbcd = new DBHelper_CD(getActivity());
        dbcsales = new DBHelper_CSALES(getActivity());
        dbcb = new DBHelper_CB(getActivity());
        dbccb = new DBHelper_CCB(getActivity());
        back = (ImageView)view.findViewById(R.id.back);
        productNo = (EditText)view.findViewById(R.id.productNo);
        total = (TextView)view.findViewById(R.id.total);
        scan = (Button)view.findViewById(R.id.scanBtn);
        addProBtn= (Button)view.findViewById(R.id.addProBtn);
        done = (Button)view.findViewById(R.id.doneBtn);
        listView = (ListView)view.findViewById(R.id.list);
        billDialog = new Dialog(getActivity());
        billDialog.setContentView(R.layout.bill_dialog);
        billDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        billno = (TextView)billDialog.findViewById(R.id.billNo);
        btotal = (TextView)billDialog.findViewById(R.id.total);
        bamtpayed = (TextView)billDialog.findViewById(R.id.amountPayed);
        bdiscount =(TextView)billDialog.findViewById(R.id.discount);
        listView2 = (ListView)billDialog.findViewById(R.id.list);
        bOkBtn = (Button)billDialog.findViewById(R.id.doneBtn);
        progressDialog=new Dialog(getContext());
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        errorDialog=new Dialog(getContext());
        errorDialog.setContentView(R.layout.error_dialog);
        errorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        okBtn=(Button)errorDialog.findViewById(R.id.okBtn);
        customerDialog=new Dialog(getContext());
        customerDialog.setContentView(R.layout.customer_details_dialog);
        customerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        customerName = (EditText)customerDialog.findViewById(R.id.customerName);
        customerPhoneNumber = (EditText)customerDialog.findViewById(R.id.customerNo);
        discount = (EditText)customerDialog.findViewById(R.id.discount);
        billBtn = (Button) customerDialog.findViewById(R.id.billBtn);
        paymentDetailsDialog = new Dialog(getContext());
        paymentDetailsDialog.setContentView(R.layout.payment_details_dialog);
        paymentDetailsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ptotal = (TextView)paymentDetailsDialog.findViewById(R.id.total);
        payed = (RadioButton)paymentDetailsDialog.findViewById(R.id.payed);
        notPayed = (RadioButton)paymentDetailsDialog.findViewById(R.id.notPayed);
        amountPayed = (EditText)paymentDetailsDialog.findViewById(R.id.amount);
        pOkBtn = (Button)paymentDetailsDialog.findViewById(R.id.okBtn);
        optionsDialog=new Dialog(getContext());
        optionsDialog.setContentView(R.layout.option_dialog);
        optionsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        cancelBtn=(Button)optionsDialog.findViewById(R.id.cancelBtn);
        textOfList=(TextView)optionsDialog.findViewById(R.id.textOfList);
        requestDialog=new Dialog(getActivity());
        requestDialog.setContentView(R.layout.request_dialog2);
        requestDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        checkView = (CheckView)requestDialog.findViewById(R.id.checkbox);
        okBtn2=(Button)requestDialog.findViewById(R.id.okBtn);
        noBtn = (Button)requestDialog.findViewById(R.id.noBtn);
        quantityDialog = new Dialog(getContext());
        quantityDialog.setContentView(R.layout.quantity_dialog);
        quantityDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        qQty = (EditText)quantityDialog.findViewById(R.id.quantity);
        unitSpinner = (Spinner)quantityDialog.findViewById(R.id.unit);
        qOkBtn = (Button)quantityDialog.findViewById(R.id.okBtn);
        productNames = new ArrayList<String>(1);
        productCodes = new ArrayList<String>(1);
        qty = new ArrayList<String>(1);
        price = new ArrayList<String>(1);
        adapter = new MyAdapter(getContext(),productNames,qty,price);
        addProBtn.setVisibility(View.GONE);
        amountPayed.setVisibility(View.GONE);
        SharedPreferences sp = getActivity().getSharedPreferences("COMPANY",Context.MODE_PRIVATE);
        cname=sp.getString("company_name","Company");
        Cursor res = dbca.getCompany(cname);
        while (res.moveToNext())
        {
            int index = res.getColumnIndex("login_id");
            this.loginID=res.getString(index);
        }
    }

    public void perform()
    {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            android.Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.CAMERA}, 1);
                    }else {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.CAMERA}, 1);
                    }
                }else {
                    Intent i = new Intent(getActivity(),ScannerActivity.class);
                    i.putExtra("activity","Billing");
                    startActivity(i);
                }
            }
        });

        okBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkView.uncheck();
                requestDialog.cancel();
                BillingFragment fragment = new BillingFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame,fragment);
                fragmentTransaction.commit();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkView.uncheck();
                requestDialog.cancel();
                getFragmentManager().popBackStackImmediate();
            }
        });

        productNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (productNo.getText().length()>0)
                {
                    scan.setVisibility(View.GONE);
                    addProBtn.setVisibility(View.VISIBLE);
                }else {
                    scan.setVisibility(View.VISIBLE);
                    addProBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addProBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbcps.productCodeExistCheck(loginID,productNo.getText().toString())) {
                    Cursor res = dbcps.getProduct(loginID, productNo.getText().toString());
                    while (res.moveToNext()) {
                        int index = res.getColumnIndex("product_name");
                        int index2 = res.getColumnIndex("sp");
                        productname = res.getString(index);
                        mrp = res.getInt(index2);
                    }
                    Cursor res2 = dbcpt.getProductData(loginID,productname);
                    while (res2.moveToNext())
                    {
                        int index = res2.getColumnIndex("unit");
                        unit=res2.getString(index);
                    }
                    if (unit.equals("Item"))
                    {
                        String[] units ={"Item"};
                        adapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,units);
                        unitSpinner.setAdapter(adapter2);
                    }else if (unit.equals("Kilogram")){
                        String[] units ={"Kilogram","Gram"};
                        adapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,units);
                        unitSpinner.setAdapter(adapter2);
                    }else if (unit.equals("Liter")){
                        String[] units ={"Liter","Milliliter"};
                        adapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,units);
                        unitSpinner.setAdapter(adapter2);
                    }
                    quantityDialog.show();
                }else {
                    productNo.setError("Wrong product code");
                }
            }
        });

        qOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qQty.getText().toString().length()>0)
                {
                    int q=0;
                    Cursor res = dbcst.getQuantity(loginID,productname);
                    while (res.moveToNext())
                    {
                        int index = res.getColumnIndex("quantity");
                        q=res.getInt(index);
                    }
                    if (Integer.parseInt(qQty.getText().toString())<=q) {
                        productNames.add(productname);
                        productCodes.add(productNo.getText().toString());
                        qty.add(qQty.getText().toString());
                        int p=0;
                        if (unit.equals("Item")||unit.equals("Kilogram")||unit.equals("Liter"))
                        {
                            p = Integer.parseInt(qQty.getText().toString()) * mrp;
                            price.add("" + p);
                        }else  if (unit.equals("Gram")||unit.equals("Milliliter"))
                        {
                            p = mrp/(1000*Integer.parseInt(qQty.getText().toString()));
                            price.add("" + p);
                        }
                        int t=Integer.parseInt(total.getText().toString())+p;
                        total.setText(""+t);
                        listView.setAdapter(adapter);
                        productNo.getText().clear();
                        qQty.getText().clear();
                        quantityDialog.cancel();
                    }else {
                        if (q==1)
                        {
                            qQty.setError("There is only " + q + " left");
                        }else {
                            qQty.setError("There are only " + q + " left");
                        }
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView item = (TextView)view.findViewById(R.id.productName);
                textOfList.setText(item.getText().toString());
                optionsDialog.show();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productNames.size()>0) {
                    customerDialog.show();
                }else
                {
                    Toast.makeText(getActivity(), "No items added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        billBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!discount.getText().toString().isEmpty())
                {
                    int d = Integer.parseInt(discount.getText().toString());
                    int amt = Integer.parseInt(total.getText().toString());
                    int famt = amt - (amt/d);
                    ptotal.setText(""+famt);
                }else {
                    ptotal.setText(total.getText().toString());
                }
                paymentDetailsDialog.show();
            }
        });

        payed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    amountPayed.setVisibility(View.GONE);
                    pay="";
                }
            }
        });

        notPayed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    amountPayed.setVisibility(View.VISIBLE);
                    pay="x";
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsDialog.cancel();
            }
        });

        pOkBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String DATE= sdf.format(new Date());
                Log.d("Date",DATE);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(DATE, formatter);
                int month = localDate.getMonthValue();
                Log.d("Month",""+month);
                String TOTAL=total.getText().toString(),DISCOUNT=discount.getText().toString();;
                if (!discount.getText().toString().isEmpty())
                {
                    int d = Integer.parseInt(discount.getText().toString());
                    int amt = Integer.parseInt(total.getText().toString());
                    int famt = amt - (amt/d);
                    TOTAL = ""+famt;
                }else {
                    DISCOUNT = "0";
                }
                for (int i=0;i<productNames.size();i++) {
                    int q = 0;
                    dbcb.addBill(loginID,billGenerate(customerPhoneNumber.getText().toString()),productNames.get(i),Integer.parseInt(qty.get(i)),DATE,month);
                    if (!dbcsales.getStock(loginID,productNames.get(i)))
                    {
                        dbcsales.addSales(loginID,billGenerate(customerPhoneNumber.getText().toString()), productNames.get(i),Integer.parseInt(qty.get(i)));
                    }else {
                        dbcsales.updateData(loginID,productNames.get(i),Integer.parseInt(qty.get(i)));
                    }
                    Cursor res = dbcst.getQuantity(loginID, productNames.get(i));
                    while (res.moveToNext()) {
                        int index = res.getColumnIndex("quantity");
                        q = res.getInt(index);
                    }
                    dbcst.updateQuantity(loginID,productNames.get(i),q-Integer.parseInt(qty.get(i)));
                }
                if (pay.equals(""))
                {
                    dbccb.addCustomerBill(loginID,billGenerate(customerPhoneNumber.getText().toString()),customerName.getText().toString(),customerPhoneNumber.getText().toString(),DATE,Integer.parseInt(DISCOUNT),Integer.parseInt(TOTAL),Integer.parseInt(TOTAL),"payed");
                }else{
                    dbccb.addCustomerBill(loginID,billGenerate(customerPhoneNumber.getText().toString()),customerName.getText().toString(),customerPhoneNumber.getText().toString(),DATE,Integer.parseInt(DISCOUNT),Integer.parseInt(TOTAL),Integer.parseInt(amountPayed.getText().toString()),"not payed");
                }
                billno.setText(billGenerate(customerPhoneNumber.getText().toString()));
                listView2.setAdapter(adapter);
                btotal.setText(TOTAL);
                if (pay.equals(""))
                {
                    bamtpayed.setText("Amount payed : " + TOTAL);
                }else {
                    bamtpayed.setText("Amount payed : " + amountPayed.getText().toString());
                }
                bdiscount.setText("Discount : "+DISCOUNT);
                billDialog.show();
            }
        });

        bOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerDialog.cancel();
                paymentDetailsDialog.cancel();
                billDialog.cancel();
                requestDialog.show();
                checkView.check();
            }
        });

    }

    public String billGenerate(String phoneno)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String d = sdf.format(new Date());
        return d+phoneno;
    }

}
