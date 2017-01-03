# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Administrator\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
    #--3D 地图--
    -keep   class com.amap.api.maps.**{*;}
    -keep   class com.autonavi.amap.mapcore.*{*;}
    -keep   class com.amap.api.trace.**{*;}

    #--定位 --
    -keep class com.amap.api.location.**{*;}
    -keep class com.amap.api.fence.**{*;}
    -keep class com.autonavi.aps.amapapi.model.**{*;}

    #--搜索--
    -keep   class com.amap.api.services.**{*;}

    #--2D地图--
    -keep class com.amap.api.maps2d.**{*;}
    -keep class com.amap.api.mapcore2d.**{*;}

    #--导航--
    -keep class com.amap.api.navi.**{*;}
    -keep class com.autonavi.**{*;}

#     -libraryjars libs/joda-time-2.1.jar
#     -libraryjars libs/jpush-android-2.1.9.jar
#    -libraryjars libs/fastjson-1.2.2.jar
#    -libraryjars libs/guava-19.0.jar
#    -libraryjars libs/xUtils-2.6.14.jar
#    -libraryjars libs/gson-2.5.jar
#    -libraryjars libs/Msc.jar
#    -libraryjars libs/Sunflower.jar
#    -libraryjars libs/armeabi/libgdinamapv4sdk752.so
#    -libraryjars libs/armeabi/libgdinamapv4sdk752ex.so
#    -libraryjars libs/armeabi/libjpush219.so
    -libraryjars libs/armeabi/libmsc.so
#    -libraryjars libs/armeabi/libtbt3631.so
#    -libraryjars libs/armeabi/libwtbt144.so

    -keep public class * extends android.app.Fragment
    -keep public class * extends android.app.Activity
    -keep public class * extends android.app.Application
    -keep public class * extends android.app.Service
    -keep public class * extends android.app.IntentService
    -keep public class * extends android.content.BroadcastReceiver
    -keep public class * extends android.content.ContentProvider
    -keep public class * extends android.app.backup.BackupAgentHelper
    -keep public class * extends android.preference.Preference
    -keep public class * extends android.support.v4.**
    -keep public class com.android.vending.licensing.ILicensingService
    -keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }
    -keep class * implements android.os.Serializable

    -keepclasseswithmembernames class * {
        native <methods>;
    }

    -keepclasseswithmembernames class * {
        public <init>(android.content.Context, android.util.AttributeSet);
    }

    -keepclasseswithmembernames class * {
        public <init>(android.content.Context, android.util.AttributeSet, int);
    }

    -keepclassmembers enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
    }

    -keepclasseswithmembers class * {
        public <init>(android.content.Context);
    }

    -dontshrink
    -dontoptimize
    -dontwarn com.google.android.maps.**
    -dontwarn android.webkit.WebView
    -dontwarn com.umeng.**
    -dontwarn com.tencent.weibo.sdk.**
    -dontwarn com.facebook.**
    -dontpreverify

    -dontwarn cn.jpush.**
    -keep class cn.jpush.** { *; }

    -keep enum com.facebook.**
    -keepattributes Exceptions,InnerClasses,Signature
    -keepattributes *Annotation*
    -keepattributes SourceFile,LineNumberTable

    -dontwarn android.support.v4.**
    -dontwarn org.apache.commons.net.**