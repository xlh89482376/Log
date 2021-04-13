package com.zhidao.logcommon.utils.logger;

/**
 * @author: xuanlonghua
 * @date: 2021/1/31
 * @version: 1.0.0
 * @description:
 */

public final class Settings {
    private int methodCount = 2;
    private boolean showThreadInfo = true;
    private int methodOffset = 0;
    private LogLevel logLevel = LogLevel.DEBUG;

    public Settings() {
    }

    public Settings hideThreadInfo() {
        this.showThreadInfo = false;
        return this;
    }

    public Settings setMethodCount(int methodCount) {
        if(methodCount < 0) {
            methodCount = 0;
        }

        this.methodCount = methodCount;
        return this;
    }

    public Settings setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public Settings setMethodOffset(int offset) {
        this.methodOffset = offset;
        return this;
    }

    public int getMethodCount() {
        return this.methodCount;
    }

    public boolean isShowThreadInfo() {
        return this.showThreadInfo;
    }

    public LogLevel getLogLevel() {
        return this.logLevel;
    }

    public int getMethodOffset() {
        return this.methodOffset;
    }
}
