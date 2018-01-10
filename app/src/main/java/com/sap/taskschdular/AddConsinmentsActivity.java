package com.sap.taskschdular;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.sap.taskschdular.Adapter.CustomAdapter;
import com.sap.taskschdular.Model.DataModel;
import com.sap.taskschdular.Session.Session;
import com.sap.taskschdular.Session.SessionConstents;
import com.sap.taskschdular.constants.Constants;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddConsinmentsActivity extends Activity implements View.OnClickListener {

    private ListView mListView;
    private EditText edtConsinment, edtMobileNo, editsearch;
    private TextView txtUser, txtZone, btnAdd, txtStartTime, txtEndTime, txtLogout,txtRate;
    private LinearLayout ll_add_data;
    private AQuery mAQuery;
    private Animation shakeAnimation;
    private Button btnStart, btnEnd, btnSms;
    private CustomAdapter adapterReportData = null;
    private ArrayList<DataModel> dataModelArrayList;
    private DataModel mDataModel;
    private Session mSession;
    private String mDateTime = "";
    private Animation animationUp, animationDown;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private Spinner spinner;
    //final Spinner spinner;
    String phoneNo;
    String message;
    private String rate_type="rate1";
    private NetworkConnection connection;
    private View mView;
    //MaterialBetterSpinner materialBetterSpinner;
private ArrayAdapter<String> adapter;
    String[] SPINNER_DATA = {"2W", "4W"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_consinment);
        txtUser = (TextView) findViewById(R.id.txt_user);
        txtZone = (TextView) findViewById(R.id.txt_zone);
        edtConsinment = (EditText) findViewById(R.id.edt_consin_no);
        edtMobileNo = (EditText) findViewById(R.id.edt_mobile_no);
        editsearch = (EditText) findViewById(R.id.edt_search);
        txtStartTime = (TextView) findViewById(R.id.edt_start_time);
        txtEndTime = (TextView) findViewById(R.id.edt_end_time);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnEnd = (Button) findViewById(R.id.btn_end);
        btnSms = (Button) findViewById(R.id.btn_send_sms);
        btnAdd = (TextView) findViewById(R.id.txt_add);
        mListView = (ListView) findViewById(R.id.lv_list);
        ll_add_data = (LinearLayout) findViewById(R.id.ll_add_data);
        txtLogout = (TextView) findViewById(R.id.txt_logout);
        txtRate=(TextView)findViewById(R.id.txt_rate);
        mView = findViewById(R.id.ll_add_data);

        connection = new NetworkConnection(this);
        //  edtConsinment.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

         spinner = (Spinner) findViewById(R.id.spinner);

        String[] rate = new String[]{
                "2W", "4W"
        };

        final List<String> plantsList = new ArrayList<>(Arrays.asList(rate));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,plantsList);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
        InputFilter[] editFilters = edtConsinment.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        edtConsinment.setFilters(newFilters);

        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        mSession = Session.getInstance();
        mAQuery = new AQuery(this);
        dataModelArrayList = new ArrayList<>();
        setListner();
        ll_add_data.setVisibility(View.GONE);
        txtUser.setText(mSession.getValue(this, SessionConstents.KEY_FIRST_NAME));
        txtZone.setText(mSession.getValue(this, SessionConstents.KEY_ZONE_NAME));
        txtRate.setText("Rate: "+mSession.getValue(this,SessionConstents.KEY_RATE1));
        animationUp = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom);
        animationUp.setDuration(400);
        animationDown = AnimationUtils.loadAnimation(this, R.anim.down_from_top);
        animationDown.setDuration(400);


        ///materialBetterSpinner = (MaterialBetterSpinner) findViewById(R.id.material_spinner1);

       /* adapter = new ArrayAdapter<String>(AddConsinmentsActivity.this, android.R.layout.simple_dropdown_item_1line, SPINNER_DATA);

        materialBetterSpinner.setAdapter(adapter);*/
        /*spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(spinner.getText().toString().equals("2W")){
                    txtRate.setText("Rate per Hour: "+mSession.getValue(AddConsinmentsActivity.this,SessionConstents.KEY_RATE1));
                }
                else if(materialBetterSpinner.getText().toString().equals("4W"))
                {
                    txtRate.setText("Rate per Hour: "+mSession.getValue(AddConsinmentsActivity.this,SessionConstents.KEY_RATE2));
                }
                else{
                    txtRate.setText("");
                }
            }
        });*/
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    rate_type="rate1";
                    txtRate.setText("Rate: "+mSession.getValue(AddConsinmentsActivity.this,SessionConstents.KEY_RATE1));
                }
                else if(position==1)
                {
                    rate_type="rate2";
                    txtRate.setText("Rate: "+mSession.getValue(AddConsinmentsActivity.this,SessionConstents.KEY_RATE2));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /*spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    txtRate.setText("Rate per Hour: "+mSession.getValue(AddConsinmentsActivity.this,SessionConstents.KEY_RATE1));
                }
                else if(position==1)
                {
                    txtRate.setText("Rate per Hour: "+mSession.getValue(AddConsinmentsActivity.this,SessionConstents.KEY_RATE2));
                }
            }
        });*/

        WScallPendingList();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btnStart.setVisibility(View.GONE);
                if(!btnEnd.isShown())
                {
                    btnEnd.setVisibility(View.VISIBLE);
                }

                if(!ll_add_data.isShown())
                {
                    ll_add_data.setAnimation(animationDown);
                    animationUp.start();
                    ll_add_data.setVisibility(View.VISIBLE);
                }

                edtConsinment.setFocusable(false);
                edtConsinment.setFocusableInTouchMode(false);
                edtConsinment.setClickable(false);
                edtMobileNo.setFocusable(false);
                edtMobileNo.setFocusableInTouchMode(false);
                edtMobileNo.setClickable(false);

                mDataModel = new DataModel();
                mDataModel = dataModelArrayList.get(position);
                String rt=mDataModel.getRateType();
                //adapter = new ArrayAdapter<String>(AddConsinmentsActivity.this, android.R.layout.simple_dropdown_item_1line, SPINNER_DATA);

                //materialBetterSpinner.setAdapter(adapter);
                if(rt.equals("rate1"))
                {
                    spinner.setSelection(0);
                    txtRate.setText("Rate: "+mDataModel.getRate());
                   // materialBetterSpinner.setSelection(adapter.getPosition("2W"));
                    //materialBetterSpinner.setText("2W");
                }
                else
                {
                    spinner.setSelection(1);
                    txtRate.setText("Rate: "+mDataModel.getRate());
                    //materialBetterSpinner.setText("4W");
                }
                spinner.setClickable(false);
                spinner.setEnabled(false);
                edtConsinment.setText(mDataModel.getConsinmentNo());
                edtMobileNo.setText(mDataModel.getMobileNo());
                txtStartTime.setText(mDataModel.getStartTime());
                //materialBetterSpinner.setText();
            }
        });

        editsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if (dataModelArrayList.size()>0) {
                    String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                    adapterReportData.filter(text);
                } else {
                    String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                    adapterReportData.filter(text);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void WScallPendingList() {
        ProgressDialog mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading...");
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        HashMap<String, String> mParams = new HashMap<>();
        mParams.put("user_id", mSession.getValue(this, SessionConstents.KEY_USER_ID));
        mParams.put("sessionTokenKey", mSession.getValue(this, SessionConstents.KEY_SESSION_TOKEN));
        dataModelArrayList.clear();
        //mParams.put("user_id", Session.getInstance().getValue(GroupNearMeActivity.this, SessionConstents.KEY_USER_ID));
        mAQuery.progress(mProgress).ajax(Constants.BASE_URL+Constants.CONSIGNMENT_LIST_URL, mParams, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                super.callback(url, object, status);
                if (object != null) {
                    try {
                        if (object.getString("errorCode").equalsIgnoreCase("0")) {
                            JSONArray mArrJSON = object.getJSONArray("data");
                            for (int i = 0; i < mArrJSON.length(); i++) {
                                JSONObject mJSONObj = mArrJSON.getJSONObject(i);
                                mDataModel = new DataModel();
                                mDataModel.setConsinmentNo(mJSONObj.getString("consign_no"));
                                mDataModel.setMobileNo(mJSONObj.getString("mobile_no"));
                                mDataModel.setStartTime(mJSONObj.getString("time_in"));
                                mDataModel.setRate(mJSONObj.getString("rate")+"Rs");
                                mDataModel.setRateType(mJSONObj.getString("rate_type"));
                                dataModelArrayList.add(mDataModel);

                            }
                            adapterReportData = new CustomAdapter(dataModelArrayList, AddConsinmentsActivity.this);
                            mListView.setAdapter(adapterReportData);
                        }
                        else
                        if (object.getString("errorCode").equalsIgnoreCase("3")) {
                            mSession.sessionLogout(AddConsinmentsActivity.this);
                            Toast.makeText(getApplication(),object.getString("msg"),Toast.LENGTH_LONG).show();
                            /*Intent intent=new Intent(AddConsinmentsActivity.this,Activity_Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);*/
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Toast.makeText(getApplication(),status.getCode()+"",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setListner() {
        btnEnd.setOnClickListener(this);
        btnSms.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        txtLogout.setOnClickListener(this);
        btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_start:
                //ll_add_data.setVisibility(View.GONE);
                if (checkValidation()) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());
                    mDateTime = formattedDate;
                    txtStartTime.setText(formattedDate);
                    btnStart.setVisibility(View.VISIBLE);
                    btnEnd.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    callStartWebservice();
                }
                break;
            case R.id.btn_end:
                //ll_add_data.setVisibility(View.GONE);
                if (checkValidation()) {
                    Calendar c1 = Calendar.getInstance();
                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate1 = df1.format(c1.getTime());
                    mDateTime = formattedDate1;
                    txtEndTime.setText(formattedDate1);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    callEndWebservice();
                }
                break;
            case R.id.txt_add:
                txtClear();
                edtConsinment.setFocusable(true);
                edtConsinment.setFocusableInTouchMode(true);
                edtConsinment.setClickable(true);
                edtMobileNo.setFocusable(true);
                edtMobileNo.setFocusableInTouchMode(true);
                edtMobileNo.setClickable(true);
                spinner.setClickable(true);
                spinner.setEnabled(true);
                btnStart.setVisibility(View.VISIBLE);
                btnEnd.setVisibility(View.GONE);
                if(!ll_add_data.isShown())
                {
                    ll_add_data.setAnimation(animationDown);
                    animationUp.start();
                    ll_add_data.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.txt_logout:
                callLogOutWebservice();
                /*mSession.sessionLogout(AddConsinmentsActivity.this);
                Intent it = new Intent(AddConsinmentsActivity.this, Activity_Login.class);
                startActivity(it);
                finish();*/
                break;
            case R.id.btn_send_sms:
                Intent intent = new Intent(AddConsinmentsActivity.this, ViewConsinmentsActivity.class);
                startActivity(intent);
                break;
        }
    }

    protected void sendSMSMessage(String _phoneNo, String _message) {
        phoneNo = _phoneNo;
        message = _message;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, message, null, null);
                Toast.makeText(getApplicationContext(), "SMS sent.",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    private void callStartWebservice() {
        ProgressDialog mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading...");
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        HashMap<String, String> mParams = new HashMap<>();
        mParams.put("user_id", mSession.getValue(this, SessionConstents.KEY_USER_ID));
        mParams.put("consign_no", edtConsinment.getText().toString());
        mParams.put("mobile_no", edtMobileNo.getText().toString());
        mParams.put("start_time", mDateTime);
        mParams.put("rate_type", rate_type);
        mParams.put("zone_id", mSession.getValue(this, SessionConstents.KEY_ZONE_ID));
        mParams.put("sessionTokenKey", mSession.getValue(this, SessionConstents.KEY_SESSION_TOKEN));
        //mParams.put("user_id", Session.getInstance().getValue(GroupNearMeActivity.this, SessionConstents.KEY_USER_ID));
        mAQuery.progress(mProgress).ajax(Constants.BASE_URL+Constants.START_CONSINMENT_URL, mParams, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                super.callback(url, object, status);
                if (object != null) {
                    try {
                        if (object.getString("errorCode").equalsIgnoreCase("0")) {
                            String msg = object.getString("message");
                            Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show();
                            ll_add_data.setVisibility(View.GONE);
                            WScallPendingList();
                        }
                        else
                        if (object.getString("errorCode").equalsIgnoreCase("3")) {
                            mSession.sessionLogout(AddConsinmentsActivity.this);
                            Toast.makeText(getApplication(),object.getString("msg")+" login Again",Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(AddConsinmentsActivity.this,Activity_Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Toast.makeText(getApplication(),status.getCode()+"",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void callEndWebservice() {
        ProgressDialog mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading...");
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        HashMap<String, String> mParams = new HashMap<>();
        mParams.put("user_id", mSession.getValue(this, SessionConstents.KEY_USER_ID));
        mParams.put("consign_no", edtConsinment.getText().toString());
        mParams.put("mobile_no", edtMobileNo.getText().toString());
        mParams.put("sessionTokenKey", mSession.getValue(AddConsinmentsActivity.this, SessionConstents.KEY_SESSION_TOKEN));
        mParams.put("out_time", mDateTime);
        mAQuery.progress(mProgress).ajax(Constants.BASE_URL+Constants.END_CONSIGNMENT_URL, mParams, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                super.callback(url, object, status);
                if (object != null) {
                    try {
                        if (object.getString("errorCode").equalsIgnoreCase("0")) {
                            String msg = object.getString("message");
                            showCustomDialog(msg);
                            ll_add_data.setAnimation(animationUp);
                            animationUp.start();
                            ll_add_data.setVisibility(LinearLayout.GONE);
                            dataModelArrayList.clear();
                            sendSMSMessage(edtMobileNo.getText().toString(), msg);
                            txtClear();
                            WScallPendingList();
                        } else if (object.getString("errorCode").equalsIgnoreCase("2")) {
                            WScallPendingList();
                        }
                        else
                        if (object.getString("errorCode").equalsIgnoreCase("3")) {
                            mSession.sessionLogout(AddConsinmentsActivity.this);
                            Toast.makeText(getApplication(),object.getString("msg")+" login Again",Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(AddConsinmentsActivity.this,Activity_Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Toast.makeText(getApplication(),status.getCode()+"",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void callLogOutWebservice() {
        ProgressDialog mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading...");
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        HashMap<String, String> mParams = new HashMap<>();
        mParams.put("user_id", mSession.getValue(this, SessionConstents.KEY_USER_ID));
        mParams.put("sessionTokenKey", mSession.getValue(this, SessionConstents.KEY_SESSION_TOKEN));
        //mParams.put("user_id", Session.getInstance().getValue(GroupNearMeActivity.this, SessionConstents.KEY_USER_ID));
        mAQuery.progress(mProgress).ajax(Constants.BASE_URL+Constants.LOGOUT_URL, mParams, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                super.callback(url, object, status);
                if (object != null) {
                    try {
                        if (object.getString("errorCode").equalsIgnoreCase("0")) {
                            mSession.sessionLogout(AddConsinmentsActivity.this);
                            Intent it = new Intent(AddConsinmentsActivity.this, Activity_Login.class);
                            startActivity(it);
                            finish();
                        }
                        if (object.getString("errorCode").equalsIgnoreCase("1")) {
                            mSession.sessionLogout(AddConsinmentsActivity.this);
                            Intent it = new Intent(AddConsinmentsActivity.this, Activity_Login.class);
                            startActivity(it);
                            finish();
                        }
                        if (object.getString("errorCode").equalsIgnoreCase("2")) {
                            mSession.sessionLogout(AddConsinmentsActivity.this);
                            Intent it = new Intent(AddConsinmentsActivity.this, Activity_Login.class);
                            startActivity(it);
                            finish();
                        }
                        if (object.getString("errorCode").equalsIgnoreCase("3")) {
                            mSession.sessionLogout(AddConsinmentsActivity.this);
                            Intent it = new Intent(AddConsinmentsActivity.this, Activity_Login.class);
                            startActivity(it);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Toast.makeText(getApplication(),status.getCode()+"",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void txtClear() {
        txtStartTime.setText("");
        txtEndTime.setText("");
        edtMobileNo.setText("");
        edtConsinment.setText("");
        edtConsinment.requestFocus();
    }

    private boolean checkValidation() {

        String consinmentNo = edtConsinment.getText().toString().trim();
        String vehicleNo = "^[A-Z]{2}[ -][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*)? [0-9]{4}$";
        if (!consinmentNo.matches(vehicleNo) && consinmentNo.length() < 10) {
            edtConsinment.setError("10 valid character required.");
            edtConsinment.requestFocus();
            edtConsinment.startAnimation(shakeAnimation);
            return false;
        }
        /*if (consinmentNo.length() < 10) {
            edtConsinment.setError("10 character required.");
            edtConsinment.requestFocus();
            edtConsinment.startAnimation(shakeAnimation);
            return false;
        }*/
        else if (edtConsinment.getText().length() < 1) {
            edtConsinment.setError("Please enter Vehicle Number.");
            edtConsinment.requestFocus();
            edtConsinment.startAnimation(shakeAnimation);
            return false;
        } else if (edtMobileNo.getText().length() < 10) {
            edtMobileNo.setError("Please enter Valid mobile number.");
            edtMobileNo.requestFocus();
            edtMobileNo.startAnimation(shakeAnimation);
            return false;
        } else if(!connection.isConnectingToInternet())
        {
            new CustomToast().Show_Toast(AddConsinmentsActivity.this, mView,
                    "No internet connectivity.");
            return false;
        }
        /*else if (spinner.getSelectedItem().toString().length() < 1) {
            txtRate.setError("Select Rate Type");
            spinner.startAnimation(shakeAnimation);
            return false;
        }*/
        return true;
    }

    private void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message");
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showCustomDialog(String msg) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompts, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(promptsView);
        final TextView txtMsg = (TextView) promptsView
                .findViewById(R.id.txt_msg);
        txtMsg.setText(msg);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to exit the application?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
