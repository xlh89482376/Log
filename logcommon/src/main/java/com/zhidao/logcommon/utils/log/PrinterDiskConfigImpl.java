package com.zhidao.logcommon.utils.log;

import com.mogo.commonnet.utils.NetContextUtils;
import com.zhidao.logcommon.utils.BasePackageUtils;
import com.zhidao.logcommon.utils.BaseTimeUtils;
import com.zhidao.logcommon.utils.FileUtils;

import java.io.File;

/**
 * @author Xuanlh
 * @desc
 * @date 2021/2/7
 */

public class PrinterDiskConfigImpl implements IPrinterDiskConfig{

    @Override
    public String getDiskDir() {
        return FileUtils.saveVideoBasePathDir(NetContextUtils.getContext(), BasePackageUtils.getAppName(NetContextUtils.getContext())+ File.separator+ BaseTimeUtils.formatTimeDate2());
    }

    @Override
    public String getDiskFileName() {
        return "log.log";
    }

    @Override
    public void clear() {
        FileUtils.removeAllFileDir(NetContextUtils.getContext(), BasePackageUtils.getAppName(NetContextUtils.getContext()));
    }
}
