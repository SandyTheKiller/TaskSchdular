package com.sap.taskschdular;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.sap.taskschdular.Session.Session;
import com.sap.taskschdular.Session.SessionConstents;
import com.sap.taskschdular.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Activity_Login extends Activity implements OnClickListener {

    private EditText userId, password;
    private Button loginButton;
    private CheckBox show_hide_password;
    private LinearLayout loginLayout;
    private Animation shakeAnimation;
    private View mView;
    private Session mSession;
    /*private Session mSession;*/
    private AQuery mAQuery;
    public Activity_Login() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initViews();
        setListeners();
    }

    // Initiate Views
    private void initViews() {
/*        mSession = Session.getInstance();*/
        mAQuery = new AQuery(this);
        mSession = Session.getInstance();
        userId = (EditText) findViewById(R.id.login_user_id);
        password = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.loginBtn);
        show_hide_password = (CheckBox) findViewById(R.id.show_hide_password);
        loginLayout = (LinearLayout) findViewById(R.id.login_layout);
        mView = findViewById(R.id.layout_root);
        shakeAnimation = AnimationUtils.loadAnimation(this,
                R.anim.shake);
        // Load ShakeAnimation
    }
    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
        show_hide_password
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {
                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {

                            show_hide_password.setText(R.string.hide_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                            password.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            password.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
               /* Intent it = new Intent(Activity_Login.this, AddConsinmentsActivity.class);
                startActivity(it);
                finish();*/
                if (checkValidation()) {
                    callLoginWebservice();
                }
                break;
        }
    }

    private void callLoginWebservice() {

        ProgressDialog mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading...");
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Map<String, String> params = new HashMap<>();
        params.put("username", userId.getText().toString());
        params.put("password", password.getText().toString());
        mAQuery.progress(mProgress).ajax(Constants.BASE_URL+Constants.LOGIN_URL, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                int code = status.getCode();
                if (json != null) {
                    try {
                        String mErrorCode = json.getString("errorCode");
                        String mMessage = json.getString("message");
                        Log.d("Login",json.toString());
                        if (mErrorCode.equals("0")) {
                           JSONArray jsonArray=json.getJSONArray("data");
                            JSONObject jsonData = jsonArray.getJSONObject(0);
                            mSession.updateValue(Activity_Login.this, SessionConstents.KEY_SESSION_TOKEN, json.getString("sessionTokenKey"));
                            mSession.updateValue(Activity_Login.this, SessionConstents.KEY_USER_ID, jsonData.getString("user_id"));
                            mSession.updateValue(Activity_Login.this, SessionConstents.KEY_FIRST_NAME, jsonData.getString("first_name"));
                            mSession.updateValue(Activity_Login.this, SessionConstents.KEY_LAST_NAME, jsonData.getString("last_name"));
                            mSession.updateValue(Activity_Login.this, SessionConstents.KEY_ZONE_ID, jsonData.getString("zone_id"));
                            mSession.updateValue(Activity_Login.this, SessionConstents.KEY_RATE1, jsonData.getString("rate1"));
                            mSession.updateValue(Activity_Login.this, SessionConstents.KEY_RATE2, jsonData.getString("rate2"));
                            String zone=(!jsonData.isNull("area_name")) ? jsonData.getString("area_name") : "Not Assigned";
                            mSession.updateValue(Activity_Login.this, SessionConstents.KEY_ZONE_NAME, zone);
                            Toast.makeText(getApplicationContext(), mMessage, Toast.LENGTH_LONG).show();
                            Intent it = new Intent(Activity_Login.this, AddConsinmentsActivity.class);
                            startActivity(it);
                            finish();
                        } else if (mErrorCode.equals("1")) {
                            Toast.makeText(getApplicationContext(), mMessage, Toast.LENGTH_LONG).show();
                        } else if (mErrorCode.equals("2")) {
                            Toast.makeText(getApplicationContext(), mMessage, Toast.LENGTH_LONG).show();
                        } else if (mErrorCode.equals("3")) {
                            Toast.makeText(getApplicationContext(), mMessage, Toast.LENGTH_LONG).show();
                        } else if (mErrorCode.equals("4")) {
                            Toast.makeText(getApplicationContext(), mMessage, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }                    //successful ajax call, show status code and json content
//                    Toast.makeText(mAQuery.getContext(), status.getCode() + ":" + json.toString(), Toast.LENGTH_LONG).show();

                } else {
                    //ajax error, show error code
                    Toast.makeText(mAQuery.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean checkValidation() {

        if (userId.getText().length()<1) {
         /*new CustomToast().Show_Toast(Activity_Login.this, mView,
                 "Please enter email address.");*/
            userId.setError("Please enter user Id.");
            userId.requestFocus();
            userId.startAnimation(shakeAnimation);
            return false;
        }
        else if (userId.getText().length() < 5) {
            userId.setError("Please enter user Id.");
            userId.requestFocus();
            userId.startAnimation(shakeAnimation);
            return false;
        } else if (password.getText().length()<1) {
         /*new CustomToast().Show_Toast(Activity_Login.this, mView,
                 "Please enter password");*/
            password.setError("Please enter password");
            password.requestFocus();
            password.startAnimation(shakeAnimation);
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}