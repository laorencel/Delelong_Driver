package com.delelong.diandiandriver.webchromeclient;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.delelong.diandiandriver.bean.Str;

/**
 * Created by Administrator on 2016/9/20.
 */
public class WebChromeClientAboveFive extends BaseWebChromeClient {

    private ValueCallback<Uri[]> mUploadCallbackAboveFive;
    private Activity mActivity;
    ProgressBar progressBar;

    public WebChromeClientAboveFive(Activity activity, ProgressBar progressBar) {
        this.mActivity = activity;
        this.progressBar = progressBar;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress == 100) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(newProgress);
        }
        super.onProgressChanged(view, newProgress);
    }

    /**
     * 兼容5.0及以上
     *
     * @param webView
     * @param valueCallback
     * @param fileChooserParams
     * @return
     */
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, android.webkit.WebChromeClient.FileChooserParams fileChooserParams) {
        mUploadCallbackAboveFive = valueCallback;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        if (!mActivity.isFinishing()){
            mActivity.startActivityForResult(Intent.createChooser(i, "File Chooser"), Str.FILECHOOSER_RESULTCODE);
        }
        return true;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Str.FILECHOOSER_RESULTCODE) {
            if (null == mUploadCallbackAboveFive) {
                return;
            }
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String dataString = data.getDataString();
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        int itemCount = clipData.getItemCount();
                        results = new Uri[itemCount];
                        for (int i = 0; i < itemCount; i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            if (mActivity!=null&&!mActivity.isFinishing()){
                mUploadCallbackAboveFive.onReceiveValue(results);
            }
            mUploadCallbackAboveFive = null;
            return;
        }
    }
}
