package com.mogo.commonnet.utils;

import com.mogo.commonnet.listener.OnUploadListener;
import com.mogo.commonnet.listener.ProgressRequestBody;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author xuanlonghua
 * @desc
 * @date 2021/2/7
 */

public class NetUtils {

    //拼接URL
    public static String prepareParam(Map<String, String> paramMap) {
        StringBuilder sb = new StringBuilder();
        if (paramMap == null || paramMap.isEmpty()) {
            return "";
        } else {
            for (String key : paramMap.keySet()) {
                String value = paramMap.get(key);
                if (sb.length() < 1) {
                    sb.append(key).append("=").append(value);
                } else {
                    sb.append("&").append(key).append("=").append(value);
                }
            }
            return sb.toString();
        }
    }

    public static String prepareParamMap(String content, Map<String, String> paramMap) {
        StringBuilder sb = new StringBuilder(content);
        if (paramMap == null || paramMap.isEmpty()) {
            return "";
        } else {
            for (String key : paramMap.keySet()) {
                String value = paramMap.get(key);
                sb.append("&").append(key).append("=").append(value);
            }
            return sb.toString();
        }
    }

    public static String prepareParam(String content, Map<String, String> paramMap) {
        StringBuilder sb = new StringBuilder(content);
        if (paramMap == null || paramMap.isEmpty()) {
            return sb.toString();
        } else {
            int index = 0;
            for (String key : paramMap.keySet()) {
                String value = paramMap.get(key);
                index++;
                if (index <= 1) {
                    sb.append(key).append("=").append(value);
                } else {
                    sb.append("&").append(key).append("=").append(value);
                }
            }
            return sb.toString();
        }
    }


    /**
     * 拼接上传文件 multipart/form-data 表单提交
     *
     * @param FormDataPartMap
     * @param fileList
     * @return
     */
    public static <T> MultipartBody createMultipartBody(HashMap<String, String> FormDataPartMap, List<File> fileList, OnUploadListener<T> listener) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (FormDataPartMap != null) {
            for (Map.Entry<String, String> entry : FormDataPartMap.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        if (fileList != null) {
            for (File file : fileList) {
                ProgressRequestBody requestBody = new ProgressRequestBody<T>(file, "image/*", listener);
                builder.addFormDataPart("file", file.getName(), requestBody);
            }
        }
        return builder.build();
    }

    /**
     * @param formDataPartFileKey dataPart 文件名key
     * @param FormDataPartMap
     * @param fileList 文件
     * @param listener
     * @param <T>
     * @return
     */
    public static <T> MultipartBody createMultipartBody(String formDataPartFileKey, HashMap<String, String> FormDataPartMap, File fileList, OnUploadListener<T> listener) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (FormDataPartMap != null) {
            for (Map.Entry<String, String> entry : FormDataPartMap.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        if (fileList != null) {
            ProgressRequestBody requestBody = new ProgressRequestBody<T>(fileList, "image/*", listener);
            builder.addFormDataPart(formDataPartFileKey, fileList.getName(), requestBody);
        }
        return builder.build();
    }


    /**
     * 上传byte[]
     *
     * @param FormDataPartMap
     * @param img
     * @param <T>
     * @param fileName        文件名称
     * @return
     */
    public static <T> MultipartBody createMultipartBodyByte(HashMap<String, String> FormDataPartMap, byte[] img, String fileName) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (FormDataPartMap != null) {
            for (Map.Entry<String, String> entry : FormDataPartMap.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), img);
        builder.addFormDataPart("file", fileName, requestBody);
        return builder.build();
    }
}
