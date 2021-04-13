package com.zhidao.logcommon.mvp.base_presenter;

/**
 * @author Xuanlh
 * @desc 添加生命周期把控
 * @date 2021/2/13
 */

public interface MvpPresenter<T> {

    void onAttach(T presenterView);

    /**
     * 释放内存
     */
    void onRelease();
}
