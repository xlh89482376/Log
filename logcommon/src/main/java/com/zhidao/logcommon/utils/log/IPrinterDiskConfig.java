package com.zhidao.logcommon.utils.log;

/**
 * @author Xuanlh
 * @desc 配置disk保存log
 * @date 2021/2/7
 */

public interface IPrinterDiskConfig {

    /**
     * 获取保存文件的夹
     *
     * @return
     */
    String getDiskDir();

    /**
     * 获取保存文件名称
     *
     * @return
     */
    String getDiskFileName();

    /**
     * clear disk
     */
    void clear();
}
