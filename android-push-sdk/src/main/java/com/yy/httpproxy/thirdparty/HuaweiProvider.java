package com.yy.httpproxy.thirdparty;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.yy.httpproxy.util.Log;
import com.yy.httpproxy.util.ServiceCheckUtil;
import com.yy.httpproxy.util.Version;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/4/29.
 */
public class HuaweiProvider implements NotificationProvider {

    public final static String TAG = "HuaweiProvider";
    private String token;

    public HuaweiProvider(Context context) {
        Log.i("HuaweiProvider", "init");
        HuaweiCallback callback = new HuaweiCallback(context);
    }


    public static boolean available(Context context) {
        try {
            return Class.forName("com.huawei.hms.update.provider.UpdateProvider") != null
                    && ServiceCheckUtil.isBroadcastReceiverAvailable(context, HuaweiReceiver.class) && isHmsAvailable(context) && EMUIValid();
        } catch (Throwable e) {
            Log.e(TAG, "available ", e);
            return false;
        }
    }

    public static boolean EMUIValid() {
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            String buildVersion = (String) getMethod.invoke(classType, new Object[]{"ro.build.version.emui"});
            buildVersion = buildVersion.replaceAll("EmotionUI_", "");
            Version ver = new Version(buildVersion);
            Log.i(TAG, "EMUI " + buildVersion);
            return ver.compareTo(new Version("5.0")) >= 0; // 5.0以下emui 有各种问题
        } catch (Exception e) {
            Log.e(TAG, "getEMUI ", e);
            return false;
        }
    }

    private static boolean isHmsAvailable(Context context) {

        PackageManager pm = context.getPackageManager();

        try {

            PackageInfo pi = pm.getPackageInfo("com.huawei.hwid", 0);

            if (pi != null) {

                Log.i(TAG, "com.huawei.hwid code " + pi.versionCode + " ,version " + pi.versionName);
                return pi.versionCode >= 241300;
            }

        } catch (Exception e) {

            Log.e(TAG, "isHmsAvailable ", e);

        }
        return false;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getType() {
        return "huawei";
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

}
