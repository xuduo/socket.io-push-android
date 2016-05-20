package com.yy.httpproxy.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xuduo on 11/6/15.
 */
public class PushedNotification {

    public String title;
    public String message;
    public String id;
    public String payload;

    public PushedNotification(String id, JSONObject object) {
        this.id = id;
        title = object.optString("title", "");
        message = object.optString("message", "");
        payload = object.optString("payload", "");
    }

    public PushedNotification(String id, String title, String message, String payload) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.payload = payload;
    }

}
