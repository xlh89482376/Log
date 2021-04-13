package com.mogo.commonnet.observer;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * @author: xuanlonghua
 * @date: 2021/2/7
 * @description: 网络错误后重试一次1秒
 */

public class RetryWithDelay implements Function<Observable<? extends Throwable>, Observable<?>> {

    private int maxRetries = 1;//最大出错重试次数
    private int retryDelayMillis = 1000;//重试间隔时间
    private int retryCount = 0;//当前出错重试次数

    public RetryWithDelay() {
    }

    public RetryWithDelay(int maxRetries, int retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) throws Exception {
        return observable
                .flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {

                    /**
                     * 需求1：根据异常类型选择是否重试
                     * 即，当发生的异常 = 网络异常 = IO异常 才选择重试
                     */
                    if (throwable instanceof IOException) {
                        if (++retryCount <= maxRetries) {
                            Log.w("RetryWithDelay", "get error, it will try after " + retryDelayMillis * retryCount
                                    + " millisecond, retry count " + retryCount);
                            // When this Observable calls onNext, the original Observable will be retried (i.e. re-subscribed).
                            return Observable.timer(retryDelayMillis * retryCount,
                                    TimeUnit.MILLISECONDS);
                        }
                        return Observable.error(throwable);
                    } else {
                        //不是网络异常 不需要重连
                        return Observable.error(throwable);
                    }
                });

    }
}
