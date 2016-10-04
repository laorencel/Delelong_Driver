package com.delelong.diandiandriver;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.delelong.diandiandriver.fragment.ForgotFrag;
import com.delelong.diandiandriver.fragment.LoginFrag;
import com.delelong.diandiandriver.fragment.RegisterActivity;

/**
 * Created by Administrator on 2016/8/18.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "BAIDUMAPFORTEST";
    FragmentManager fragmentManager;
    LoginFrag loginFrag;
    ForgotFrag forgotFrag;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        initView();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initView() {
        fragmentManager = getFragmentManager();
        loginFrag = new LoginFrag();
        forgotFrag = new ForgotFrag();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.rl1,loginFrag,"loginFrag")
                .show(loginFrag)
                .commit();

    }
    TextView tv_forgotPwd;
    TextView tv_register;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onResume() {
        super.onResume();
        //忘记密码
        tv_forgotPwd = (TextView) loginFrag.getView().findViewById(R.id.tv_forgotPwd);
        tv_forgotPwd.setOnClickListener(this);

        //注册
        tv_register = (TextView) loginFrag.getView().findViewById(R.id.tv_register);
        tv_register.setOnClickListener(this);
    }

//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_forgotPwd:
                fragmentManager.beginTransaction()
//                        .setCustomAnimations(R.anim.frag_in,0,0,R.anim.frag_out)
                        .add(R.id.rl2,forgotFrag,"modifyFrag")
                        .hide(loginFrag)
                        .show(forgotFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.tv_register:
//                fragmentManager.beginTransaction()
////                        .setCustomAnimations(R.anim.frag_in,0,0,R.anim.frag_out)
//                        .add(R.id.rl3,registerFrag,"registerFrag")
//                        .hide(loginFrag)
//                        .hide(forgotFrag)
//                        .show(registerFrag)
//                        .addToBackStack(null)
//                        .commit();
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }



}
