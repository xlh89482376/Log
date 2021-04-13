package com.mogo.commonnet.manage;

import android.text.TextUtils;

import com.mogo.commonnet.observer.NetObserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author xuanlonghua
 * @date 2021/2/7
 * @des:
 */

public class NetDownloadManage {

    //保存任务
    private HashMap<String, NetObserver> mHashMapMission = new HashMap<>();

    //下载管理
    private static NetDownloadManage manage;

    public static NetDownloadManage getInstance() {
        if (manage == null) {
            manage = new NetDownloadManage();
        }
        return manage;
    }

    public void addMission(String missionUrl, NetObserver observer) {
        mHashMapMission.put(missionUrl, observer);
    }

    /**
     * 移除任务
     *
     * @param missionUrl
     */
    public void removeMission(String missionUrl) {
        if (!TextUtils.isEmpty(missionUrl)) {
            if (mHashMapMission != null) {
                Iterator<Map.Entry<String, NetObserver>> iterator = mHashMapMission.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, NetObserver> entry = iterator.next();
                    if (missionUrl.equals(entry.getKey())) {
                        NetObserver observer = entry.getValue();
                        if (observer != null) {
                            observer.setCancelDownload(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * 清除所有的下载任务
     */
    public void clearDownload() {
        for (NetObserver observer : mHashMapMission.values()) {
            if (observer != null) {
                observer.setCancelDownload(true);
            }

        }
    }
}
