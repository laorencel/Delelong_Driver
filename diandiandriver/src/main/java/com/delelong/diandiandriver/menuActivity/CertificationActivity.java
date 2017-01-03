package com.delelong.diandiandriver.menuActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.Driver;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyAsyncHttpUtils;
import com.delelong.diandiandriver.http.MyHttpHelper;
import com.delelong.diandiandriver.http.MyProgTextHttpResponseHandler;
import com.delelong.diandiandriver.http.MyTextHttpResponseHandler;
import com.delelong.diandiandriver.listener.MyHttpDataListener;
import com.delelong.diandiandriver.numberPicker.ToastUtil;
import com.loopj.android.http.RequestParams;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/9/18.
 */
public class CertificationActivity extends BaseActivity implements TextWatcher, View.OnClickListener {

    private static final String TAG = "BAIDUMAPFORTEST";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_certification);
        initActionBar();
        initView();
        initDriver();
    }

    ImageButton arrow_back;

    private void initActionBar() {
        arrow_back = (ImageButton) findViewById(R.id.arrow_back);
        arrow_back.setOnClickListener(this);
    }

    EditText edt_realName, edt_certificateNo;
    Button btn_confirm;
//    MyHttpUtils myHttpUtils;

    private void initView() {
        edt_realName = (EditText) findViewById(R.id.edt_realName);
        edt_certificateNo = (EditText) findViewById(R.id.edt_certificateNo);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setEnabled(false);
        edt_certificateNo.addTextChangedListener(this);
        btn_confirm.setOnClickListener(this);

//        myHttpUtils = new MyHttpUtils(this);
        myHttpHelper = new MyHttpHelper(this);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 15 || s.length() == 18) {
            btn_confirm.setEnabled(true);
            btn_confirm.setBackgroundResource(R.drawable.bg_corner_btn);
        } else {
            btn_confirm.setEnabled(false);
        }
    }

    Driver driver;

    private void initDriver() {
        MyAsyncHttpUtils.get(Str.URL_MEMBER, new MyTextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.i(TAG, "onSuccess:getDriverByJson: " + s);
                if (driver == null) {
                    driver = myHttpHelper.getDriverByJson(s, new MyHttpDataListener() {
                        @Override
                        public void toLogin() {
                            ToastUtil.show(CertificationActivity.this, "未登录");
                        }

                        @Override
                        public void onError(Object object) {

                        }
                    });
                }
            }
        });
    }

    MyHttpHelper myHttpHelper;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.btn_confirm:
                final String name = edt_realName.getText().toString();
                final String certificateNo = edt_certificateNo.getText().toString();
                if (name.length() < 2) {
                    ToastUtil.show(this, "姓名少于2个字");
                    return;
                }
                if (!personIdValidation(certificateNo)) {
                    ToastUtil.show(this, "身份证号码不符");
                    return;
                }

                if (driver == null) {
                    MyAsyncHttpUtils.get(Str.URL_MEMBER, new MyTextHttpResponseHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            Log.i(TAG, "onSuccess:getDriverByJson: " + s);
                            if (driver == null) {
                                driver = myHttpHelper.getDriverByJson(s, new MyHttpDataListener() {
                                    @Override
                                    public void toLogin() {
                                        ToastUtil.show(CertificationActivity.this, "未登录");
                                    }

                                    @Override
                                    public void onError(Object object) {

                                    }
                                });
                                if (driver != null) {
                                    if (driver.getCertificate_no() != null) {
                                        if (driver.getCertificate_no().equals("")) {
                                            upDateClient(driver, name, certificateNo);
                                        } else {
                                            ToastUtil.show(CertificationActivity.this, "您已验证过身份信息，无需再次验证");
                                        }
                                    }
                                }
                            }
                        }
                    });
                } else {
                    if (driver.getCertificate_no() != null) {
                        if (driver.getCertificate_no().equals("")) {
                            upDateClient(driver, name, certificateNo);
                        } else {
                            ToastUtil.show(CertificationActivity.this, "您已验证过身份信息，无需再次验证");
                        }
                    }
                }
                break;
        }
    }

    private void upDateClient(Driver driver, String name, String certificateNo) {
        driver.setReal_name(name);
        driver.setCertificate_no(certificateNo);
        RequestParams params = myHttpHelper.getUpDateDriverParams(driver);
        MyAsyncHttpUtils.post(Str.URL_UPDATEDRIVER, params, new MyProgTextHttpResponseHandler(this) {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                ToastUtil.show(CertificationActivity.this, "提交失败");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                List<String> result = myHttpHelper.resultByJson(s, new MyHttpDataListener() {
                    @Override
                    public void toLogin() {
//                        MyToastDialog.show(CertificationActivity.this, "未登录");
                        ToastUtil.show(CertificationActivity.this, "未登录");
                    }

                    @Override
                    public void onError(Object object) {
//                        MyToastDialog.show(CertificationActivity.this, object.toString());
                        ToastUtil.show(CertificationActivity.this, object.toString());
                    }
                });
                if (result == null) {
                    return;
                }
                if (result.get(0).equalsIgnoreCase("OK")) {
                    ToastUtil.show(CertificationActivity.this, "提交成功");
//                    MyToastDialog.show(CertificationActivity.this, "提交成功");
                    finish();
                } else {
                    ToastUtil.show(CertificationActivity.this, "提交失败，" + result.get(1));
//                    MyToastDialog.show(CertificationActivity.this, "提交失败，" + result.get(1));
                    return;
                }
            }
        });
//        List<String> result = myHttpUtils.upDateClient(Str.URL_UPDATECLIENT, client);
    }

    /**
     * 验证身份证号是否符合规则
     *
     * @param text 身份证号
     * @return
     */
    public boolean personIdValidation(String text) {
        String regx = "[0-9]{17}x";
        String reg1 = "[0-9]{15}";
        String regex = "[0-9]{18}";
        return text.matches(regx) || text.matches(reg1) || text.matches(regex);
    }
}
