package com.zhidao.logcommon.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Xuanlh
 * @desc
 * @date 2021/2/7
 */

public class BaseTimeUtils {

    /**
     * 格式化日期
     *
     * @return
     */
    public static String[] formatTimeDate() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm");
        String time = sdf.format(d);
        if (!TextUtils.isEmpty(time) && time.contains("-")) {
            String[] timeSplit = time.split("-");
            return timeSplit;
        }
        return null;
    }

    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public static String formatTimeDate1(long time) {
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public static String formatTimeDate(long time, String format) {
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 格式化时间
     *
     * @return
     */
    public static String formatTimeDate2() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static String formatTimeDate3() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
