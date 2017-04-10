package com.delelong.diandiandriver.menuActivity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.DriverAmount;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.dialog.MyDialogUtils;
import com.delelong.diandiandriver.dialog.MyToastDialog;
import com.delelong.diandiandriver.http.MyAsyncHttpUtils;
import com.delelong.diandiandriver.http.MyHttpHelper;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.http.MyTextHttpResponseHandler;
import com.delelong.diandiandriver.listener.MyPayResultInterface;
import com.delelong.diandiandriver.utils.ToastUtil;
import com.google.common.primitives.Doubles;
import com.loopj.android.http.RequestParams;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/10/27.
 */

public class MyBalanceActivity extends BaseActivity implements View.OnClickListener, TextWatcher, MyPayResultInterface {
    private static final int CONFIRM_RECHARGE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        initActionBar();
        registerMessageReceiver();
        initView();
        initMsg();
    }

    MyDialogUtils myDialogUtils;
    MyHttpUtils myHttpUtils;
    DriverAmount mDriverAmount;
    List<String> rechargeItem;
    MyHttpHelper myHttpHelper;

    private void initMsg() {
        if (isNull(myDialogUtils)) {
            myDialogUtils = new MyDialogUtils(this);
        }
        if (isNull(myHttpUtils)) {
            myHttpUtils = new MyHttpUtils(this);
        }
        if (isNull(myHttpHelper)) {
            myHttpHelper = new MyHttpHelper(MyBalanceActivity.this);
        }
        mDriverAmount = myHttpUtils.getDriverYeAmount(Str.URL_DRIVER_YE_AMOUNT);
        rechargeItem = myHttpUtils.setRechangeItemByGET(Str.URL_RECHARGE_AMOUNT);

        if (mDriverAmount != null) {
            tv_myBalance.setText(mDriverAmount.getYe() + " 元");
        }
        if (rechargeItem != null) {
            if (rechargeItem.get(0).equalsIgnoreCase("OK")) {
                btn_sum_0.setText(rechargeItem.get(2));
                btn_sum_1.setText(rechargeItem.get(3));
                btn_sum_2.setText(rechargeItem.get(4));
            } else {
                ToastUtil.show(this, rechargeItem.get(1));
            }
        }
    }

    OrderMessageReceiver orderMessageReceiver;

    public void registerMessageReceiver() {
        orderMessageReceiver = new OrderMessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Str.WXPAY_RESULT_ACTION);//微信回调通知
        registerReceiver(orderMessageReceiver, filter);
    }

    ImageButton arrow_back;

    private void initActionBar() {
        arrow_back = (ImageButton) findViewById(R.id.arrow_back);
        arrow_back.setOnClickListener(this);
    }

    TextView tv_myBalance;
    Button btn_sum_0, btn_sum_1, btn_sum_2;
    EditText edt_sum;
    LinearLayout ly_pay_ali, ly_pay_wx;

    private void initView() {
        tv_myBalance = (TextView) findViewById(R.id.tv_myBalance);
        btn_sum_0 = (Button) findViewById(R.id.btn_sum_0);
        btn_sum_1 = (Button) findViewById(R.id.btn_sum_1);
        btn_sum_2 = (Button) findViewById(R.id.btn_sum_2);
        edt_sum = (EditText) findViewById(R.id.edt_sum);
        ly_pay_ali = (LinearLayout) findViewById(R.id.ly_pay_ali);
        ly_pay_wx = (LinearLayout) findViewById(R.id.ly_pay_wx);

        setListener();
    }

    private void setListener() {
        btn_sum_0.setOnClickListener(this);
        btn_sum_1.setOnClickListener(this);
        btn_sum_2.setOnClickListener(this);
        ly_pay_ali.setOnClickListener(this);
        ly_pay_wx.setOnClickListener(this);
        edt_sum.addTextChangedListener(this);
    }

    String payUrl;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.btn_sum_0:
                changeSum(btn_sum_0);
                break;
            case R.id.btn_sum_1:
                changeSum(btn_sum_1);
                break;
            case R.id.btn_sum_2:
                changeSum(btn_sum_2);
                break;
            case R.id.ly_pay_ali:
