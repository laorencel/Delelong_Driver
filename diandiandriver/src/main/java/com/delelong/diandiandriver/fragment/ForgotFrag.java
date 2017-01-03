package com.delelong.diandiandriver.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.delelong.diandiandriver.LoginActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.utils.MD5;
import com.delelong.diandiandriver.utils.ToastUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/9/12.
 */
public class ForgotFrag extends Fragment implements View.OnClickListener{

    private static final String TAG = "BAIDUMAPFORTEST";

    MyHttpUtils myHttpUtils;
    private static final int SEND_VERIFICATION = 0;//开始发送验证码
    private static final int END_VERIFICATION = 1;//验证码结束
    int verificationTime = 60;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEND_VERIFICATION:
                    if (verificationTime>0){
                        verificationTime--;
                    }
                    if (verificationTime> 0 ){
                        btn_verificationCode.setTextSize(12);
                        btn_verificationCode.setText(verificationTime+"s后重发");
                    }else {
                        handler.sendEmptyMessage(END_VERIFICATION);
                    }
                    break;
                case END_VERIFICATION:
                    verificationTime = 60;
                    if (handler.hasMessages(SEND_VERIFICATION)){
                        Log.i(TAG, "handleMessage: ");
                        handler.removeMessages(SEND_VERIFICATION);
                    }
                    btn_verificationCode.setTextSize(15);
                    btn_verificationCode.setText("获 取");
                    btn_verificationCode.setBackgroundResource(R.drawable.bg_edt_register);
                    btn_verificationCode.setClickable(true);
                    break;
            }
        }
    };
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_forgot,container,false);
        initView();
        return view;
    }

    ImageButton btn_back;
    EditText edt_phone,edt_newPwd,edt_rePwd,edt_verificationCode;
    ImageView img_showPwd1;
    Button btn_verificationCode,btn_confirm;
    private void initView() {
        btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        edt_phone = (EditText) view.findViewById(R.id.edt_phone);
        edt_newPwd = (EditText) view.findViewById(R.id.edt_newPwd);
        edt_rePwd = (EditText) view.findViewById(R.id.edt_rePwd);
        edt_verificationCode = (EditText) view.findViewById(R.id.edt_verificationCode);

        btn_verificationCode = (Button) view.findViewById(R.id.btn_verificationCode);
        btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        btn_verificationCode.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);

//        img_showPwd = (ImageView) view.findViewById(R.id.img_showPwd);
        img_showPwd1 = (ImageView) view.findViewById(R.id.img_showPwd1);
        btn_back = (ImageButton) view.findViewById(R.id.btn_back);
//        img_showPwd.setOnClickListener(this);
        img_showPwd1.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    private String phone, verificationCode, pwd, rePwd;
    List<String> resultForReset;
    @Override
    public void onClick(View v) {
        phone = edt_phone.getText().toString();
        verificationCode = edt_verificationCode.getText().toString();
        String pwd_edt = edt_newPwd.getText().toString();
        String rePwd_edt = edt_rePwd.getText().toString();
        pwd = MD5.getMD5Str(pwd_edt);
        rePwd = MD5.getMD5Str(rePwd_edt);

        switch (v.getId()) {
            case R.id.img_showPwd1:
                showPwd();
                break;
            case R.id.btn_back:
                getActivity().getFragmentManager().popBackStack();
                break;
            case R.id.btn_verificationCode:
                if (phone.length()!=11) {
                    Toast.makeText(activity, "请填写完整手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<String> resultForVerific = myHttpUtils.getVerification(Str.URL_SMSCODE, phone, Str.VERIFICATION_TYPE_RESET);
                if (resultForVerific == null||resultForVerific.size()== 0){
                    return;
                }
                if (resultForVerific.get(0).equalsIgnoreCase("OK")){
                    btn_verificationCode.setTextSize(12);
                    btn_verificationCode.setText("60s后重发");
                    btn_verificationCode.setBackgroundResource(R.drawable.bg_edt_register_1);
                    btn_verificationCode.setClickable(false);
                    sendMsgDelayed();
                }
                if (resultForVerific.get(0).equals("FAILURE")) {
                    //失败 重新获取
                    Toast.makeText(activity, resultForVerific.get(1), Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case R.id.btn_confirm:
                if (!pwd_edt.equals(rePwd_edt)) {
                    Toast.makeText(activity, "请确认密码一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (verificationCode.isEmpty()) {
                    Toast.makeText(activity, "请填写验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (phone.isEmpty()) {
                    Toast.makeText(activity, "请填写手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwd_edt.isEmpty()||pwd_edt.length()<6) {
                    Toast.makeText(activity, "密码长度不能小于6位", Toast.LENGTH_SHORT).show();
                    return;
                }

                MyHttpUtils myHttpUtils = new MyHttpUtils(activity);
//                resultForReset = activity.resetPwd(URL_FORGOT, phone, verificationCode, pwd, rePwd);
                resultForReset = myHttpUtils.resetPwd(Str.URL_FORGOT, phone, verificationCode, pwd, rePwd);
                if (resultForReset == null||resultForReset.size() == 0){
                    ToastUtil.show(activity,"抱歉，未获取到数据");
                    return;
                }
                if (resultForReset.get(0).equals("FAILURE")) {
                    resultForReset = myHttpUtils.resetPwd(Str.URL_FORGOT, phone, verificationCode, pwd, rePwd);
                    if (resultForReset.get(0).equals("FAILURE")) {
                        Toast.makeText(activity, "修改失败"+resultForReset.get(1), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (resultForReset.get(0).equals("ERROR")) {
                    resultForReset = myHttpUtils.resetPwd(Str.URL_FORGOT, phone, verificationCode, pwd, rePwd);
                    if (resultForReset.get(0).equals("ERROR")) {
                        Toast.makeText(activity, "修改错误"+resultForReset.get(1), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //注册成功,返回登陆界面
                Toast.makeText(activity, "修改密码成功", Toast.LENGTH_SHORT).show();
                //保存手机号并设置为首次登陆（进入登陆页面）
                boolean firstLogin = true;
                preferences.edit()
                        .putString("phone", phone)
                        .putBoolean("firstLogin", firstLogin)
                        .apply();
                //进入登陆页面
                getActivity().getFragmentManager().popBackStack();
                break;
        }
    }
    private void sendMsgDelayed(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(SEND_VERIFICATION);
                handler.postDelayed(this,1000);
            }
        });
    }
    private boolean showPwd;
    /**
     * 切换显示密码
     */
    private void showPwd() {
        if (showPwd) {
            edt_newPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edt_rePwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            img_showPwd.setImageResource(R.drawable.show_open);
            img_showPwd1.setImageResource(R.drawable.show_open);
        } else {
            edt_newPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            edt_rePwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//            img_showPwd.setImageResource(R.drawable.show_close);
            img_showPwd1.setImageResource(R.drawable.show_close);
        }
        showPwd = !showPwd;
    }
    LoginActivity activity;
    SharedPreferences preferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (LoginActivity) getActivity();
        myHttpUtils = new MyHttpUtils(activity);
        preferences = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
    }
}
