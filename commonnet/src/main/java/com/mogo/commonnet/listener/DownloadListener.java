package com.mogo.commonnet.listener;

/**
 * @author: xuanlonghua
 * @date: 2021/2/7
 * @version: 1.0.0
 * @description: 下载监听
 */

public interface DownloadListener {

    /**
     * 下载成功
     */
    void onSuccess();

    /**
     * 下载失败
     *
     * @param throwable
     */
    void onError(Throwable throwable);

    /**
     * 上传进度
     *
     * @param percentage
     */
    void onProgressUpdate(int percentage);

    /**
     * 取消任务 不保存
     */
    void onCancel();
}