//                Log.i(TAG, "onClick: " + sum);
                payUrl = Str.URL_DRIVER_ALIRECHARGE;
                recharge();
                break;
            case R.id.ly_pay_wx:
                payUrl = Str.URL_DRIVER_WXRECHARGE;
                recharge();
                break;

        }
    }

    public void recharge() {
        if (sum > 0 && sum % 10 == 0) {
//        if (sum > 0) {
            myDialogUtils.confirmRecharge(CONFIRM_RECHARGE, new MyDialogUtils.MyDialogInterface() {

                @Override
                public void chooseDriverCar(int requestCode, int position) {

                }

                @Override
                public void sure(int requestCode, String arg0) {

                }

                @Override
                public void sure(int request, Object object) {
                    boolean sure = (boolean) object;
                    if (sure) {
                        RequestParams params = myHttpHelper.getRechargeParams(sum + "");
                        MyAsyncHttpUtils.post(payUrl, params, new MyTextHttpResponseHandler() {
                            @Override
                            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                                super.onFailure(i, headers, s, throwable);
                            }

                            @Override
                            public void onSuccess(int i, Header[] headers, String s) {
                                super.onSuccess(i, headers, s);
                                List<String> result;
                                switch (payUrl) {
                                    case Str.URL_DRIVER_ALIRECHARGE:
                                        result = myHttpHelper.resultByJson(s, null);
                                        if (result == null) {
                                            return;
                                        }
                                        if (result.get(0).equalsIgnoreCase("OK")) {
                                            PayByAli(result.get(1), MyBalanceActivity.this);
                                        } else {
                                            ToastUtil.show(MyBalanceActivity.this, result.get(1));
                                        }
                                        break;
                                    case Str.URL_DRIVER_WXRECHARGE:
                                        result = myHttpHelper.wxPayInforesultByJson(s, null);
                                        if (result == null || result.size() < 2) {
                                            MyToastDialog.show(MyBalanceActivity.this, "支付失败，未获取到信息");
                                            return;
                                        }
                                        if (result.get(0).equalsIgnoreCase("OK") && result.size() >= 3) {
                                            PayByWX(result.get(2));
                                        } else {
                                            MyToastDialog.show(MyBalanceActivity.this, "支付失败，" + result.get(1));
                                        }
                                        break;
                                }
                            }
                        });
                    }
                }
            });
        } else {
            ToastUtil.show(this, "输入金额必须是10的倍数");
            return;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String ss = s.toString().equals("") ? "0" : s.toString();
        double sum_ = Editable2Double(ss);
        sum = sum_;
        changeSum(edt_sum);
    }

    private double Editable2Double(String ss) {
        double num;
        try {
            num = Doubles.tryParse(ss.toString());
//            if (num < 0.01) {
//                ToastUtil.show(this, "输入金额不能小于0.01元");
//            }
        } catch (Exception e) {
            return 0;
        }
        return num;
    }

    double sum;

    @TargetApi(Build.VERSION_CODES.M)
    private void changeSum(View view) {
        switch (view.getId()) {
            case R.id.btn_sum_0:
                edt_sum.setText("");
                btn_sum_0.setTextColor(Color.parseColor("#1BC47A"));
                btn_sum_1.setTextColor(Color.parseColor("#ffffff"));
                btn_sum_2.setTextColor(Color.parseColor("#ffffff"));
                sum = Doubles.tryParse(btn_sum_0.getText().toString());
                break;
            case R.id.btn_sum_1:
                edt_sum.setText("");
                btn_sum_0.setTextColor(Color.parseColor("#ffffff"));
                btn_sum_1.setTextColor(Color.parseColor("#1BC47A"));
                btn_sum_2.setTextColor(Color.parseColor("#ffffff"));
                sum = Doubles.tryParse(btn_sum_1.getText().toString());
                break;
            case R.id.btn_sum_2:
                edt_sum.setText("");
                btn_sum_0.setTextColor(Color.parseColor("#ffffff"));
                btn_sum_1.setTextColor(Color.parseColor("#ffffff"));//白色
                btn_sum_2.setTextColor(Color.parseColor("#1BC47A"));//绿色
                sum = Doubles.tryParse(btn_sum_2.getText().toString());
                break;
            case R.id.edt_sum:
//                btn_sum_0.setBackgroundResource(R.drawable.bg_alpha_corner_edt);
//                btn_sum_1.setBackgroundResource(R.drawable.bg_alpha_corner_edt);
//                btn_sum_2.setBackgroundResource(R.drawable.bg_alpha_corner_edt);
                btn_sum_0.setTextColor(Color.parseColor("#ffffff"));
                btn_sum_1.setTextColor(Color.parseColor("#ffffff"));
                btn_sum_2.setTextColor(Color.parseColor("#ffffff"));
                Log.i(TAG, "changeSum: 444");
                break;
        }
    }

    public void payRusultByWX(int code) {
        if (code == 0) {//显示充值成功的页面和需要的操作
            MyToastDialog.show(MyBalanceActivity.this, "充值成功！");
            initMsg();
        }
        if (code == -1) {//错误:可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
            MyToastDialog.show(MyBalanceActivity.this, "支付错误！");
        }
        if (code == -2) {//用户取消

        }
    }

    @Override
    public void payResult(String payResult) {
        Log.i(TAG, "payResult: " + payResult);
        if (payResult.equals("支付成功")) {
            MyToastDialog.show(MyBalanceActivity.this, "充值成功！");
            initMsg();
        }
    }

    public class OrderMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Str.WXPAY_RESULT_ACTION.equals(intent.getAction())) {
                int payResultCode = intent.getIntExtra(Str.KEY_ORDER_TITLE, 2);
                payRusultByWX(payResultCode);
            }
        }
    }
}
