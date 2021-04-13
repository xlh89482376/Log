package com.zhidao.logcat;

import android.app.Application;

import com.mogo.commonnet.utils.NetContextUtils;

/**
 * @author Xuanlh
 * @desc
 * @date 2021/2/10
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NetContextUtils.setContext(this);
    }
}
