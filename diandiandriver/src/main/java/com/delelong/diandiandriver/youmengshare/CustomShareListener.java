package com.delelong.diandiandriver.youmengshare;

import android.app.Activity;

import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/2/17.
 */

public class CustomShareListener implements UMShareListener {

    private WeakReference<Activity> mActivity;

    public CustomShareListener(Activity activity) {
        mActivity = new WeakReference(activity);
    }

    @Override
    public void onStart(SHARE_MEDIA platform) {

    }

    @Override
    public void onResult(SHARE_MEDIA platform) {

//        android.util.Log.i(Str.TAG, "CustomShareListener:onResult: "+platform);
//        if (platform.name().equals("WEIXIN_FAVORITE")) {
//            Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
//        } else {
//            if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
//                    && platform != SHARE_MEDIA.EMAIL
//                    && platform != SHARE_MEDIA.FLICKR
//                    && platform != SHARE_MEDIA.FOURSQUARE
//                    && platform != SHARE_MEDIA.TUMBLR
//                    && platform != SHARE_MEDIA.POCKET
//                    && platform != SHARE_MEDIA.PINTEREST
//
//                    && platform != SHARE_MEDIA.INSTAGRAM
//                    && platform != SHARE_MEDIA.GOOGLEPLUS
//                    && platform != SHARE_MEDIA.YNOTE
//                    && platform != SHARE_MEDIA.EVERNOTE) {
//                Toast.makeText(mActivity.get(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
//            }
//
//        }
    }

    @Override
    public void onError(SHARE_MEDIA platform, Throwable t) {
//        android.util.Log.i(Str.TAG, "CustomShareListener:onError: "+platform);
//        if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
//                && platform != SHARE_MEDIA.EMAIL
//                && platform != SHARE_MEDIA.FLICKR
//                && platform != SHARE_MEDIA.FOURSQUARE
//                && platform != SHARE_MEDIA.TUMBLR
//                && platform != SHARE_MEDIA.POCKET
//                && platform != SHARE_MEDIA.PINTEREST
//
//                && platform != SHARE_MEDIA.INSTAGRAM
//                && platform != SHARE_MEDIA.GOOGLEPLUS
//                && platform != SHARE_MEDIA.YNOTE
//                && platform != SHARE_MEDIA.EVERNOTE) {
//            Toast.makeText(mActivity.get(), platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
//            if (t != null) {
//                Log.i(Str.TAG, "onError: "+t.getMessage());
////                Log.d("throw", "throw:" + t.getMessage());
//            }
//        }

    }

    @Override
    public void onCancel(SHARE_MEDIA platform) {
//        android.util.Log.i(Str.TAG, "CustomShareListener:onCancel: "+platform);
//        Toast.makeText(mActivity.get(), platform + " 分享取消了", Toast.LENGTH_SHORT).show();
    }
}