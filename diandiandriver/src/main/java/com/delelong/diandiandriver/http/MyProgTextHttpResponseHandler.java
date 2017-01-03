package com.delelong.diandiandriver.http;

import android.app.Activity;

import com.delelong.diandiandriver.dialog.MyProgressDialog;

/**
 * Created by Administrator on 2016/11/17.
 */

public abstract class MyProgTextHttpResponseHandler extends MyTextHttpResponseHandler {
    Activity context;

    public MyProgTextHttpResponseHandler(Activity context) {
        this.context = context;
    }

    MyProgressDialog myProgressDialog;

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (context != null) {
                if (myProgressDialog == null) {
                    myProgressDialog = new MyProgressDialog(context);
                }
                if (!context.isFinishing() && myProgressDialog != null && !myProgressDialog.isShowing()) {
                    myProgressDialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        try {
            if (context != null) {
                if (!context.isFinishing() && myProgressDialog != null && myProgressDialog.isShowing()) {
                    myProgressDialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

