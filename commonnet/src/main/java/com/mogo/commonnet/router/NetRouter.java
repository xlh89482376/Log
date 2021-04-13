package com.mogo.commonnet.router;

import android.app.Application;

import com.mogo.commonnet.NetSmartRetrofit;
import com.mogo.commonnet.utils.NetContextUtils;

import okhttp3.OkHttpClient;

/**
 * @author xuanlonghua
 * @desc
 * @date 2021/2/7
 */

public class NetRouter {

    /**
     * 初始化上下文以及网络句柄
     *
     * @param application
     */
    public void init(Application application, OkHttpClient.Builder builder, String baseUrl) {
        NetContextUtils.setContext(application);
        NetSmartRetrofit.getInstance().createRetrofit(builder, baseUrl);
    }
}
