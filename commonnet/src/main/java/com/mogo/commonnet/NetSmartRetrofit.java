package com.mogo.commonnet;

import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.mogo.commonnet.bean.BaseBean;
import com.mogo.commonnet.listener.DownloadListener;
import com.mogo.commonnet.listener.NetDataListener;
import com.mogo.commonnet.listener.OnUploadListener;
import com.mogo.commonnet.manage.NetDownloadManage;
import com.mogo.commonnet.observer.NetObserver;
import com.mogo.commonnet.observer.RetryWithDelay;
import com.mogo.commonnet.service.ApiService;
import com.mogo.commonnet.utils.NetUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Dispatcher;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

/**
 * @author: xuanlonghua
 * @date: 2021/2/7
 * @description:
 */

public class NetSmartRetrofit {

    private static final String TAG = "NetSmartRetrofit";

    private static NetSmartRetrofit instance;
    // retrofit build
    private Retrofit.Builder retrofitBuild;
    // retrofit
    private Retrofit retrofit;
    // service
    private ApiService apiService;

    private OkHttpClient.Builder mOkBuilder;
    // okhttp client
    private OkHttpClient client;

    /**
     * 单例模式
     *
     * @return instance
     */
    public static NetSmartRetrofit getInstance() {
        if (instance == null) {
            synchronized (NetSmartRetrofit.class) {
                if (instance == null) {
                    instance = new NetSmartRetrofit();
                }
            }
        }
        return instance;
    }

    public NetSmartRetrofit() {
        // 设置网络请求的Url地址
        retrofitBuild = new Retrofit.Builder()
                // 支持RxJava平台
                //compile 'com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0'   是他 就是他
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    /**
     * 生成Retrofit
     *
     * @param builder
     * @param baseUrl
     */
    public void createRetrofit(OkHttpClient.Builder builder, String baseUrl) {
        mOkBuilder = builder;
        client = mOkBuilder.build();
        retrofit = retrofitBuild
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // 支持RxJava
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * 获取接口类
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T getService(Class<T> tClass) {
        return retrofit.create(tClass);
    }

    /**
     * baseUrl
     *
     * @param baseUrl 必须以 / 结尾
     * @return
     */
    public NetSmartRetrofit build(String baseUrl) {
        retrofitBuild = new Retrofit.Builder();
        client = mOkBuilder.build();
        Log.d(TAG, "client:" + client.toString());
        retrofit = retrofitBuild
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        apiService = getService(ApiService.class);
        return this;
    }

    public <T extends BaseBean> DisposableObserver doGet(Class<T> tClass, String Url, Map<String, String> map, final NetDataListener<T> listener) {
        if (map != null) {
            return apiService
                    .doGet(Url, map)
                    .retryWhen(new RetryWithDelay()) //网络有问题尝试一次1秒
                    .subscribeOn(Schedulers.io())//在子线程取数据
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//在主线程显示ui
                    .subscribeWith(NetObserver.create().getObserver(tClass, listener));
        } else {
            return apiService
                    .doGet(Url)
                    .retryWhen(new RetryWithDelay()) //网络有问题尝试一次1秒
                    .subscribeOn(Schedulers.io())//在子线程取数据
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//在主线程显示ui
                    .subscribeWith(NetObserver.create().getObserver(tClass, listener));
        }

    }

    /**
     * post
     *
     * @param tClass
     * @param UrlPath  baseUrl+UrlPath
     * @param map
     * @param listener
     * @param <T>
     */
    public <T extends BaseBean> DisposableObserver doPost(final Class<T> tClass, String UrlPath, HashMap<String, String> map, final NetDataListener<T> listener) {
        return apiService
                .doPost(UrlPath, map)
                .retryWhen(new RetryWithDelay()) //网络有问题尝试一次1秒
                .subscribeOn(Schedulers.io())//在子线程取数据
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//在主线程显示ui
                .subscribeWith(NetObserver.create().getObserver(tClass, listener));
    }

    public <T extends BaseBean> DisposableObserver doPostNormal(final Class<T> tClass, String UrlPath, HashMap<String, Object> map, final NetDataListener<T> listener) {
        return apiService
                .doPostNormal(UrlPath, map)
                .retryWhen(new RetryWithDelay()) //网络有问题尝试一次1秒
                .subscribeOn(Schedulers.io())//在子线程取数据
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//在主线程显示ui
                .subscribeWith(NetObserver.create().getObserver(tClass, listener));
    }

    /**
     * @param tClass
     * @param UrlPath  baseUrl+aaa/bb
     * @param json     jsonString
     * @param listener
     * @param <T>
     */
    public <T extends BaseBean> DisposableObserver doPostJson(final Class<T> tClass, String UrlPath, String json, final NetDataListener<T> listener) {

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);

        return apiService
                .doPostJson(UrlPath, body)
                .subscribeOn(Schedulers.io()) //在子线程取数据
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) //在主线程显示ui
                .subscribeWith(NetObserver.create().getObserver(tClass, listener));
    }

