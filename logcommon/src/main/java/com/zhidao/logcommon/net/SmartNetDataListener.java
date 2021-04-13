package com.zhidao.logcommon.net;

import com.mogo.commonnet.bean.BaseBean;
import com.mogo.commonnet.listener.NetDataListener;
import com.mogo.commonnet.listener.OnUploadListener;
import com.zhidao.logcommon.utils.log.LoggerController;

/**
 * @author Xuanlh
 * @desc
 * @date 2021/2/14
 */

public abstract class SmartNetDataListener<T> implements NetDataListener<T>, OnUploadListener<T> {

    /**
     * 响应成功
     */
    public static final String RESPONSE_SUCCESS = "10000";

    //token 失效
    public static final int TOKEN_BAD = 80000;

    enum ErrorCode {
        ERROR_HTTP(8888);
        private int mErrorCode;

        ErrorCode(int errorCode) {
            mErrorCode = errorCode;
        }

        public int getErrorCode() {
            return mErrorCode;
        }

    }

    @Override
    public void start() {

    }


    @Override
    public void onProgressUpdate(int percentage) {

    }

    @Override
    public void onSuccess(T t) {
        if (t != null && t instanceof BaseBean) {
            BaseBean<T> baseBean = (BaseBean<T>) t;
            int errorCode = baseBean.getCode();
            if (errorCode == 0) {
                onDataSuccess(t);
            } else if (errorCode == TOKEN_BAD) {
                LoggerController.w("===>token失效");
            } else {
                onDataError(baseBean);
            }
        }
    }


    @Override
    public void onError(Throwable throwable) {
        BaseBean baseBean = new BaseBean();
        baseBean.setMsg(throwable.getMessage());
        baseBean.setCode(ErrorCode.ERROR_HTTP.getErrorCode());
        onDataError(baseBean);
    }

    @Override
    public void onComplete() {

    }

    /**
     * 数据成功的 errorCode = 0 ;
     *
     * @param t
     */
    public abstract void onDataSuccess(T t);

    /**
     * 数据解析失败
     */
    public abstract void onDataError(BaseBean baseBean);
}
