package com.zhidao.logcat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.alibaba.android.arouter.launcher.ARouter;
import com.zhidao.logcommon.AppLogContext;
import com.zhidao.logcommon.BuildConfig;
import com.zhidao.logcommon.debug.DebugConfig;
import com.zhidao.logcommon.utils.logger.LogLevel;
import com.zhidao.logcommon.utils.logger.Logger;

import java.util.LinkedList;
import java.util.List;

public class AbsLogApplication extends BaseApplication {

    private static Application sApp;
    private static Context mContext;

    public static Application getApp() {return sApp;}

    public static Context getContext() {return mContext;}

    private AbsLogApplication instance;
    private List<Activity> activities;
    private String globalVar;

    @Override
    public void onCreate() {
        super.onCreate();
        DebugConfig.setNetMode(com.zhidao.logcat.BuildConfig.NET_ENV);
        // 目前只有初始化network
        AppLogContext.getInstance().init(this);
        activities = new LinkedList<Activity>();
        sApp = this;
        // 初始化ARouter
        initARouter();
        // 初始化logger，DEBUG模式开启
        Logger.init( com.zhidao.logcommon.BuildConfig.DEBUG ? LogLevel.VERBOSE : LogLevel.OFF );
        // 获取全局context
        mContext = getApplicationContext();
    }

    private void initARouter() {
        ARouter.init( sApp );
        // 初始化 arouter
        if ( BuildConfig.DEBUG ) {
            ARouter.openDebug();
            ARouter.openLog();
        }
    }

    public AbsLogApplication getInstance() {
        if (instance == null) {
            return new AbsLogApplication();
        }
        return instance;
    }

    /**
     * 遍历所有Activity并finish
     */
    public void finishActivity() {
        for (Activity activity : activities) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public String getGlobalVar() {
        return globalVar;
    }

    public void setGlobalVar(String globalVar) {
        this.globalVar = globalVar;
    }

    /**
     * 作为onLowMemory的一个特定于应用程序的替代选择，在android4.0时引入，
     * 在程序运行时决定当前应用程序应该尝试减少其内存开销时（通常在它进入后台时）调用
     * 它包含一个level参数，用于提供请求的上下文
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
    /**
     * 与Activity不同，在配置改变时，应用程序对象不会被终止和重启。
     * 如果应用程序使用的值依赖于特定的配置，则重写这个方法来重新加载这些值，或者在应用程序级别处理这些值的改变
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    /**
     * 当系统处于资源匮乏时，具有良好行为的应用程序可以释放额外的内存。
     * 这个方法一般只会在后台进程已经终止，但是前台应用程序仍然缺少内存时调用。
     * 我们可以重写这个程序来清空缓存或者释放不必要的资源
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}
