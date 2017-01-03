package com.delelong.diandiandriver.http;

import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/12/3.
 */

public abstract class MyTextHttpResponseHandler extends TextHttpResponseHandler {
    @Override
    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//        MyAsyncHttpUtils.destroyClient();
    }

    @Override
    public void onSuccess(int i, Header[] headers, String s) {
//        MyAsyncHttpUtils.destroyClient();
    }
}
