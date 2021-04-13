package com.mogo.commonnet.observer;

import android.util.Log;

import com.google.gson.Gson;
import com.mogo.commonnet.DisposableObserver;
import com.mogo.commonnet.listener.DownloadListener;
import com.mogo.commonnet.listener.NetDataListener;
import com.mogo.commonnet.listener.OnUploadListener;
import com.mogo.commonnet.manage.NetDownloadManage;
import com.mogo.commonnet.service.ApiService;
import com.mogo.commonnet.utils.NetDownloadUtils;
import com.mogo.commonnet.utils.NetIoUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.mogo.commonnet.utils.NetDownloadUtils.handlerFailed;
import static com.mogo.commonnet.utils.NetDownloadUtils.handlerSuccess;

/**
 * @author: xuanlonghua
 * @date: 2021/2/7
 * @description:
 */

public class NetObserver {

    /*当前下载的进度*/
    private volatile int progressCurrent = 0;
    //是否取消下载
    private boolean isCancelDownload;

    public static NetObserver create() {
        return new NetObserver();
    }

    /**
     * @param listener
     * @param <T>
     * @return
     */
    public <T> DisposableObserver getObserver(final Class<T> tClass, final NetDataListener<T> listener) {
        return new DisposableObserver<ResponseBody>() {

            @Override
            protected void onSubscribe() {
                super.onSubscribe();
                listener.start();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    Gson gson = new Gson();
                    T t = gson.fromJson(responseBody.string(), tClass);
                    listener.onSuccess(t);
                } catch (Exception e) {
                    listener.onError(e);
                }
            }


            @Override
            public void onError(Throwable e) {
                onComplete();
                listener.onError(e);
            }

            @Override
            public void onComplete() {
                listener.onComplete();
            }
        };
    }

    /**
     * 获取上传观察者
     *
     * @param tClass
     * @param listener
     * @param <T>
     * @return
     */
    public <T> DisposableObserver<? super ResponseBody> getUploadObserver(final Class<T> tClass, final OnUploadListener<T> listener) {

        return new DisposableObserver<ResponseBody>() {
            @Override
            protected void onSubscribe() {
                super.onSubscribe();
                listener.start();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    Gson gson = new Gson();
                    T t = gson.fromJson(responseBody.string(), tClass);
                    listener.onSuccess(t);
                } catch (Exception e) {
                    listener.onError(e);
                }
            }

            @Override
            public void onError(Throwable e) {
                onComplete();
                listener.onError(e);
            }

            @Override
            public void onComplete() {
                listener.onComplete();
            }
        };
    }

    /**
     * 创建下载观察者
     *
     * @param downLoadPath
     * @param listener
     * @return
     */
    public Observer<ResponseBody> createDownLoadObserver(final String downLoadPath, final DownloadListener listener) {
        return new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {
                NetDownloadManage.getInstance().addMission(downLoadPath, NetObserver.this);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String filePath = null;
                try {
                    filePath = NetDownloadUtils.createFile(downLoadPath);
                    progressCurrent = 0;
                    RandomAccessFile randomAccessFile = null;
                    InputStream inputStream = responseBody.byteStream();
                    long length = responseBody.contentLength();// 流的大小
                    try {
                        randomAccessFile = new RandomAccessFile(filePath, "rwd");
                        int count = 0;
                        int currentLength = 0; //当前的长度
                        byte[] buf = new byte[1024];
                        while ((count = inputStream.read(buf, 0, buf.length)) != -1 && !isCancelDownload) {
                            randomAccessFile.write(buf, 0, count);
                            currentLength = currentLength + count;
                            //进度
                            final int progress = (int) (((float) currentLength) / (length) * 100); // 计算百分比
                            if (progress != progressCurrent) {
                                //回掉
                                handlerSuccess(progress, listener);
                            }
                            progressCurrent = progress;
                        }
                        if (isCancelDownload()) {
                            listener.onCancel();
                        }

                    } catch (FileNotFoundException e) {
                        handlerFailed(e, listener);
                    } catch (IOException e) {
                        handlerFailed(e, listener);
                    } finally {
                        try {
                            if (randomAccessFile != null) {
                                randomAccessFile.close();
                            }
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handlerFailed(e, listener);
                }

            }

            @Override
            public void onError(Throwable e) {
                NetDownloadManage.getInstance().removeMission(downLoadPath);
                handlerFailed(e, listener);
            }

            @Override
            public void onComplete() {
                NetDownloadManage.getInstance().removeMission(downLoadPath);
            }
        };
    }

    /**
     * 下载大文件
     *
     * @param apiService
     * @param url
     * @param filePath
     * @param listener
     */
    public synchronized void downLoadBig(ApiService apiService, final String url, final String filePath, final DownloadListener listener) {
        final String downloadPath;
        try {
            downloadPath = NetDownloadUtils.createFile(filePath);
            File file = new File(downloadPath);
            long hasDownloadSize = 0;
            if (file.exists()) {
                //已经下载的文件大小
                hasDownloadSize = file.length();
            }
            Observable<ResponseBody> observable = apiService.downloadFile("bytes=" +
                    hasDownloadSize +
                    "-", url);
            final long finalHasDownloadSize = hasDownloadSize;
            observable
                    .subscribeOn(Schedulers.io())//在子线程取数据
                    .unsubscribeOn(Schedulers.io())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Log.e("OBserver", "===>onSubscribe");
                            NetDownloadManage.getInstance().addMission(url, NetObserver.this);
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            long length = responseBody.contentLength();// 流的大小
                            Log.e("OBserver", "===>onNext" + length);
                            if (finalHasDownloadSize >= length && length >= 0) {
                                Log.e("OBserver", "===>length" + length + ",finalHasDownloadSize" + finalHasDownloadSize);
                                //回掉
                                handlerSuccess(100, listener);
                                onComplete();
                                return;
                            }
                            //写文件流
                            RandomAccessFile randomAccessFile = null;
                            InputStream inputStream = null;
                            try {
                                inputStream = responseBody.byteStream();
                                randomAccessFile = new RandomAccessFile(downloadPath, "rwd");
                                randomAccessFile.seek(finalHasDownloadSize);
                                byte[] bytes = new byte[1024];
                                int count;
                                int currentLength = 0;
                                while ((count = inputStream.read(bytes, 0, bytes.length)) != -1 && !isCancelDownload) { //读   到头是-1
                                    randomAccessFile.write(bytes, 0, count);
                                    currentLength = currentLength + count;
                                    int progress = getPercentage(currentLength, length);
                                    Log.e("Observer", "--->progress" + progress + ",progressCurrent" + progressCurrent);
                                    if (progressCurrent != progress) { //不采用没%2==0 方式进行通知 这样用户看起来进度很怪 数据库更新操作可以以这种方式进行保存进度 这样有一个缓存
                                        progressCurrent = progress;
                                        //回掉
                                        handlerSuccess(progressCurrent, listener);
                                    }
                                }
                                if (isCancelDownload()) {
                                    listener.onCancel();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handlerFailed(e, listener);
                            } finally {
                                NetIoUtils.closeSilent(randomAccessFile);
                                NetIoUtils.closeSilent(inputStream);
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            handlerFailed(e, listener);
                            NetDownloadManage.getInstance().removeMission(downloadPath);
                        }

                        @Override
                        public void onComplete() {
                            NetDownloadManage.getInstance().removeMission(downloadPath);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            handlerFailed(e, listener);
        }

    }

    public final synchronized int getPercentage(long mDownloaded, long mFileSize) {
        if (mFileSize == 0) {
            return 0;
        } else {
            return (int) (((float) mDownloaded) / (mFileSize) * 100); // 计算百分比
        }

    }

    public boolean isCancelDownload() {
        return isCancelDownload;
    }

    public void setCancelDownload(boolean cancelDownload) {
        isCancelDownload = cancelDownload;
    }
}
