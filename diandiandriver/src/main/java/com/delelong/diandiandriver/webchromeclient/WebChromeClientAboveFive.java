package com.delelong.diandiandriver.webchromeclient;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileNotFoundException;

import static com.delelong.diandiandriver.bean.Str.FILECHOOSER_RESULTCODE;

/**
 * Created by Administrator on 2016/9/20.
 */
public class WebChromeClientAboveFive extends BaseWebChromeClient {

    private static final String TAG = "BAIDUMAPFOTTEST";
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
//        mUploadCallbackAboveFive = valueCallback;
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.addCategory(Intent.CATEGORY_OPENABLE);
//        i.setType("*/*");
//        if (!mActivity.isFinishing()){
//            mActivity.startActivityForResult(Intent.createChooser(i, "File Chooser"), Str.FILECHOOSER_RESULTCODE);
//        }

//        if (mUploadCallbackAboveFive != null) {
//            mUploadCallbackAboveFive.onReceiveValue(null);
//            mUploadCallbackAboveFive = null;
//        }
//        mUploadCallbackAboveFive = valueCallback;
//        Intent intent = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            Log.i(TAG, "onShowFileChooser: ");
//            intent = fileChooserParams.createIntent();
//        }
//        try {
//            if (intent != null) {
//                mActivity.startActivityForResult(intent, Str.FILECHOOSER_RESULTCODE);
//            }
//        } catch (ActivityNotFoundException e) {
//            mUploadCallbackAboveFive = null;
//            Log.i(TAG, "Cannot Open File Chooser: ");
//            return false;
//        }

        mUploadCallbackAboveFive = valueCallback;
        mActivity.startActivityForResult(createDefaultOpenableIntent(),
                FILECHOOSER_RESULTCODE);
        return true;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
//            if (null == mUploadCallbackAboveFive) {
//                return;
//            }
//            Uri[] results = null;
//            Log.i(TAG, "onActivityResult: 111");
//            if (resultCode == Activity.RESULT_OK) {
//                if (data != null) {
//                    Log.i(TAG, "onActivityResult: 222");
//                    String dataString = data.getDataString();
//                    ClipData clipData = data.getClipData();
//                    if (clipData != null) {
//                        Log.i(TAG, "onActivityResult: 333");
//                        int itemCount = clipData.getItemCount();
//                        results = new Uri[itemCount];
//                        for (int i = 0; i < itemCount; i++) {
//                            ClipData.Item item = clipData.getItemAt(i);
//                            results[i] = item.getUri();
//                            Log.i(TAG, "onActivityResult: 444");
//                        }
//                    }
//                    if (dataString != null) {
//                        Log.i(TAG, "onActivityResult: 555");
//                        results = new Uri[]{Uri.parse(dataString)};
//                    }
//                }
//            }
//            if (mActivity != null && !mActivity.isFinishing()) {
//                mUploadCallbackAboveFive.onReceiveValue(results);
//            }
//            mUploadCallbackAboveFive = null;
//            return;


            if (null == mUploadCallbackAboveFive) return;
            Log.i(TAG, "onActivityResult:111 " + data);
            Uri[] result = new Uri[1];
            result[0] = data == null || resultCode != Activity.RESULT_OK ? null
                    : data.getData();
            Log.i(TAG, "onActivityResult:222 " + result + "//" + result[0]);
            if ((result[0] == null && (data == null) || data.getExtras() == null) && resultCode == Activity.RESULT_OK) {
                File externalDataDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM);
                File cameraDataDir = new File(externalDataDir.getAbsolutePath() +
                        File.separator + "diandian-photos");
                cameraDataDir.mkdirs();
                String mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator +
                        "photo" + ".jpg";
                File cameraFile = new File(mCameraFilePath);
                if (cameraFile.exists()) {
//                    result[0] = Uri.fromFile(cameraFile);
                    try {
                        result[0] = Uri.parse(MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), cameraFile.getAbsolutePath(), "", ""));
                        Log.i(TAG, "onActivityResult:333 " + result[0]);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    // Broadcast to the media scanner that we have a new photo
                    // so it will be added into the gallery for the user.
                    mActivity.sendBroadcast(
                            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result[0]));
                }
            }
            if (result != null && result[0] != null) {
                Log.i(TAG, "onActivityResult:555 " + result + result[0]);
                mUploadCallbackAboveFive.onReceiveValue(result);
            }
            mUploadCallbackAboveFive = null;
            return;
        }
    }
}
