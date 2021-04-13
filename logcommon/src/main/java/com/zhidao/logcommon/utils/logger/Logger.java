package com.zhidao.logcommon.utils.logger;

/**
 * @author: xuanlonghua
 * @date: 2021/1/31
 * @version: 1.0.0
 * @description:
 */

public final class Logger {
    private static final Printer sPrinter = new LoggerPrinter();

    private Logger() {
    }

    public static Settings init() {
        return sPrinter.init(LogLevel.DEBUG);
    }

    public static Settings init(LogLevel logLevel) {
        return sPrinter.init(logLevel);
    }

    public static void d( String tag, String message, Object... args) {
        if(isLoggable(LogLevel.DEBUG)) sPrinter.d(tag, message, args);
    }

    public static void e( String tag, String message, Object... args) {
        if(isLoggable(LogLevel.ERROR)) sPrinter.e(tag, null, message, args);
    }

    public static void e( String tag, Throwable throwable, String message, Object... args) {
        if(isLoggable(LogLevel.ERROR)) sPrinter.e(tag, throwable, message, args);
    }

    public static void i( String tag, String message, Object... args) {
        if(isLoggable(LogLevel.INFO)) sPrinter.i(tag, message, args);
    }

    public static void v( String tag, String message, Object... args) {
        if(isLoggable(LogLevel.VERBOSE)) sPrinter.v(tag, message, args);
    }

    public static void w( String tag, String message, Object... args) {
        if(isLoggable(LogLevel.WARN)) sPrinter.w(tag, message, args);
    }

    public static void easyLog( String tag, String message) {
        if(isLoggable(LogLevel.DEBUG)) sPrinter.d(tag, message);
    }

    public static void json( String tag, String json) {
        if(isLoggable(LogLevel.DEBUG)) sPrinter.json(tag, json);
    }

    public static void xml( String tag, String xml) {
        if(isLoggable(LogLevel.DEBUG)) sPrinter.xml(tag, xml);
    }

    private static boolean isLoggable(LogLevel logLevel){
        return sPrinter.getSettings().getLogLevel().level <= logLevel.level;
    }
}