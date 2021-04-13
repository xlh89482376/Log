package com.zhidao.logcommon.utils.log;

/**
 * @author Xuanlh
 * @desc
 * @date 2021/2/7
 */

public class LoggerController {

    //打印
    private static IPrinter printer = new AndroidLogPrinter();
    //config
    private static IPrinterDiskConfig printerDiskConfig;

    /**
     * verbose
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        printer.v(tag, msg);
    }

    /**
     * @param msg
     */
    public static void v(String msg) {
        printer.v(StackStringUtils.generateTag(getCallerStackTraceElement()), msg);
    }

    /**
     * debug
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        printer.d(tag, msg);
    }

    /**
     * debug
     *
     * @param tag
     * @param msg
     */
    public static void d(String msg) {
        printer.d(StackStringUtils.generateTag(getCallerStackTraceElement()), msg);
    }

    /**
     * not filter 不会过滤
     *
     * @param msg
     */
    public static void dNotFilter(String msg) {
        printer.dNoFilter(StackStringUtils.generateTag(getCallerStackTraceElement()), msg);
    }


    /**
     * debug
     *
     * @param msg
     */
    public static void w(String tag, String msg) {
        printer.w(tag, msg);
    }

    public static void w(String msg) {
        printer.w(StackStringUtils.generateTag(getCallerStackTraceElement()), msg);
    }

    public static void e(String tag, String msg) {
        printer.e(tag, msg);
    }

    public static void e(String msg) {

        printer.e(StackStringUtils.generateTag(getCallerStackTraceElement()), msg);
    }

    public static void json(String msg) {
        printer.json(StackStringUtils.generateTag(getCallerStackTraceElement()), msg);
    }

    /**
     * json
     *
     * @param tag
     * @param msg
     */
    public static void json(String tag, String msg) {
        printer.json(tag, msg);
    }

    /**
     * 获取调用的类名 以及 方法名 行数
     *
     * @return
     */
    public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    /**
     * 全局打印日志开关
     *
     * @param printerLog
     */
    public static void setPrinterLog(boolean printerLog) {
        printer.setPrinterLog(printerLog);
    }

    /**
     * 全局保存文件
     *
     * @param config
     * @param msg
     */
    public static void saveDisk(IPrinterDiskConfig config, String msg) {
        printer.saveDisk(config, msg);
    }

    /**
     * 默认保存一个位置
     *
     * @param msg
     */
    public static void saveDisk(String msg) {
        if (printerDiskConfig == null) {
            printerDiskConfig = new PrinterDiskConfigImpl();
        }
        printer.saveDisk(printerDiskConfig, msg);
    }

    /**
     * clear
     */
    public static void clear() {
        printer.clear();
    }

    /**
     * 清理磁盘缓存
     */
    public static void clearSaveDisk() {
        if (printerDiskConfig != null) {
            printerDiskConfig.clear();
        }
    }
}
