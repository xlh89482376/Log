package com.mogo.logcore.logdata.presenter;

import android.content.Context;

import com.mogo.commonnet.NetSmartRetrofit;
import com.mogo.commonnet.bean.BaseBean;
import com.mogo.logcore.logdata.ApiEndPoint;
import com.mogo.logcore.logdata.model.GetDataModel;
import com.zhidao.logcommon.debug.DebugConfig;
import com.zhidao.logcommon.mvp.BasePresenter;
import com.zhidao.logcommon.net.SmartNetDataListener;
import com.zhidao.logcommon.utils.log.LoggerController;
import com.zhidao.logcommon.utils.logger.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Xuanlh
 * @desc
 * @date 2021/2/14
 */

public class LogDataPresenter extends BasePresenter<LogDataContract.View> implements LogDataContract.Presenter{

    private static final String TAG = "LogDataPresenter";

    public LogDataPresenter(Context context) {
        super(context);
    }

    @Override
    public void onTestPostRequest() {
        getCompositeDisposable().add(NetSmartRetrofit.getInstance().build(DebugConfig.getHostUrl()).doGet(GetDataModel.class,
                DebugConfig.getHostUrl().concat(ApiEndPoint.GET_DATA), null, new SmartNetDataListener<GetDataModel>(){
                    @Override
                    public void onDataSuccess(GetDataModel getDataModel) {
                        LoggerController.v("getData===>onDataSuccess===>code = " + getDataModel.getCode());
                        LoggerController.v("getData===>onDataSuccess===>msg = " + getDataModel.getMsg());

//                        getPresentView().onTestGetRequest(getDataModel);
                    }

                    @Override
                    public void onDataError(BaseBean baseBean) {
                        LoggerController.v("getData===>onDataError===> msg = "+baseBean.toString());
//                        getPresentView().onTestGetRequestError();
                    }
                }));
    }

    @Override
    public void onTestGetRequestWithParms() {

        Map<String, String> map = new HashMap<String, String>();
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
