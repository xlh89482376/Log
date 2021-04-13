package com.zhidao.logcommon;

import android.app.Application;
import android.content.Context;

import com.mogo.commonnet.sdk.NetApi;
import com.mogo.commonnet.utils.NetContextUtils;
import com.mogo.commonnet.utils.NetHttpsUtils;
import com.zhidao.logcommon.debug.DebugConfig;
import com.zhidao.logcommon.utils.logger.Logger;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author Xuanlh
 * @desc
 * @date 2021/2/7
 */

public class AppLogContext {

    private static final String TAG = "AppLogContext";

    public static AppLogContext appLogContext;

    public static AppLogContext  getInstance() {
        if (appLogContext == null) {
            appLogContext = new AppLogContext();
        }
        return appLogContext;
    }

    /**
     * 初始化
     */
    public void init(Context context) {
        NetContextUtils.setContext(context);
        initNetwork();
    }

    private void initNetwork() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        httpClientBuilder
                .connectTimeout(20, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(20, TimeUnit.SECONDS) // 设置读取超时时间
                .writeTimeout(20, TimeUnit.SECONDS) // 设置写入超时时间
                .sslSocketFactory(NetHttpsUtils.getSslSocketFactory(null, null, null).sSLSocketFactory,   // 添加证书
                        NetHttpsUtils.getSslSocketFactory(null, null, null).trustManager)
                .build();

        Logger.d(TAG, "httpClientBuilder: " + httpClientBuilder);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(s -> {
                // retrofit日志
                Logger.i("RetrofitLog", "retrofitBack = " + s);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggingInterceptor);
        }
        Logger.d(TAG, "HostUrl:" + DebugConfig.getHostUrl());
        Logger.d(TAG, "Context:" + NetContextUtils.getContext());
        NetApi.getInstance().init((Application) NetContextUtils.getContext(), httpClientBuilder, DebugConfig.getHostUrl());
    }
}
