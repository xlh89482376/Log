package com.zhidao.logcat.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

/**
 * 对话框工具
 *
 * @author donghongyu
 */
public class DialogUtils {
    /**
     * 提醒开启位置模拟的弹框
     */
    public static void setDialog(final Activity activity) {
        //判断是否开启开发者选项
        new AlertDialog.Builder(activity)
                //这里是表头的内容
                .setTitle("启用位置模拟")
                //这里是中间显示的具体信息
                .setMessage("请在\"开发者选项→选择模拟位置信息应用\"中进行设置")
                //这个string是设置左边按钮的文字
                //setPositiveButton里面的onClick执行的是左边按钮
                .setPositiveButton("设置",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                                    activity.startActivity(intent);
                                } catch (Exception e) {
                                    ToastUtils.displayToast(activity, "无法跳转到开发者选项,请先确保您的设备已处于开发者模式");
                                    e.printStackTrace();
                                }
                            }
                        })
                //这个string是设置右边按钮的文字
                //setNegativeButton里面的onClick执行的是右边的按钮的操作
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                .show();
    }

    /**
     * 显示开启GPS的提示
     */
    public static void showGpsDialog(final Activity activity) {
        new AlertDialog.Builder(activity)
                //这里是表头的内容
                .setTitle("Tips")
                //这里是中间显示的具体信息
                .setMessage("是否开启GPS定位服务?")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activity.startActivityForResult(intent, 0);
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                .show();
    }


    /**
     * 提醒开启悬浮窗的弹框
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setFloatWindowDialog(final Activity activity) {
        new AlertDialog.Builder(activity)
                //这里是表头的内容
                .setTitle("启用悬浮窗")
                //这里是中间显示的具体信息
                .setMessage("使用全局悬浮窗功能，需要打开\"显示悬浮窗\"选项")
                //这个string是设置左边按钮的文字
                //setPositiveButton里面的onClick执行的是左边按钮
                .setPositiveButton("设置",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
                                    activity.startActivity(intent);
                                } catch (Exception e) {
                                    ToastUtils.displayToast(activity, "无法跳转到设置界面，请在权限管理中开启该应用的悬浮窗");
                                    e.printStackTrace();
                                }
                            }
                        })
                //这个string是设置右边按钮的文字
                //setNegativeButton里面的onClick执行的是右边的按钮的操作
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                .show();
    }

}
