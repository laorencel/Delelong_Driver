package com.delelong.diandiandriver.menuActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.Str;

/**
 * Created by Administrator on 2016/11/4.
 */

public class MyFeeRuleActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "BAIDUMAPFORTEST";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fee_rule);
        initActionBar();
        initView();
        initMsg();
    }

    String url;
    String cityCode = "340100";
    private void setMsg() {
//        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
//        if (preferences != null){
//            String phone = preferences.getString("phone", null);
//            if (phone!= null){
//            }
//        }
        url =  Str.URL_DETAIL_AMOUNT_RULE+"?cityCode="+cityCode+"&type=1"+"&serviceType="+serviceType;
        if (url!=null&&!url.equals("")){
            Intent intent = new Intent(this,MyWebViewActivity.class);
            intent.putExtra("url",url);
            startActivity(intent);
        }
    }

    private void initMsg(){
        String cityCode_ = getIntent().getStringExtra("cityCode");
        if (cityCode_!=null&&!cityCode_.isEmpty()){
            cityCode =cityCode_;
        }
    }
    ImageView arrow_back;

    private void initActionBar() {
        arrow_back = (ImageView) findViewById(R.id.arrow_back);
        arrow_back.setOnClickListener(this);
    }
    TextView tv_zhuanChe, tv_daiJia, tv_chuZuChe, tv_kuaiChe;
    int serviceType = 1;
    private void initView() {
        tv_zhuanChe = (TextView) findViewById(R.id.tv_zhuanChe);
        tv_daiJia = (TextView) findViewById(R.id.tv_daiJia);
        tv_chuZuChe = (TextView) findViewById(R.id.tv_chuZuChe);
        tv_kuaiChe = (TextView) findViewById(R.id.tv_kuaiChe);
        tv_zhuanChe.setTextColor(Color.parseColor("#1BC47A"));

        tv_zhuanChe.setOnClickListener(this);
        tv_daiJia.setOnClickListener(this);
        tv_chuZuChe.setOnClickListener(this);
        tv_kuaiChe.setOnClickListener(this);
    }
    private void changeSum(View view) {
        switch (view.getId()) {
            case R.id.tv_zhuanChe:
                serviceType = 1;
                tv_zhuanChe.setTextColor(Color.parseColor("#1BC47A"));
                tv_daiJia.setTextColor(Color.parseColor("#ffffff"));//白色
                tv_chuZuChe.setTextColor(Color.parseColor("#ffffff"));//绿色
                tv_kuaiChe.setTextColor(Color.parseColor("#ffffff"));//绿色
                break;
            case R.id.tv_daiJia:
                serviceType = 2;
                tv_zhuanChe.setTextColor(Color.parseColor("#ffffff"));
                tv_daiJia.setTextColor(Color.parseColor("#1BC47A"));//白色
                tv_chuZuChe.setTextColor(Color.parseColor("#ffffff"));//绿色
                tv_kuaiChe.setTextColor(Color.parseColor("#ffffff"));//绿色
                break;
            case R.id.tv_chuZuChe:
                serviceType = 3;
                tv_zhuanChe.setTextColor(Color.parseColor("#ffffff"));
                tv_daiJia.setTextColor(Color.parseColor("#ffffff"));//白色
                tv_chuZuChe.setTextColor(Color.parseColor("#1BC47A"));//绿色
                tv_kuaiChe.setTextColor(Color.parseColor("#ffffff"));//绿色
                break;
            case R.id.tv_kuaiChe:
                serviceType = 4;
                tv_zhuanChe.setTextColor(Color.parseColor("#ffffff"));
                tv_daiJia.setTextColor(Color.parseColor("#ffffff"));//白色
                tv_chuZuChe.setTextColor(Color.parseColor("#ffffff"));//绿色
                tv_kuaiChe.setTextColor(Color.parseColor("#1BC47A"));//绿色
                break;
        }
        setMsg();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.tv_zhuanChe:
                changeSum(tv_zhuanChe);
                break;
            case R.id.tv_daiJia:
                changeSum(tv_daiJia);
                break;
            case R.id.tv_chuZuChe:
                changeSum(tv_chuZuChe);
                break;
            case R.id.tv_kuaiChe:
                changeSum(tv_kuaiChe);
                break;
        }
    }
}
