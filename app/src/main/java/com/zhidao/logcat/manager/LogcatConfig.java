package com.zhidao.logcat.manager;

import android.content.Context;
import android.content.SharedPreferences;

public final class LogcatConfig {

    private static SharedPreferences sConfig;

    /**
     * 初始化
     */
    public static void init(Context context) {
        sConfig = context.getSharedPreferences("logcat", Context.MODE_PRIVATE);
    }

    /**
     * 日志过滤等级
     */
    private static final String LOGCAT_LEVEL = "logcat_level";

    public static String getLogcatLevel() {
        if (sConfig != null) {
            return sConfig.getString(LOGCAT_LEVEL, "V");
        } else {
            return "V";
        }
    }

//    public static String getLogcatLevel() {
//        SharedPrefsMgr.getInstance(AbsLogApplication.getContext()).getString(LOGCAT_LEVEL, "V");
//    }

    /**
     * 设置log级别
     */
    public static void setLogcatLevel(String level) {
        if (sConfig != null) {
            sConfig.edit().putString(LOGCAT_LEVEL, level).apply();
        }
    }

    /**
     * 搜索关键字
     */
    private static final String LOGCAT_TEXT = "logcat_text";

    public static String getLogcatText() {
        if(sConfig != null) {
            return sConfig.getString(LOGCAT_TEXT, "");
        } else {
            return "";
        }
    }

    /**
     * 设置搜索关键字
     * @param keyword
     */
    public static void setLogcatText(String keyword) {
        if (sConfig != null) {
            sConfig.edit().putString(LOGCAT_TEXT, keyword).apply();
        }
    }
}