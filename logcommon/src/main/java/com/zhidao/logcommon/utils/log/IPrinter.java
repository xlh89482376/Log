package com.zhidao.logcommon.utils.log;

/**
 * @author Xuanlh
 * @desc
 * @date 2021/2/7
 */

public interface IPrinter {

    /**
     * verbose
     *
     * @param tag
     * @param msg
     */
    void v(String tag, String msg);


    /**
     * debug
     *
     * @param tag tag
     * @param msg 信息
     */
    void d(String tag, String msg);

    /**
     * 不带过滤 是否debug
     *
     * @param tag
     * @param msg
     */
    void dNoFilter(String tag, String msg);

    /**
     * warn
     *
     * @param tag
     * @param msg
     */
    void w(String tag, String msg);


    /**
     * error
     * 高等级
     *
     * @param tag
     * @param msg
     */
    void e(String tag, String msg);

    /**
     * @param tag
     * @param msg
     */
    void json(String tag, String msg);

    /**
     * 保存到文件
     *
     * @param config
     * @param msg
     */
    void saveDisk(IPrinterDiskConfig config, String msg);

    /**
     * 全局控制是否打印日志
     *
     * @param printerLog
     */
    void setPrinterLog(boolean printerLog);

    /**
     * 设置是否打印log
     *
     */
    boolean isPrinterLog();

    /**
     * 清理内存
     */
    void clear();
}
