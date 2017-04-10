package com.delelong.diandiandriver.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.traver.bean.SystemContact;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 系统工具类
 * Created by Lawrence on 2016-07-18.
 */
public class SystemUtils {

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机Android系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * @return 获取手机序列号
     */
    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return 手机IMEI
     */
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }
        return null;
    }
    /**
     * 调用系统联系人返回的数据
     * @param context
     * @param intent
     * @return
     */
    public static SystemContact getContact(Context context, Intent intent) {
        Cursor cursor = null;
        SystemContact systemContact = null;
        try {
            Uri uri = intent.getData();
            // 得到ContentResolver对象
            ContentResolver cr = context.getContentResolver();
            // 取得电话本中开始一项的光标
            cursor = cr.query(uri, null, null, null, null);
            Log.i(Str.TAG, "backContact: " + cursor.getCount());
            // 向下移动光标
            String contact = null;
            List<String> names = new ArrayList<>();
            List<String> phones = null;
            while (cursor.moveToNext()) {
                // 取得联系人名字
                int nameFieldColumnIndex = cursor
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                contact = cursor.getString(nameFieldColumnIndex);
                names.add(contact);
                // 取得电话号码
                String ContactId = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phone = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + "=" + ContactId, null, null);
                //不只一个电话号码
                phones = new ArrayList<>();
                while (phone.moveToNext()) {
                    String PhoneNumber = phone
                            .getString(phone
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phones.add(PhoneNumber);
//                    Log.i(Str.TAG, "backContact: " + contact + "//" + PhoneNumber);
                }
            }
            systemContact = new SystemContact(contact,phones);
//            for (int i = 0; i < names.size(); i++) {
//                Log.i(Str.TAG, "names:backContact: " + names.get(i));
//            }
//            for (int i = 0; i < phones.size(); i++) {
//                Log.i(Str.TAG, "phones:backContact: " + phones.get(i));
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return systemContact;
    }
}
