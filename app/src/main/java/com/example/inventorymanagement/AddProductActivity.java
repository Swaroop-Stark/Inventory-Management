package com.example.inventorymanagement;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class AddProductActivity extends AppCompatActivity {

    DBHelper_CA dbac;
    DBHelper_CPT dbcpt;
    DBHelper_CPS dbcps;
    DBHelper_CS dbcs;
    DBHelper_CPC dbcpc;
    DBHelper_CStock dbcst;
    DBHelper_CD dbcd;
    DBHelper_CP dbcp;
    MyAdapter2 adapter3;
    public String loginID="",cname="",task="",work="",pay="";
    public ImageView back,edit,delete;
    public TextView finish,no,textOfList,total;
    public ListView listView,listCode;
    public RelativeLayout lay;
    public Dialog billDialog, addProductDialog,progressDialog,errorDialog,optionsDialog,paymentDetailsDialog,quantityDialog;
    public EditText billNo,cp,mrp,qty,date,amountPayed,qQty;
    public static EditText productNo;
    public RadioButton does,doesnt,payed,notPayed;
    public AutoCompleteTextView supplierName,productName,category;
    public Button addMoreProduct,doneBtn,supplierDropDown,scan,addProBtn,catrgotyDropDown,calenderPopUpBtn,saveNextBtn,finishBtn,okBtn,cancelBtn,pOkBtn,qOkBtn;
    public ArrayAdapter<String> adapter,adapter2,cAdapter,sAdapter,pAdapter;
    public ArrayList<String> productCodes,productNames,qtys,categories,suppliers,products;
    public ArrayList<Integer> price;
    public String[] units={"Item","Kilogram","Liter"};
    public ProgressBar progressBar;
    public Spinner unitSpinner;
    int i=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        initialization();
        perform();
    }

    public void initialization()
    {
        dbac =  new DBHelper_CA(this);
        dbcpc = new DBHelper_CPC(this);
        dbcps= new DBHelper_CPS(this);
        dbcpt = new DBHelper_CPT(this);
        dbcs = new DBHelper_CS(this);
        dbcst = new DBHelper_CStock(this);
        dbcd = new DBHelper_CD(this);
        dbcp = new DBHelper_CP(this);
        back = (ImageView)findViewById(R.id.back);
        listView = (ListView)findViewById(R.id.list);
        finish = (TextView)findViewById(R.id.finish);
        addMoreProduct = (Button)findViewById(R.id.addMoreProducts);
        billDialog = new Dialog(AddProductActivity.this);
        billDialog.setContentView(R.layout.bill_no_dialog);
        billDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        billNo=(EditText)billDialog.findViewById(R.id.billNo);
        supplierName=(AutoCompleteTextView)billDialog.findViewById(R.id.supplier);
        doneBtn=(Button)billDialog.findViewById(R.id.addBtn);
        supplierDropDown=(Button)billDialog.findViewById(R.id.drop2);
        progressDialog=new Dialog(this);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        errorDialog=new Dialog(this);
        errorDialog.setContentView(R.layout.error_dialog);
        errorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        okBtn=(Button)errorDialog.findViewById(R.id.okBtn);
        optionsDialog=new Dialog(this);
        optionsDialog.setContentView(R.layout.option_dialog);
        optionsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        edit=(ImageView)optionsDialog.findViewById(R.id.editBtn);
        delete=(ImageView)optionsDialog.findViewById(R.id.deleteBtn);
        cancelBtn=(Button)optionsDialog.findViewById(R.id.cancelBtn);
        textOfList=(TextView)optionsDialog.findViewById(R.id.textOfList);
        addProductDialog = new Dialog(AddProductActivity.this);
        addProductDialog.setContentView(R.layout.add_products_dialog);
        addProductDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        no=(TextView)addProductDialog.findViewById(R.id.no);
        lay = (RelativeLayout) addProductDialog.findViewById(R.id.lay);
        productNo = (EditText)addProductDialog.findViewById(R.id.productNo);
        cp = (EditText) addProductDialog.findViewById(R.id.cp);
        mrp = (EditText) addProductDialog.findViewById(R.id.mrp);
        qty = (EditText) addProductDialog.findViewById(R.id.quantity);
        date = (EditText)addProductDialog.findViewById(R.id.date);
        productName = (AutoCompleteTextView)addProductDialog.findViewById(R.id.productName);
        category = (AutoCompleteTextView)addProductDialog.findViewById(R.id.category);
        scan= (Button)addProductDialog.findViewById(R.id.scanBtn);
        addProBtn= (Button)addProductDialog.findViewById(R.id.addProBtn);
        catrgotyDropDown = (Button)addProductDialog.findViewById(R.id.drop1);
        calenderPopUpBtn = (Button)addProductDialog.findViewById(R.id.cal);
        does = (RadioButton)addProductDialog.findViewById(R.id.does);
        doesnt = (RadioButton)addProductDialog.findViewById(R.id.doesnt);
        listCode = (ListView)addProductDialog.findViewById(R.id.listCode);
        saveNextBtn = (Button)addProductDialog.findViewById(R.id.save);
        finishBtn = (Button)addProductDialog.findViewById(R.id.finishBtn);
        paymentDetailsDialog = new Dialog(AddProductActivity.this);
        paymentDetailsDialog.setContentView(R.layout.payment_details_dialog);
        paymentDetailsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        total = (TextView)paymentDetailsDialog.findViewById(R.id.total);
        payed = (RadioButton)paymentDetailsDialog.findViewById(R.id.payed);
        notPayed = (RadioButton)paymentDetailsDialog.findViewById(R.id.notPayed);
        amountPayed = (EditText)paymentDetailsDialog.findViewById(R.id.amount);
        pOkBtn = (Button)paymentDetailsDialog.findViewById(R.id.okBtn);
        quantityDialog = new Dialog(AddProductActivity.this);
        quantityDialog.setContentView(R.layout.quantity_dialog);
        quantityDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        qQty = (EditText)quantityDialog.findViewById(R.id.quantity);
        unitSpinner = (Spinner)quantityDialog.findViewById(R.id.unit);
        qOkBtn = (Button)quantityDialog.findViewById(R.id.okBtn);
        productCodes = new ArrayList<String>(1);
        productNames = new ArrayList<String>(1);
        qtys = new ArrayList<String>(1);
        price = new ArrayList<Integer>(1);
        categories = new ArrayList<String>(1);
        suppliers = new ArrayList<String>(1);
        products = new ArrayList<String>(1);
        adapter = new ArrayAdapter<String>(AddProductActivity.this,android.R.layout.simple_list_item_1,productCodes);
        adapter2 = new ArrayAdapter<String>(AddProductActivity.this,android.R.layout.simple_spinner_dropdown_item,units);
        unitSpinner.setAdapter(adapter2);
        addMoreProduct.setText("Add new");
        billDialog.show();
        finish.setVisibility(View.GONE);
        addProBtn.setVisibility(View.GONE);
        amountPayed.setVisibility(View.GONE);
        SharedPreferences sp = getSharedPreferences("COMPANY",Context.MODE_PRIVATE);
        cname=sp.getString("company_name","Company");
        Cursor res = dbac.getCompany(cname);
        while (res.moveToNext())
        {
            int index = res.getColumnIndex("login_id");
            this.loginID=res.getString(index);
        }
        Cursor res2 = dbcpc.getCategories(loginID);
        while (res2.moveToNext())
        {
            int index = res2.getColumnIndex("category");
            categories.add(res2.getString(index));
        }
        cAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,categories);
        category.setAdapter(cAdapter);
        Cursor res3 = dbcs.getSuppliers(loginID);
        while (res3.moveToNext())
        {
            int index = res3.getColumnIndex("supplier_name");
            suppliers.add(res3.getString(index));
        }
        sAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,suppliers);
        supplierName.setAdapter(sAdapter);
        Cursor res4 = dbcst.getProductNames(loginID);
        while (res4.moveToNext())
        {
            int index = res4.getColumnIndex("product_name");
            products.add(res4.getString(index));
        }
        pAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,products);
        productName.setAdapter(pAdapter);
    }
    public void perform()
    {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        supplierDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supplierName.showDropDown();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internalFunction.validate(new EditText[]{billNo},new AutoCompleteTextView[]{supplierName})) {
                    boolean sCorrect = false;
                    for (int i=0;i<suppliers.size();i++)
                    {
                        if (suppliers.get(i).equals(supplierName.getText().toString()))
                        {
                            sCorrect = true;
                        }
                    }
                    if (sCorrect) {
                        billDialog.cancel();
                        addProductDialog.show();
                    }else {
                        supplierName.setError("Unregistered supplier");
                    }
                }
            }
        });

        does.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    if (lay.getHeight() == 0) {
                        Animation animation = new SlideAnim(lay, 0, 80);
                        animation.setInterpolator(new AccelerateInterpolator());
                        animation.setDuration(100);
                        lay.setAnimation(animation);
                        lay.startAnimation(animation);
                    }
                }
            }
        });

        doesnt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    if (lay.getHeight() != 0) {
                        Animation animation = new SlideAnim(lay, 80, 0);
                        animation.setInterpolator(new AccelerateInterpolator());
                        animation.setDuration(100);
                        lay.setAnimation(animation);
                        lay.startAnimation(animation);
                    }
                }
            }
        });

        catrgotyDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.showDropDown();
            }
        });

        calenderPopUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cal=Calendar.getInstance();
                int myear = cal.get(Calendar.YEAR), mmonth = cal.get(Calendar.MONTH), mday = cal.get(Calendar.DATE);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddProductActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        int m=month+1;
                        String mon=""+m,day=""+dayOfMonth;
                        if (mon.length()<2)
                        {
                            mon="0"+mon;
                        }
                        if (day.length()<2)
                        {
                            day="0"+day;
                        }
                        date.setText(year+"-"+mon+"-"+day);
                    }
                }, myear, mmonth, mday);
                datePickerDialog.show();
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddProductActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddProductActivity.this,
                            android.Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(AddProductActivity.this,
                                new String[]{android.Manifest.permission.CAMERA}, 1);
                    }else {
                        ActivityCompat.requestPermissions(AddProductActivity.this,
                                new String[]{android.Manifest.permission.CAMERA}, 1);
                    }
                }else {
                    Intent i = new Intent(AddProductActivity.this, ScannerActivity.class);
                    i.putExtra("activity", "AddProduct");
                    startActivity(i);
                }
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
                    if (!dbcps.getProductCode(loginID,productNo.getText().toString()))
                    {
                        scan.setVisibility(View.GONE);
                        addProBtn.setVisibility(View.VISIBLE);
                    }else {
                        productNo.setError("Already exists");
                    }
                }else {
                    scan.setVisibility(View.VISIBLE);
                    addProBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    quantityDialog.show();
                }
            }
        });

        qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityDialog.show();
            }
        });

        qOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qty.setText(qQty.getText().toString());
                quantityDialog.cancel();
            }
        });

        addProBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productCodes.add(productNo.getText().toString());
                price.add(Integer.parseInt(cp.getText().toString()));
                listCode.setAdapter(adapter);
                productNo.getText().clear();
            }
        });

        saveNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internalFunction.validate(new EditText[]{cp,mrp,qty},new AutoCompleteTextView[]{productName,category})) {
                    boolean cCorrect = false;
                    for (int i=0;i<categories.size();i++)
                    {
                        if (categories.get(i).equals(category.getText().toString()))
                        {
                            cCorrect = true;
                        }
                    }
                    if (cCorrect) {
                        addData("saveNextBtn");
//                        DBConnection db = new DBConnection();
//                        db.execute();
                    }else {
                        category.setError("Unregistered");
                    }
                }
            }
        });
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internalFunction.validate(new EditText[]{cp,mrp,qty},new AutoCompleteTextView[]{productName,category})) {
                    work="products";
                    addData("finishBtn");
//                    DBConnection db = new DBConnection();
//                    db.execute();
                }
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

        pOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pay.equals("")) {
                    dbcp.addSupplierBill(loginID,billNo.getText().toString(),supplierName.getText().toString(),Integer.parseInt(total.getText().toString()),Integer.parseInt(total.getText().toString()),"payed");
                }else if (pay.equals("x"))
                {
                    dbcp.addSupplierBill(loginID,billNo.getText().toString(),supplierName.getText().toString(),Integer.parseInt(total.getText().toString()),Integer.parseInt(amountPayed.getText().toString()),"not payed");
                }
                finish();
            }
        });

        addMoreProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i>1) {
                    addProductDialog.show();
                }else {
                    billDialog.show();
                }
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int t=0;
                for (int i=0;i<price.size();i++)
                {
                    t=t+price.get(i);
                }
                total.setText(""+t);
                paymentDetailsDialog.show();
            }
        });
    }

    public void addData(String task)
    {
        if (!dbcst.productExist(loginID, productName.getText().toString())) {
            dbcst.addProduct(loginID, productName.getText().toString(),Integer.parseInt(qQty.getText().toString()));
        }else {
            Cursor cursor = dbcst.getQuantity(loginID,productName.getText().toString());
            int quantity=0;
            while (cursor.moveToNext())
            {
                int index = cursor.getColumnIndex("quantity");
                quantity=cursor.getInt(index);
            }
            dbcst.updateQuantity(loginID,productName.getText().toString(),quantity+Integer.parseInt(qQty.getText().toString()));
        }
        if (date.getText().toString() == null || date.getText().toString().equals("")) {
            dbcpt.addData(loginID, billNo.getText().toString(), supplierName.getText().toString(), productName.getText().toString(), category.getText().toString(), Integer.parseInt(qQty.getText().toString()), unitSpinner.getSelectedItem().toString(), "No Expiry");
        } else {
            dbcpt.addData(loginID, billNo.getText().toString(), supplierName.getText().toString(), productName.getText().toString(), category.getText().toString(), Integer.parseInt(qQty.getText().toString()), unitSpinner.getSelectedItem().toString(), date.getText().toString());
        }
        for (int i = 0; i < productCodes.size(); i++) {
            dbcps.addProducts(loginID, billNo.getText().toString(), productCodes.get(i), productName.getText().toString(), Integer.parseInt(cp.getText().toString()), Integer.parseInt(mrp.getText().toString()));
        }

        if (task.equals("saveNextBtn")) {
            addProductDialog.cancel();
            i++;
            addMoreProduct.setText("Add more");
            finish.setVisibility(View.VISIBLE);
            no.setText("" + i);
            addProductDialog.show();
        } else if (task.equals("finishBtn")) {
            i++;
            addMoreProduct.setText("Add more");
            finish.setVisibility(View.VISIBLE);
            no.setText("" + i);
            addProductDialog.cancel();
        }
        Cursor result = dbcpt.getProductData(loginID, productName.getText().toString());
        if (result != null && result.getCount() > 0) {
            while (result.moveToNext()) {
                int index1 = result.getColumnIndex("product_name");
                int index2 = result.getColumnIndex("quantity");
                productNames.add(result.getString(index1));
                qtys.add(result.getString(index2));
            }
        }
        adapter3 = new MyAdapter2(AddProductActivity.this, productNames, qtys);
        listView.setAdapter(adapter3);
        productCodes.clear();
        does.isChecked();
        date.getText().clear();
        qQty.getText().clear();
        qty.getText().clear();
        mrp.getText().clear();
        cp.getText().clear();
        category.getText().clear();
        productName.getText().clear();
    }

    public class SlideAnim extends Animation {
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

    @Override
    public void onBackPressed() {
        if (productNames.isEmpty()) {
            super.onBackPressed();
        }else {
            int t=0;
            for (int i=0;i<price.size();i++)
            {
                t=t+price.get(i);
            }
            total.setText(""+t);
            paymentDetailsDialog.show();
        }
    }
}
