package com.delelong.diandiandriver.wxapi;

import android.util.Log;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.bean.Str;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/2/13.
 */

public class WXPay {
    private static final String TAG = "BAIDUMAPFORTEST";
    BaseActivity activity;
    IWXAPI msgApi;

    public WXPay(BaseActivity activity) {
        this.activity = activity;
        msgApi = WXAPIFactory.createWXAPI(activity, null);
// 将该app注册到微信
        msgApi.registerApp(Str.APP_ID_WX);
    }

    public void pay(String content) {
        Log.i(TAG, "pay: "+content);
        JSONObject json = null;
        try {
            json = new JSONObject(content);
            if (null != json && !json.has("retcode")) {
                PayReq req = new PayReq();
                req.appId = json.getString("appid");
                req.partnerId = json.getString("partnerid");
                req.prepayId = json.getString("prepayid");
                req.nonceStr = json.getString("noncestr");
//                String currentTime = System.currentTimeMillis()+"";
//                if (currentTime.length()>10){
//                    req.timeStamp = Long.parseLong(currentTime)/1000+"";
//                }else {
//                    req.timeStamp = currentTime+"";
//                }
                req.timeStamp =json.getString("timestamp");
                req.packageValue =json.getString("package");
                req.sign = json.getString("sign");
                req.extData = "app data"; // optional
//                Toast.makeText(activity, "正常调起支付", Toast.LENGTH_SHORT).show();
                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                Log.i(TAG, "pay: "+req);
                msgApi.sendReq(req);
            } else {
                Log.d("BAIDUMAPFORTEST", "返回错误" + json.getString("retmsg"));
//                Toast.makeText(activity, "返回错误" + json.getString("retmsg"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

    }


}
