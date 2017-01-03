package com.delelong.diandiandriver.menuActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.utils.SystemUtils;
import com.delelong.diandiandriver.webchromeclient.DefaultWebChromeClient;
import com.delelong.diandiandriver.webchromeclient.JavaScriptinterface;
import com.delelong.diandiandriver.webchromeclient.WebChromeClientAboveFive;
import com.google.common.primitives.Doubles;

/**
 * Created by Administrator on 2016/11/3.
 */

public class MyWebViewActivity  extends BaseActivity {
    private static final String TAG = "BAIDUMAPFORTEST";
    WebView webView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_mall);
        initView();
        setUpWeb();
    }
    String url;
    private void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webView = (WebView) findViewById(R.id.web);
        url = getIntent().getStringExtra("url");
    }

    private void setUpWeb() {
        if (url!=null&&!url.equals("")){
            webView.loadUrl(url);
        }else {
            webView.loadUrl(Str.URL_LAW);
        }
        //设置使用webview打开网页,而不是使用默认浏览器打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        WebSettings webSettings = webView.getSettings();
        //设置支持JavaScript
        webSettings.setJavaScriptEnabled(true);
        //设置与js交互接口
        webView.addJavascriptInterface(new JavaScriptinterface(this),"JavaScriptInterface");

        //WebChromeClient添加打开本地文件方法
        String version = SystemUtils.getSystemVersion();
        double num = Doubles.tryParse(version.substring(0, 3));
        if (num >= 5){
            webView.setWebChromeClient(webChromeClientAboveFive = new WebChromeClientAboveFive(this,progressBar) {
                @Override
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                    return super.onShowFileChooser( webView,  valueCallback,  fileChooserParams);
                }
                @Override
                public void onActivityResult(int requestCode,int resultCode, Intent data) {
                    super.onActivityResult(requestCode,resultCode,data);
                }

            });
        }else {
            webView.setWebChromeClient(defaultWebChromeClient = new DefaultWebChromeClient(this,progressBar) {
                @Override
                public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                    super.openFileChooser( uploadMsg);
                }
                @Override
                public void onActivityResult(int requestCode,int resultCode, Intent data) {
                    super.onActivityResult(requestCode,resultCode,data);
                }
            });
        }
    }

    WebChromeClientAboveFive webChromeClientAboveFive;
    DefaultWebChromeClient defaultWebChromeClient;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Str.FILECHOOSER_RESULTCODE){
            String version = SystemUtils.getSystemVersion();
            double num = Doubles.tryParse(version.substring(0, 3));
            if (num >= 5){
                webChromeClientAboveFive.onActivityResult(requestCode,resultCode,data);
            }else {
                defaultWebChromeClient.onActivityResult(requestCode,resultCode,data);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }

    //设置返回键,使得程序返回上一级网页而不是退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView != null) {
                if (webView.canGoBack()) {
                    webView.goBack();
                    return true;
                } else {
                    finish();//退出程序
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}