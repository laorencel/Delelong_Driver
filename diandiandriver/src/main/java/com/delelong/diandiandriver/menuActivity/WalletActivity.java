package com.delelong.diandiandriver.menuActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.DriverAmount;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyAsyncHttpUtils;
import com.delelong.diandiandriver.http.MyHttpHelper;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Administrator on 2016/9/18.
 */
public class WalletActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_wallet);
        initActionBar();
        initView();
        initYE();
    }

    RelativeLayout rl_pay, rl_sum, rl_tixian;
    TextView tv_pay, tv_sum, tv_coupon;

    private void initView() {
        rl_pay = (RelativeLayout) findViewById(R.id.rl_pay);//支付方式
        rl_sum = (RelativeLayout) findViewById(R.id.rl_sum);//余额
        rl_tixian = (RelativeLayout) findViewById(R.id.rl_tixian);//提现

        tv_pay = (TextView) findViewById(R.id.tv_pay);
        tv_sum = (TextView) findViewById(R.id.tv_sum);
        tv_coupon = (TextView) findViewById(R.id.tv_coupon);

        rl_pay.setOnClickListener(this);
        rl_sum.setOnClickListener(this);
        rl_tixian.setOnClickListener(this);
//        rl_invoice.setOnClickListener(this);//报销暂取消
    }

    MyHttpHelper myHttpHelper;
    DriverAmount mDriverAmount;

    private void initYE() {
        if (myHttpHelper == null) {
            myHttpHelper = new MyHttpHelper(this);
        }
        MyAsyncHttpUtils.post(Str.URL_DRIVER_YE_AMOUNT, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                if (myHttpHelper == null) {
                    myHttpHelper = new MyHttpHelper(WalletActivity.this);
                }
                if (myHttpHelper == null) {
                    return;
                }
                mDriverAmount = myHttpHelper.getDriverYeAmountByJson(s, null);
                if (mDriverAmount != null) {
                    tv_sum.setText("￥" + mDriverAmount.getYe());
                }
            }
        });
//            mDriverAmount = myHttpUtils.getDriverYeAmount(Str.URL_DRIVER_YE_AMOUNT);
    }

    ImageButton arrow_back;

    private void initActionBar() {
        arrow_back = (ImageButton) findViewById(R.id.arrow_back);
        arrow_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.rl_pay:
                startActivity(new Intent(this, PaymentActivity.class));
                break;
            case R.id.rl_sum:
                startActivity(new Intent(this, MyAccountActivity.class));
                break;
            case R.id.rl_tixian:
                startActivity(new Intent(this, MyTiXianActivity.class));
                break;
        }
    }
}
