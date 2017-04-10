package com.delelong.diandiandriver.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.delelong.diandiandriver.bean.Str;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


/**
 * Created by Administrator on 2017/2/13.
 */

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "BAIDUMAPFORTEST";
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_main);

        api = WXAPIFactory.createWXAPI(this, Str.APP_ID_WX);
        api.handleIntent(getIntent(), this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        Toast.makeText(getApplicationContext(),"onReq",Toast.LENGTH_SHORT).show();
        Log.i(TAG, "WXPayEntryActivity onReq: "+req);
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.i(TAG, "WXPayEntryActivity onResp: "+resp+"//"+resp.errCode);
        int code = resp.errCode;
        Intent payResultIntent = new Intent(Str.WXPAY_RESULT_ACTION);
        payResultIntent.putExtra(Str.KEY_ORDER_TITLE, code);
        sendBroadcast(payResultIntent);
        finish();
//        if (code == 0){//显示充值成功的页面和需要的操作
//
//        }
//        if (code == -1){//错误:可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
//
//        }
//        if (code == -2){//用户取消
//
//        }
    }
}