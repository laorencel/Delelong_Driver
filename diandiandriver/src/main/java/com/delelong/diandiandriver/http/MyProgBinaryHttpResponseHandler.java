package com.delelong.diandiandriver.http;

import android.app.Activity;

import com.delelong.diandiandriver.dialog.MyProgressDialog;

/**
 * Created by Administrator on 2016/11/17.
 */

public abstract class MyProgBinaryHttpResponseHandler extends MyBinaryHttpResponseHandler {
    Activity context;

    public MyProgBinaryHttpResponseHandler(Activity context) {
        super();
        this.context = context;
    }

    MyProgressDialog myProgressDialog;

    @Override
    public void onStart() {
        super.onStart();
        if (context != null) {
            if (myProgressDialog == null) {
                myProgressDialog = new MyProgressDialog(context);
            }
            if (!context.isFinishing()&&myProgressDialog != null && !myProgressDialog.isShowing()) {
                myProgressDialog.show();
            }
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        if (context != null) {
            if (!context.isFinishing()&&myProgressDialog != null && myProgressDialog.isShowing()) {
                myProgressDialog.dismiss();
            }
        }
    }
}
