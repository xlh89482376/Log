package com.zhidao.logcommon.mvp;

import android.content.Context;

import com.zhidao.logcommon.mvp.base_contract.BaseView;
import com.zhidao.logcommon.mvp.base_presenter.MvpPresenter;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Xuanlh
 * @desc
 * @date 2021/2/13
 */

public class BasePresenter<T extends BaseView> implements MvpPresenter<T> {

    private T mPresenterView;

    private Context mContext;

    // 管理rxjava2与retrofit 防止内存泄漏 取消网络请求
    private CompositeDisposable mCompositeDisposable;

    public BasePresenter(Context context) {
        mCompositeDisposable = new CompositeDisposable();
        this.mContext = context;
    }

    @Override
    public void onAttach(T presenterView) {
        this.mPresenterView = presenterView;
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    @Override
    public void onRelease() {
        clearRequest();
        mPresenterView = null;
        mContext = null;
    }

    /**
     * 清除retrofit请求
     */
    private void clearRequest() {
        if (getCompositeDisposable() != null) {
            CompositeDisposable disposable = getCompositeDisposable();
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }

    public T getPresentView() {
        return mPresenterView;
    }

    public Context getContext() {
        return mContext;
    }
}
