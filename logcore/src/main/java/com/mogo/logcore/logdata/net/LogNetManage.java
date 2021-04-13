package com.mogo.logcore.logdata.net;

import com.mogo.commonnet.NetSmartRetrofit;
import com.mogo.commonnet.bean.BaseBean;
import com.mogo.commonnet.utils.NetContextUtils;
import com.mogo.logcore.logdata.ApiEndPoint;
import com.mogo.logcore.logdata.model.GetDataModel;
import com.mogo.logcore.logdata.presenter.LogDataContract;
import com.mogo.logcore.logdata.presenter.LogDataPresenter;
import com.zhidao.logcommon.debug.DebugConfig;
import com.zhidao.logcommon.net.SmartNetDataListener;
import com.zhidao.logcommon.utils.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Xuanlh
 * @desc
 * @date 2021/2/14
 */

public class LogNetManage implements LogDataContract.View {

    private static final String TAG = "LogNetManage";

    private static final LogNetManage outInstance = new LogNetManage();

    private final LogDataPresenter logDataPresenter;

    public static LogNetManage getInstance() {
        return outInstance;
    }

    private LogNetManage() {
        logDataPresenter = new LogDataPresenter(NetContextUtils.getContext());
        logDataPresenter.onAttach(this);
    }

    public LogDataPresenter getLogDataPresenter() {
        return logDataPresenter;
    }

    public void onDestory() {
        logDataPresenter.onRelease();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }


    @Override
    public void onTestGetRequest(GetDataModel getDataModel) {
        logDataPresenter.onTestPostRequest();
    }

    @Override
    public void onTestGetRequestWith(GetDataModel getDataModel) {
        logDataPresenter.onTestGetRequestWithParms();
    }

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public void onTestGetRequestWith1(){
        Map<String, String> map;
        map = new HashMap<>();
        map.put("id", "1");
        map.put("name", "宣龙华");

        getCompositeDisposable().add(NetSmartRetrofit.getInstance().build(DebugConfig.getHostUrl()).doGet(GetDataModel.class,
                DebugConfig.getHostUrl().concat(ApiEndPoint.GET_DATA), map, new SmartNetDataListener<GetDataModel>() {
                    @Override
                    public void onDataSuccess(GetDataModel getDataModel) {
                        Logger.d(TAG, "发了个带参数的get请求");
                    }

                    @Override
                    public void onDataError(BaseBean baseBean) {
                        Logger.d(TAG, "糟了，又特么失败了");
                    }
                }));
    }

}
