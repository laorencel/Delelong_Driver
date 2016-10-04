package com.delelong.diandiandriver.webchromeclient;

import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by Administrator on 2016/9/20.
 */
public class BaseWebChromeClient extends WebChromeClient {


    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
    }

    //For Android 4.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
    }
    //For Android 5.0+
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
