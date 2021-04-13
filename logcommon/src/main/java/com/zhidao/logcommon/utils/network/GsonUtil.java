package com.zhidao.logcommon.utils.network;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xuanlonghua
 * @date: 2021/2/3
 * @version: 1.0.0
 * @description:
 */

public class GsonUtil {

    private static volatile Gson gson;

    private GsonUtil() {}

    public static Gson getGson() {
        if(gson == null){
            synchronized (GsonUtil.class) {
                if (gson == null) {
                    GsonBuilder builder = new GsonBuilder();
                    gson = builder.create();
                }
            }
        }
        return gson;
    }

    public static String jsonFromObject( Object object) {
        if (object == null) {
            return null;
        } else {
            try {
                return getGson().toJson(object);
            } catch ( Exception var2) {
                var2.printStackTrace();
                return null;
            }
        }
    }

    public static <T> T objectFromJson( String json, Class<T> klass) {
        if (json == null) {
            return null;
        } else {
            try {
                return getGson().fromJson(json, klass);
            } catch ( Exception var3) {
                var3.printStackTrace();
                return null;
            }
        }
    }


    public static <T> List<T> arrayFromJson(String json, Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        if ( TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            org.json.JSONArray array = new org.json.JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String js = object.toString();
                T t = GsonUtil.objectFromJson(js, clazz);
                list.add(t);
            }
            return list;
        } catch ( Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
