package com.zhidao.logcat.utils;

import android.content.Context;

import com.zhidao.logcat.service.LogcatViewerFloatingView;

import wei.mark.standout.StandOutWindow;

public class LogcatViewer {

    public static void showLogcatLoggerView(Context context){
        StandOutWindow.closeAll(context, LogcatViewerFloatingView.class);
        StandOutWindow.show(context, LogcatViewerFloatingView.class, StandOutWindow.DEFAULT_ID);
    }

    public static void closeLogcatLoggerView(Context context) {
        StandOutWindow.closeAll(context, LogcatViewerFloatingView.class);
    }
}
