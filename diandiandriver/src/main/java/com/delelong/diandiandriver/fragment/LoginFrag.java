package com.delelong.diandiandriver.fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.delelong.diandiandriver.DriverActivity;
import com.delelong.diandiandriver.LoginActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.utils.MD5;
import com.delelong.diandiandriver.utils.ToastUtil;

import java.util.List;


/**
 * Created by Administrator on 2016/8/18.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginFrag extends Fragment implements View.OnClickListener {

    private static final String TAG = "BAIDUMAPFORTEST";
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_login, container, false);

        initView();
        return view;
    }

    EditText edt_phone, edt_pwd;
    Button btn_login;
    CheckBox chb_agree_service,chb_remember;//同意服务，记住密码
    /**
     * 初始化view
     */
    private void initView() {
        edt_phone = (EditText) view.findViewById(R.id.edt_phone);
        edt_pwd = (EditText) view.findViewById(R.id.edt_pwd);
        btn_login = (Button) view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        chb_agree_service = (CheckBox) view.findViewById(R.id.chb_agree_service);
        chb_remember = (CheckBox) view.findViewById(R.id.chb_remember);

        //如果以前有登陆过，获取登陆的手机号
        preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String preferenceName = preferences.getString("phone", null);
        if (preferenceName != null) {
            edt_phone.setText(preferenceName);
            if (preferences.getBoolean("rememberPwd",false)){
                edt_pwd.setText(preferences.getString("pwd_edt",null));
            }
        }
    }

    String phone, pwd_edt, pwd;
    SharedPreferences preferences;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (!chb_agree_service.isChecked()){
                    ToastUtil.show(getActivity(),"未同意《服务人员合作协议》");
                    return;
                }
                preferences.edit()
                        .putBoolean("rememberPwd", chb_remember.isChecked())
                        .apply();
                phone = edt_phone.getText().toString();
                pwd_edt = edt_pwd.getText().toString();
                pwd = MD5.getMD5Str(pwd_edt);
                login();
                break;
        }
    }

    /**
     * 验证登录
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void login() {
        if (phone.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(activity, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (phone.length() != 11 || pwd.length() < 6) {
                Toast.makeText(activity, "号码或密码长度不够", Toast.LENGTH_SHORT).show();
                return;
            } else {
                MyHttpUtils myHttpUtils = new MyHttpUtils(activity);
                List<String> result = myHttpUtils.login(Str.URL_LOGIN, phone, pwd);
                if (result.get(0).equals("OK")) {
                    LoginActivity finish = (LoginActivity) getActivity();
                    startActivity(new Intent(getActivity(), DriverActivity.class));
                    //存储用户(token)、密码(sercet)
                    setPreferences(result);
                    finish.finish();
                } else if (result.get(0).equals("ERROR")) {
                    Toast.makeText(getActivity(), "登陆出错,请重新登陆", Toast.LENGTH_SHORT).show();
                    return;
                }else if (result.get(0).equals("FAILURE")) {
                    Toast.makeText(getActivity(), "登陆失败,请重新登陆", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }
    public void setPreferences(List<String> result){
        boolean firstLogin = false;
        int loginTimes = preferences.getInt("loginTimes",0);
        preferences.edit()
                .putString("token", result.get(2))
                .putString("sercet", result.get(3))
                .putString("phone", phone)
                .putString("pwd", pwd)
                .putString("pwd_edt", pwd_edt)
                .putInt("loginTimes",++loginTimes)
                .putBoolean("firstLogin", firstLogin)
                .commit();
    }


    LoginActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (LoginActivity) getActivity();
    }

}
