package com.mogo.logcore.logdata.presenter;

import com.mogo.logcore.logdata.model.GetDataModel;
import com.zhidao.logcommon.mvp.base_contract.BaseView;
import com.zhidao.logcommon.mvp.base_presenter.MvpPresenter;

/**
 * @author Xuanlh
 * @desc 数据上传
 * @date 2021/2/13
 */

public class LogDataContract {

    public interface View extends BaseView {

        /**
         * 测试get请求
         * @param getDataModel
         */
        void onTestGetRequest(GetDataModel getDataModel);

        void onTestGetRequestWith(GetDataModel getDataModel);

    }

    public interface Presenter extends MvpPresenter<View> {

        /**
         * 测试post请求
         */
        void onTestPostRequest();

        void onTestGetRequestWithParms();

    }
}
