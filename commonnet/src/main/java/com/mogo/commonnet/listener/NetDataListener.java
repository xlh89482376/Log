package com.mogo.commonnet.listener;

/**
 * @author: xuanlonghua
 * @date: 2021/2/7
 * @version: 1.0.0
 * @description:
 */

public interface NetDataListener<T> {

    /**
     * 开始加载网络请求
     */
    void start();

    /**
     * 数据回掉
     *
     * @param t
     */
    void onSuccess(T t);

    /**
     * 异常
     *
     * @param throwable
     */
    void onError(Throwable throwable);

    /**
     * 结束网络请求
     */
    void onComplete();
}
