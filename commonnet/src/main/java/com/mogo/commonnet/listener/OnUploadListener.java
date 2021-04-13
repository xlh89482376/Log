package com.mogo.commonnet.listener;

/**
 * @author: xuanlonghua
 * @date: 2021/2/7
 * @version: 1.0.0
 * @description:
 */

public interface OnUploadListener<T> {

    /**
     * 开始加载网络请求
     */
    void start();

    /**
     * 上传进度
     *
     * @param percentage
     */
    void onProgressUpdate(int percentage);

    /**
     * 上传成功
     *
     * @param t
     */
    void onSuccess(T t);

    /**
     * 上传失败
     *
     * @param e
     */
    void onError(Throwable e);

    /**
     * 结束网络请求
     */
    void onComplete();
}
