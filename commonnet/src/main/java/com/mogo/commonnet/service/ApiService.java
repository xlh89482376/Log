package com.mogo.commonnet.service;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author: Xuanlh
 * @date: 2021/2/7
 * @description: Api Service
 */

public interface ApiService {

    @GET
    Observable<ResponseBody> doGet(@Url String url);

    @GET
    Observable<ResponseBody> doGet(@Url String url, @QueryMap(encoded = false) Map<String, String> map);

    @FormUrlEncoded
    @POST("{path}")
    Observable<ResponseBody> doPost(@Path(value = "path", encoded = true) String urlPath, @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST
    Observable<ResponseBody> doPostNormal(@Url String url, @FieldMap Map<String, Object> map);

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("{path}")
    Observable<ResponseBody> doPostJson(@Path(value = "path", encoded = true) String urlPath, @Body RequestBody json);

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST()
    Observable<ResponseBody> doPostJsonNormal(@Url String urlPath, @Body RequestBody json);

    //上传
    @POST()
    Observable<ResponseBody> upLoad(@Url() String url, @Body RequestBody Body);

    /**
     * 下载视频
     *
     * @param fileUrl 文件路径
     */
    @Streaming //大文件时要加不然会OOM Range 断点续传
    @GET
    Observable<ResponseBody> downloadFile(@Header("Range") String range, @Url String fileUrl);

    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);
}
