package com.mogo.commonnet.sdk;

import android.app.Application;

import com.mogo.commonnet.router.NetRouter;

import okhttp3.OkHttpClient;

/**
 * @author xuanlonghua
 * @desc
 * @date 2021/2/7
 */

public class NetApi {

    private final NetRouter router;

    private static NetApi api;

    public NetApi() {
        router = new NetRouter();
    }

    public static NetApi getInstance() {
        if (api == null) {
            synchronized (NetApi.class) {
                if (api == null) {
                    api = new NetApi();
                }
            }
        }
        return api;
    }

    /**
     * 初始化
     *
     * @param application
     * @param builder
     * @param baseUrl 全局配置基础url 也可build（）新基础url
     */
    public void init(Application application, OkHttpClient.Builder builder, String baseUrl) {
        router.init(application, builder, baseUrl);
    }
}
