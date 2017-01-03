package com.delelong.diandiandriver.http;

import com.loopj.android.http.BinaryHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/12/3.
 */

public abstract class MyBinaryHttpResponseHandler extends BinaryHttpResponseHandler {
    @Override
    public void onSuccess(int i, Header[] headers, byte[] bytes) {
//        MyAsyncHttpUtils.destroyClient();
    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//        MyAsyncHttpUtils.destroyClient();
    }
}
