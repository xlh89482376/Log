package com.mogo.commonnet.utils;

import android.content.Context;

/**
 * @author xuanlonghua
 * @desc 上下文管理
 * @date 2021/2/7
 */

public class NetContextUtils {

    /**
     * 上下文
     */
    private static Context mContext;

    /**
     * 设置上下文
     *
     * @param context
     */
    public static void setContext(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 获取上下文
     *
     * @return
     */
    public static Context getContext() {
        return mContext;
    }
}
