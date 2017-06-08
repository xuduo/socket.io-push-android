package com.yy.httpproxy.subscribe;

import android.content.Context;

import com.yy.httpproxy.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by xuduo on 10/20/15.
 */
public class RandomPushIdGenerator {

    private static final String TAG = "RandomPushIdGenerator";

    public String generatePushId(Context context) {
        CachedSharedPreference cachedSharedPreference = new CachedSharedPreference(context);
        String pushId = cachedSharedPreference.get("pushId");
        Log.i(TAG, "read pushId from sharePref " + pushId);
        if (pushId == null) {
            File pushIdFile = null;
            try {
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/" + context.getPackageName());
                dir.mkdirs();
                pushIdFile = new File(dir, "pushId");
                pushId = getStringFromFile(pushIdFile);
                Log.i(TAG, "read pushId from file " + pushId);
                cachedSharedPreference.save("pushId", pushId);
            } catch (Exception e) {
                Log.e(TAG, "generatePushId exception ", e);
            }
            if (pushId == null) {
                pushId = new BigInteger(130, new SecureRandom()).toString(32);
                cachedSharedPreference.save("pushId", pushId);
                if (pushIdFile != null) {
                    Log.i(TAG, "write pushId to file " + pushId);
                    writeToFile(pushId, pushIdFile);
                }
            }
        }
        return pushId;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(File file) throws Exception {
        if (!file.exists()) {
            return null;
        }
        FileInputStream fin = new FileInputStream(file);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }


    private void writeToFile(String data, File file) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
