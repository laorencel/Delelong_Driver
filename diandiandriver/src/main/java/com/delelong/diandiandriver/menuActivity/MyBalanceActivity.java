package com.delelong.diandiandriver.menuActivity;

import android.annotation.TargetApi;
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
import com.delelong.diandiandriver.alipay.util.Alipay;
import com.delelong.diandiandriver.bean.DriverAmount;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.dialog.MyDialogUtils;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.utils.ToastUtil;
import com.google.common.primitives.Doubles;

import java.util.List;

/**
 * Created by Administrator on 2016/10/27.
 */

public class MyBalanceActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private static final int CONFIRM_RECHARGE = 0;
    private static final String TAG = "BAIDUMAPFORTEST";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        initActionBar();
        initView();
        initMsg();
    }

    MyDialogUtils myDialogUtils;
    MyHttpUtils myHttpUtils;
    DriverAmount mDriverAmount;
    List<String> rechargeItem;
    private void initMsg() {
        myDialogUtils = new MyDialogUtils(this);
        myHttpUtils = new MyHttpUtils(this);
        mDriverAmount = myHttpUtils.getDriverYeAmount(Str.URL_DRIVER_YE_AMOUNT);
        rechargeItem = myHttpUtils.setRechangeItemByGET(Str.URL_RECHARGE_AMOUNT);

        if (mDriverAmount!= null){
            tv_myBalance.setText(mDriverAmount.getYe()+" 元");
        }
        if (rechargeItem !=null){
            if (rechargeItem.get(0).equalsIgnoreCase("OK")){
                btn_sum_0.setText(rechargeItem.get(2));
                btn_sum_1.setText(rechargeItem.get(3));
                btn_sum_2.setText(rechargeItem.get(4));
            }else {
                ToastUtil.show(this,rechargeItem.get(1));
            }
        }


    }

    ImageButton arrow_back;
    private void initActionBar() {
        arrow_back = (ImageButton) findViewById(R.id.arrow_back);
        arrow_back.setOnClickListener(this);
    }

    TextView tv_myBalance;
    Button btn_sum_0, btn_sum_1, btn_sum_2;
    EditText edt_sum;
    LinearLayout ly_pay_ali;

    private void initView() {
        tv_myBalance = (TextView) findViewById(R.id.tv_myBalance);
        btn_sum_0 = (Button) findViewById(R.id.btn_sum_0);
        btn_sum_1 = (Button) findViewById(R.id.btn_sum_1);
        btn_sum_2 = (Button) findViewById(R.id.btn_sum_2);
        edt_sum = (EditText) findViewById(R.id.edt_sum);
        ly_pay_ali = (LinearLayout) findViewById(R.id.ly_pay_ali);

        setListener();
    }

    private void setListener() {
        btn_sum_0.setOnClickListener(this);
        btn_sum_1.setOnClickListener(this);
        btn_sum_2.setOnClickListener(this);
        ly_pay_ali.setOnClickListener(this);
        edt_sum.addTextChangedListener(this);
    }

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
                Log.i(TAG, "onClick: "+sum);
                if (sum>0 &&sum%10 == 0){
                    myDialogUtils.confirmRecharge(CONFIRM_RECHARGE, new MyDialogUtils.MyDialogInterface() {
                        @Override
                        public void chooseDriverCar(int requestCode, int position) {

                        }

                        @Override
                        public void sure(int requestCode, String arg0) {

                        }

                        @Override
                        public void sure(int requestCode, Object object) {
                            boolean sure = (boolean) object;
                            if (sure){
                                List<String> result = myHttpUtils.getPayOrderInfo(Str.URL_DRIVER_RECHARGE,sum+"");
                                if (result.get(0).equalsIgnoreCase("OK")){
                                    toPay(result.get(1));

                                }else {
                                    ToastUtil.show(MyBalanceActivity.this,result.get(1));
                                }
                            }
                        }
                    });

                }else {
                    ToastUtil.show(this,"输入金额必须是10的倍数");
                    return;
                }
                break;

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
        } catch (Exception e) {
            return 0;
        }
        return num;
    }
    private void toPay(String orderInfo) {
        //测试
        Alipay alipay = new Alipay(this);
        alipay.payV2(orderInfo);
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
}