    public <T extends BaseBean> DisposableObserver doPostJsonNormal(final Class<T> tClass, String UrlPath, String json, final NetDataListener<T> listener) {

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);

        return apiService
                .doPostJsonNormal(UrlPath, body)
                .subscribeOn(Schedulers.io())//在子线程取数据
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//在主线程显示ui
                .retryWhen(new RetryWithDelay()) //网络有问题尝试一次1秒
                .subscribeWith(NetObserver.create().getObserver(tClass, listener));
    }

    /**
     * 上传
     *
     * @param tClass
     * @param url
     * @param FormDataPartMap
     * @param fileArrayList
     * @param listener
     * @param <T>
     */
    public <T> DisposableObserver doUpLoad(Class<T> tClass, String url, HashMap<String, String> FormDataPartMap, List<File> fileArrayList, OnUploadListener<T> listener) {
        MultipartBody multipartBody = NetUtils.createMultipartBody(FormDataPartMap, fileArrayList, listener);
        return apiService.upLoad(url, multipartBody)
                .subscribeOn(Schedulers.io())//在子线程取数据
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//在主线程显示ui
                .subscribeWith(NetObserver.create().getUploadObserver(tClass, listener));
    }

    /**
     * 上传文件
     * @param tClass
     * @param url
     * @param fileKey 上传文件key
     * @param FormDataPartMap
     * @param fileArrayList
     * @param listener
     * @param <T>
     * @return
     */
    public <T> DisposableObserver doUpLoad(Class<T> tClass, String url, String fileKey, HashMap<String, String> FormDataPartMap, File fileArrayList, OnUploadListener<T> listener) {
        MultipartBody multipartBody = NetUtils.createMultipartBody(fileKey, FormDataPartMap, fileArrayList, listener);
        return apiService.upLoad(url, multipartBody)
                .subscribeOn(Schedulers.io())//在子线程取数据
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//在主线程显示ui
                .subscribeWith(NetObserver.create().getUploadObserver(tClass, listener));
    }

    /**
     * 上传图片 上传byte[]
     *
     * @param tClass
     * @param url
     * @param FormDataPartMap
     * @param img
     * @param listener
     * @param <T>
     */
    public <T> DisposableObserver doUpLoad(Class<T> tClass, String url, HashMap<String, String> FormDataPartMap, byte[] img, String fileName, OnUploadListener<T> listener) {
        MultipartBody multipartBody = NetUtils.createMultipartBodyByte(FormDataPartMap, img, fileName);
        return apiService.upLoad(url, multipartBody)
                .subscribeOn(Schedulers.io())//在子线程取数据
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//在主线程显示ui
                .subscribeWith(NetObserver.create().getUploadObserver(tClass, listener));
    }

    /**
     * @param url          下载url
     * @param downLoadPath 下载保存的路径
     * @param listener     下载监听器
     */
    public void doDownLoad(String url, String downLoadPath, DownloadListener listener) {
        apiService.downloadFile(url)
                .subscribeOn(Schedulers.io())//在子线程取数据
                .unsubscribeOn(Schedulers.io())
                .subscribe(NetObserver.create().createDownLoadObserver(downLoadPath, listener));
    }

    /**
     * 下载大文件
     *
     * @param url
     * @param downLoadPath
     * @param listener
     */
    public void doDownLoadBig(String url, String downLoadPath, DownloadListener listener) {
        NetObserver.create().downLoadBig(apiService, url, downLoadPath, listener);
    }

    /**
     * 移除任务
     *
     * @param url
     */
    public void cancelDownload(String url) {
        NetDownloadManage.getInstance().removeMission(url);
    }

    /**
     * 清除所有的下载任务
     */
    public void clearDownload() {
        NetDownloadManage.getInstance().clearDownload();
    }

    /**
     * 所有请求都cancel
     */
    public void cancelAll() {
        Dispatcher dispatcher = client.dispatcher();
        synchronized (dispatcher) {
            dispatcher.cancelAll();
        }
    }
}
