package com.sap.taskschdular.Session;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    public static SharedPreferences mSharedPreference;
    public static SharedPreferences.Editor mShEditor;
    public static Session ourInstance = null;

    public static Session getInstance() {
        if (ourInstance == null) {
            ourInstance = new Session ();
        }
        return ourInstance;
    }

    private void init(Context mContext) {
        mSharedPreference = mContext.getSharedPreferences(SessionConstents.PROJECT_NAME, Context.MODE_PRIVATE);
        mShEditor = mSharedPreference.edit();
    }

    public String getValue(Context mContext, String mStrKey) {
        if (mSharedPreference == null) {
            init(mContext);
        }
        return mSharedPreference.getString(mStrKey, "");
    }


    public void updateValue(Context mContext, String mStrKey, String mStrValue) {
        if (mSharedPreference == null) {
            init(mContext);
        }
        mShEditor.putString(mStrKey, mStrValue);
        mShEditor.commit();
    }

    public void sessionLogout(Context mContext) {
        if (mSharedPreference == null) {
            init(mContext);
        }
        //String mStrDeviceToken = Session.getInstance().getValue(mContext, SessionConstants.KEY_FIREBASE_TOKEN_ID);
        //Session.getInstance().updateValue(mContext, SessionConstants.KEY_FIREBASE_TOKEN_ID, mStrDeviceToken);
        mShEditor.clear();
        mShEditor.commit();
    }
}
