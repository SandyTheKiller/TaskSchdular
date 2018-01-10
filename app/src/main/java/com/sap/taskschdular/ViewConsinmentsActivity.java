package com.sap.taskschdular;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.sap.taskschdular.Adapter.CustomAdapter;
import com.sap.taskschdular.Adapter.CustomAdapterViewAll;
import com.sap.taskschdular.Model.DataModel;
import com.sap.taskschdular.Session.Session;
import com.sap.taskschdular.Session.SessionConstents;
import com.sap.taskschdular.constants.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ViewConsinmentsActivity extends Activity implements View.OnClickListener {

    private ListView mListView;
    private TextView txtUser, txtZone, txtLogout;
    private AQuery mAQuery;
    private EditText editsearch;
    private CustomAdapterViewAll adapterReportData=null;
    private ArrayList<DataModel> dataModelArrayList;
    private DataModel mDataModel;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_consignment_layout);
        txtUser = (TextView) findViewById(R.id.txt_user);
        txtZone = (TextView) findViewById(R.id.txt_zone);
        mListView = (ListView) findViewById(R.id.lv_list);
        txtLogout = (TextView) findViewById(R.id.txt_logout);
        editsearch=(EditText)findViewById(R.id.edt_search);
        mSession = Session.getInstance();
        mAQuery = new AQuery(this);
        dataModelArrayList = new ArrayList<>();
        setListner();
        txtUser.setText(mSession.getValue(this, SessionConstents.KEY_FIRST_NAME));
        txtZone.setText(mSession.getValue(this, SessionConstents.KEY_ZONE_NAME));
        WScallPendingList();

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
        mAQuery.progress(mProgress).ajax(Constants.BASE_URL+Constants.VIEW_All_URL, mParams, JSONObject.class, new AjaxCallback<JSONObject>() {
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
                                mDataModel.setEndTime(mJSONObj.getString("time_out"));
                                mDataModel.setRate(mJSONObj.getString("rate")+"Rs");
                                if(mJSONObj.getString("time_out").equals(null) || mJSONObj.getString("time_out").equals("null"))
                                {
                                    mDataModel.setEndTime(" ");
                                }
                                else
                                {
                                    mDataModel.setEndTime(mJSONObj.getString("time_out"));
                                }
                                dataModelArrayList.add(mDataModel);
                            }
                            adapterReportData = new CustomAdapterViewAll(dataModelArrayList, ViewConsinmentsActivity.this);
                            mListView.setAdapter(adapterReportData);
                        }
                        if (object.getString("errorCode").equalsIgnoreCase("3")) {
                            mSession.sessionLogout(ViewConsinmentsActivity.this);
                            Toast.makeText(getApplication(),object.getString("msg"),Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(ViewConsinmentsActivity.this,Activity_Login.class);
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
    private void setListner() {
        txtLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txt_logout:
                mSession.sessionLogout(ViewConsinmentsActivity.this);
                Intent it = new Intent(ViewConsinmentsActivity.this, Activity_Login.class);
                startActivity(it);
                finish();
                break;
        }
    }
}
