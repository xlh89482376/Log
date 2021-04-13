package com.mogo.commonnet.utils;

import com.mogo.commonnet.listener.DownloadListener;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * @author xuanlonghua
 * @des
 * @date 2021/2/7
 */

public class NetDownloadUtils {

    /*当前下载的进度*/
    private static volatile int progressCurrent = 0;


    // 失败时候
    public static void handlerFailed(Throwable throwable, final DownloadListener listener) {
        Observable.just(throwable)
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        listener.onError(throwable);
                    }
                });
    }

    /**
     * 处理进度 以及成功回掉
     *
     * @param progressCurrent
     * @param listener
     */
    public static void handlerSuccess(final int progressCurrent, final DownloadListener listener) {
        Observable.just(progressCurrent)
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        if (integer >= 100) {
                            listener.onSuccess();
                        } else {
                            listener.onProgressUpdate(integer);
                        }
                    }
                });
    }

    //创建文件夹  返回文件夹地址
    public static String createFile(String downLoadPath) throws IOException {
        File file = new File(downLoadPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        return file.getAbsolutePath();
    }

    //获取下载文件的名称
    public static String getFileName(String downLoadUrl) {
        URL url = null;
        String filename = null;
        try {
            url = new URL(downLoadUrl);
            filename = url.getFile();
            return filename.substring(filename.lastIndexOf("/") + 1);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
