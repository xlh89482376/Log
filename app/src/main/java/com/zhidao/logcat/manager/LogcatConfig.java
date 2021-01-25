package com.zhidao.logcat;

import android.content.Context;
import android.content.SharedPreferences;

final class LogcatConfig {

    private static SharedPreferences sConfig;

    /**
     * 初始化
     */
    static void init(Context context) {
        sConfig = context.getSharedPreferences("logcat", Context.MODE_PRIVATE);
    }

    /**
     * 日志过滤等级
     */
    private static final String LOGCAT_LEVEL = "logcat_level";

    static String getLogcatLevel() {
        if (sConfig != null) {
            return sConfig.getString(LOGCAT_LEVEL, "V");
        } else {
            return "V";
        }
    }

    /**
     * 设置log级别
     */
    static void setLogcatLevel(String level) {
        if (sConfig != null) {
            sConfig.edit().putString(LOGCAT_LEVEL, level).apply();
        }
    }

    /**
     * 搜索关键字
     */
    private static final String LOGCAT_TEXT = "logcat_text";

    static String getLogcatText() {
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
    static void setLogcatText(String keyword) {
        if (sConfig != null) {
            sConfig.edit().putString(LOGCAT_TEXT, keyword).apply();
        }
    }
}