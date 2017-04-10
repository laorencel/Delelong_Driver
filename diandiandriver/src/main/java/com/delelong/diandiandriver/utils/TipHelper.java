package com.delelong.diandiandriver.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;


/**
 * Created by Administrator on 2016/10/16.
 */

/**
 * 信息提示类
 */
public class TipHelper {
    private static final String TAG = "BAIDUMAPFORTEST";
    SharedPreferences preferences;
    Activity activity;
    SpeechSynthesizer mTts;
    Context context;

    public TipHelper(Activity activity) {
        this.activity = activity;
        context = activity;
        preferences = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
        vibrateEnable = preferences.getBoolean("vibrate", false);
        isVoiceEnable = preferences.getBoolean("voice", true);
        initSpeech();
    }

    private void initSpeech() {
        SpeechUtility.createUtility(MyApp.getInstance(), SpeechConstant.APPID + "=5806cdd4," + SpeechConstant.FORCE_LOGIN + "=true");
        mTts = SpeechSynthesizer.createSynthesizer(MyApp.getInstance(), null);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "55");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "100");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
    }

    boolean isVoiceEnable;

    public void speak(String content) {
        if (isVoiceEnable) {
//        mTts.startSpeaking("科大讯飞，让世界聆听我们的声音", mSynListener);
            if (mTts == null) {
                return;
            }
//            Log.i(TAG, "speak: content");
            mTts.startSpeaking(content, mSynListener);
        }
    }

    public void stopSpeak() {
        if (mTts != null && mTts.isSpeaking()) {
            mTts.stopSpeaking();
        }
    }

    boolean vibrateEnable;

    /**
     * @param milliseconds 震动的时长，单位是毫秒
     */
    public void Vibrate(long milliseconds) {
        if (vibrateEnable) {
            Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(milliseconds);
        }
    }

    /**
     * @param pattern  自定义震动模式 。数组中数字的含义依次是静止的时长，震动时长，静止时长，震动时长。。。时长的单位是毫秒
     * @param isRepeat 是否反复震动，如果是true，反复震动，如果是false，只震动一次
     */
    public void Vibrate(long[] pattern, boolean isRepeat) {
        if (vibrateEnable) {
            Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(pattern, isRepeat ? 1 : -1);
        }
    }

    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
        }

        //暂停播放
        public void onSpeakPaused() {
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };

}
