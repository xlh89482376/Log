package com.zhidao.logcommon.mvp.base_contract;

/**
 * @author Xuanlh
 * @desc
 * @date 2021/2/13
 */

public interface BaseView {

    /**
     * 展示加载框
     */
    void showLoading();

    /**
     * 隐藏加载框
     */
    void dismissLoading();
}
