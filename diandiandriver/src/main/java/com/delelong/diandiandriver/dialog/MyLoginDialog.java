package com.delelong.diandiandriver.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.utils.MD5;

import java.util.List;

/**
 * Created by Administrator on 2016/10/27.
 */

public class MyLoginDialog implements View.OnClickListener{

    private static final String TAG = "BAIDUMAPFORTEST";
    Context context;
    Dialog dialog;
    MyLoginDialogInterface mLoginDialogInterface;
    int mRequestCode;
    String phone, pwd_edt, pwd;
    SharedPreferences preferences;
    public MyLoginDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void dismiss(){
        if (dialog.isShowing()){
            dialog.dismiss();
        }
    }

    ImageView img_cancel;
    EditText edt_phone,edt_pwd;
    Button btn_confirm;
    public void toLogin(){
        Log.i(TAG, "toLogin: 111");
        if (dialog.isShowing()){
            return;
        }
        Log.i(TAG, "toLogin: 222");
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_login);

        img_cancel = (ImageView) window.findViewById(R.id.img_cancel);
        edt_phone = (EditText) window.findViewById(R.id.edt_phone);
        edt_pwd = (EditText) window.findViewById(R.id.edt_pwd);
        btn_confirm = (Button) window.findViewById(R.id.btn_confirm);

        preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        img_cancel.setOnClickListener(this);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = edt_phone.getText().toString();
                pwd_edt = edt_pwd.getText().toString();
                pwd = MD5.getMD5Str(pwd_edt);
                login();
            }
        });
    }
    public void hide(){
        dialog.hide();
    }
    public void show(){
        dialog.show();
    }
    public void toLogin(int requestCode,MyLoginDialogInterface myLoginDialogInterface){
        Log.i(TAG, "toLogin: 111");
        if (dialog.isShowing()){
            return;
        }
        mRequestCode = requestCode;
        mLoginDialogInterface=myLoginDialogInterface;
        Log.i(TAG, "toLogin: 222");
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_login);

        img_cancel = (ImageView) window.findViewById(R.id.img_cancel);
        edt_phone = (EditText) window.findViewById(R.id.edt_phone);
        edt_pwd = (EditText) window.findViewById(R.id.edt_pwd);
        btn_confirm = (Button) window.findViewById(R.id.btn_confirm);

        preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        img_cancel.setOnClickListener(this);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = edt_phone.getText().toString();
                pwd_edt = edt_pwd.getText().toString();
                pwd = MD5.getMD5Str(pwd_edt);
                login();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_cancel:
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    private void login() {
        if (phone.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(context, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (phone.length() != 11 || pwd.length() < 6) {
                Toast.makeText(context, "号码或密码长度不够", Toast.LENGTH_SHORT).show();
                return;
            } else {
                MyHttpUtils myHttpUtils = new MyHttpUtils(context);
                List<String> result = myHttpUtils.login(Str.URL_LOGIN, phone, pwd);
                if (result == null){
                    return;
                }
                if (result.get(0).equals("OK")) {
                    //存储用户(token)、密码(sercet)
                    setPreferences(result);
                    mLoginDialogInterface.hasLogin(mRequestCode,true);
                    dismiss();
                } else if (result.get(0).equals("ERROR")) {
                    Toast.makeText(context, "登陆出错,"+result.get(1), Toast.LENGTH_SHORT).show();
                    return;
                }else if (result.get(0).equals("FAILURE")) {
                    Toast.makeText(context, "登陆失败,"+result.get(1), Toast.LENGTH_SHORT).show();
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

    public interface MyLoginDialogInterface{
        void hasLogin(int requestCode,boolean hasLogin);
    }
}
