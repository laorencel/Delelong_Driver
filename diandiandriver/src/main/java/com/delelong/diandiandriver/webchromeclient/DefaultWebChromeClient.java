package com.delelong.diandiandriver.webchromeclient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.delelong.diandiandriver.bean.Str;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2016/9/20.
 */
public class DefaultWebChromeClient extends BaseWebChromeClient {

    private final static String TAG = "BAIDUMAPFORTEST";
    private ValueCallback<Uri> mUploadMessage;
    private Activity mActivity;
    ProgressBar progressBar;

    public DefaultWebChromeClient(Activity activity, ProgressBar progressBar) {
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


    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "", "");
    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
        openFileChooser(uploadMsg, acceptType, "");
    }

    //For Android 4.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        mUploadMessage = uploadMsg;
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.addCategory(Intent.CATEGORY_OPENABLE);
//        i.setType("image/*");
//        if (!mActivity.isFinishing()){
//            mActivity.startActivityForResult(Intent.createChooser(i, "File Chooser"), Str.FILECHOOSER_RESULTCODE);
//        }
        mActivity.startActivityForResult(createDefaultOpenableIntent(),
                Str.FILECHOOSER_RESULTCODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Str.FILECHOOSER_RESULTCODE) {
            Log.i(TAG, "onActivityResult:data: "+data);
//            if (null == mUploadMessage) {
//                Log.i(TAG, "onActivityResult: null == mUploadMessage");
//                return;
//            }
//            Uri result = data == null || resultCode != Activity.RESULT_OK ? null
//                    : data.getData();
//            Log.i(TAG, "onActivityResult:result: "+result);
//            if (mUploadMessage != null) {
//                mUploadMessage.onReceiveValue(result);
//                mUploadMessage = null;
//            }

            if (null == mUploadMessage) return;
            Uri result = data == null || resultCode != Activity.RESULT_OK ? null
                    : data.getData();
            if (result == null && data == null && resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "onActivityResult: null");
//                File cameraFile = new File(mCameraFilePath);
                File externalDataDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM);
                File cameraDataDir = new File(externalDataDir.getAbsolutePath() +
                        File.separator + "diandian-photos");
                cameraDataDir.mkdirs();
                String mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator +
                        "photo" + ".jpg";
                File cameraFile = new File(mCameraFilePath);
                if (cameraFile.exists()) {
//                    result = Uri.fromFile(cameraFile);
                    try {
                        Log.i(TAG, "onActivityResult: cameraFile.exists()");
                        result = Uri.parse(MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), cameraFile.getAbsolutePath(), "", ""));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    // Broadcast to the media scanner that we have a new photo
                    // so it will be added into the gallery for the user.
                    mActivity.sendBroadcast(
                            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
                }
            }
            Log.i(TAG, "onActivityResult: "+result);
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }
}