package com.delelong.diandiandriver.utils.umengShare;

import android.app.Activity;
import android.util.Log;

import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.youmengshare.CustomShareListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

/**
 * Created by Administrator on 2017/2/19.
 */

public class UMengShareUtils {

    Activity mActivity;
    CustomShareListener mCustomShareListener;
    ShareAction mShareAction;

    public UMengShareUtils(Activity mActivity) {
        this.mActivity = mActivity;
        this.mCustomShareListener = new CustomShareListener(mActivity);
    }

    public ShareAction shareUMWeb(final UMWeb uMWeb, SHARE_MEDIA... list){
//        mShareAction = new ShareAction(mActivity).setDisplayList(
//                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE,
//                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
//                SHARE_MEDIA.ALIPAY, SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN,
//                SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL, SHARE_MEDIA.YNOTE,
//                SHARE_MEDIA.EVERNOTE, SHARE_MEDIA.LAIWANG, SHARE_MEDIA.LAIWANG_DYNAMIC,
//                SHARE_MEDIA.LINKEDIN, SHARE_MEDIA.YIXIN, SHARE_MEDIA.YIXIN_CIRCLE,
//                SHARE_MEDIA.TENCENT, SHARE_MEDIA.FACEBOOK, SHARE_MEDIA.TWITTER,
//                SHARE_MEDIA.WHATSAPP, SHARE_MEDIA.GOOGLEPLUS, SHARE_MEDIA.LINE,
//                SHARE_MEDIA.INSTAGRAM, SHARE_MEDIA.KAKAO, SHARE_MEDIA.PINTEREST,
//                SHARE_MEDIA.POCKET, SHARE_MEDIA.TUMBLR, SHARE_MEDIA.FLICKR,
//                SHARE_MEDIA.FOURSQUARE, SHARE_MEDIA.MORE)
//                .addButton("umeng_sharebutton_copy", "umeng_sharebutton_copy", "umeng_socialize_copy", "umeng_socialize_copy")
//                .addButton("umeng_sharebutton_copyurl", "umeng_sharebutton_copyurl", "umeng_socialize_copyurl", "umeng_socialize_copyurl")
        mShareAction = new ShareAction(mActivity).setDisplayList(list)
        .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
//                        if (snsPlatform.mShowWord.equals("umeng_sharebutton_copy")) {
//                            Toast.makeText(MyTuiJianActivity.this, "复制文本按钮", Toast.LENGTH_LONG).show();
//                        } else if (snsPlatform.mShowWord.equals("umeng_sharebutton_copyurl")) {
//                            Toast.makeText(MyTuiJianActivity.this, "复制链接按钮", Toast.LENGTH_LONG).show();
//
//                        } else {
                        Log.i(Str.TAG, "onclick:snsPlatform: "+snsPlatform+"share_media:"+share_media);
                            new ShareAction(mActivity)
                                    .withMedia(uMWeb)
                                    .setPlatform(share_media)
                                    .setCallback(mCustomShareListener)
                                    .share();
//                        }
                    }
                });
        return mShareAction;
    }
    public void open(){
        Log.i(Str.TAG, "onclick:snsPlatform: 111");
        if (mShareAction!=null){
            Log.i(Str.TAG, "onclick:snsPlatform: 222");
            mShareAction.open();
        }
    }

}
