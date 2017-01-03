package com.delelong.diandiandriver.fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.delelong.diandiandriver.LoginActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.menuActivity.MyLawActivity;
import com.delelong.diandiandriver.utils.MD5;
import com.delelong.diandiandriver.utils.ToastUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Administrator on 2016/8/18.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginFrag extends Fragment implements View.OnClickListener {

    private static final String TAG = "BAIDUMAPFORTEST";
    View view;
    ExecutorService threadPool = Executors.newCachedThreadPool();
    Handler loginHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    activity.login(phone, pwd_edt, pwd);
                    break;
                case 1:
                    View view = (View) msg.obj;
                    view.setEnabled(true);
                    break;
            }
        }
    };

    private void sendMsgDelayToHandlerByExecutor(final int what, final Object object, final long delayMillis) {
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                Message message = loginHandler.obtainMessage();
                message.what = what;
                message.obj = object;
                loginHandler.sendMessageDelayed(message, delayMillis);
            }
        });
    }
    public void clickableDelayed(View view) {
        view.setEnabled(false);
        sendMsgDelayToHandlerByExecutor(1, view, 3000);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_login, container, false);

        initView();
        return view;
    }

    EditText edt_phone, edt_pwd;
    Button btn_login;
    CheckBox chb_agree_service, chb_remember;//同意服务，记住密码
    TextView tv_law;

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

        tv_law = (TextView) view.findViewById(R.id.tv_law);
        tv_law.setOnClickListener(this);
        //如果以前有登陆过，获取登陆的手机号
        preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String preferenceName = preferences.getString("phone", null);
        if (preferenceName != null) {
            edt_phone.setText(preferenceName);
            if (preferences.getBoolean("rememberPwd", false)) {
                chb_remember.setChecked(true);
                edt_pwd.setText(preferences.getString("pwd_edt", null));
            }
        }
        chb_agree_service.setChecked(true);
    }

    String phone, pwd_edt, pwd;
    SharedPreferences preferences;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (!chb_agree_service.isChecked()) {
                    ToastUtil.show(getActivity(), "未同意《服务人员合作协议》");
                    return;
                }
                clickableDelayed(btn_login);
                preferences.edit()
                        .putBoolean("rememberPwd", chb_remember.isChecked())
                        .apply();
                phone = edt_phone.getText().toString();
                pwd_edt = edt_pwd.getText().toString();
                pwd = MD5.getMD5Str(pwd_edt);
                if (activity != null) {
                    activity.hideSoftInputFromWindow();
                }
                threadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        loginHandler.sendEmptyMessage(0);
                    }
                });
                break;
            case R.id.tv_law:
                //客户端类型(目前有这几种类型: 1:软件使用协议和政策;2:代驾服务协议;
                // 3:司机服务合作协议;4:关于我们;5:出租车用户协议;6:专车使用条款;)
                String url = Str.URL_LAW_PROVISION + "?appType=" + Str.APPTYPE_CLIENT + "&provisionType=" + 3;
                Intent intent = new Intent(activity, MyLawActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loginHandler != null) {
            loginHandler.removeCallbacksAndMessages(null);
            loginHandler = null;
        }
    }

    LoginActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (LoginActivity) getActivity();
    }

}
