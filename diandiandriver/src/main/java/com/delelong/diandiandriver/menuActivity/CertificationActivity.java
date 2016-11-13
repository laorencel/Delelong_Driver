package com.delelong.diandiandriver.menuActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.Client;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.utils.ToastUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/9/18.
 */
public class CertificationActivity extends BaseActivity implements TextWatcher, View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_certification);
        initActionBar();
        initView();
    }

    ImageButton arrow_back;

    private void initActionBar() {
        arrow_back = (ImageButton) findViewById(R.id.arrow_back);
        arrow_back.setOnClickListener(this);
    }

    EditText edt_realName, edt_certificateNo;
    Button btn_confirm;
    MyHttpUtils myHttpUtils;

    private void initView() {
        edt_realName = (EditText) findViewById(R.id.edt_realName);
        edt_certificateNo = (EditText) findViewById(R.id.edt_certificateNo);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setEnabled(false);
        edt_certificateNo.addTextChangedListener(this);

        myHttpUtils = new MyHttpUtils(this);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        client = (Client) bundle.getSerializable("client");//从上级activity获取
    }

    Client client;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() ==15 ||s.length() == 18){
            btn_confirm.setEnabled(true);
            btn_confirm.setBackgroundResource(R.drawable.bg_corner_btn);
        }else {
            btn_confirm.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.btn_confirm:
                if (client == null) {
                    client = myHttpUtils.getClientByGET(Str.URL_MEMBER);
                }
                String name = edt_realName.getText().toString();
                String certificateNo = edt_certificateNo.getText().toString();

                if (name.length() < 2) {
                    ToastUtil.show(this,"姓名少于2个字");
                    return;
                }
                if (!personIdValidation(certificateNo)){
                    ToastUtil.show(this,"身份证号码不符");
                    return;
                }
                client.setReal_name(name);
                client.setCertificate_no(certificateNo);
                List<String> result = myHttpUtils.upDateClient(Str.URL_UPDATECLIENT, client);
                if (result == null){
                    return;
                }
                if (result.get(0).equalsIgnoreCase("OK")) {
                    ToastUtil.show(this,"提交成功");
                    finish();
                } else {
                    ToastUtil.show(this,"提交失败，"+result.get(1));
                    return;
                }
                break;
        }
    }

    /**
     * 验证身份证号是否符合规则
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
