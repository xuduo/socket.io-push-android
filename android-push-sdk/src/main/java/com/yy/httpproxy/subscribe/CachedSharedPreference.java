package com.yy.httpproxy.subscribe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class CachedSharedPreference {

    private SharedPreferences preferences;
    private Map<String, String> cache = new HashMap<>();

    public CachedSharedPreference(Context context) {
        preferences = context.getSharedPreferences("SharedPreferencePushGenerator", Context.MODE_PRIVATE);
    }

    public void save(String key, String value) {
        cache.put(key, value);
        SharedPreferences.Editor editor = preferences.edit().putString(key, value);
        if (Build.VERSION.SDK_INT > 8) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public Set<String> getStringSet(String key) {
        return preferences.getStringSet(key, new HashSet<String>());
    }

    public void addStringSet(String key, String value) {
        Set<String> values = getStringSet(key);
        values.add(value);
        preferences.edit().putStringSet(key, values).commit();
    }

    public String get(String key) {
        String value = cache.get(key);
        if (value == null) {
            value = preferences.getString(key, null);
        }
        return value;
    }
}
