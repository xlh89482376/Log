package com.zhidao.logcommon.utils.logger;

/**
 * @author: xuanlonghua
 * @date: 2021/1/31
 * @version: 1.0.0
 * @description:
 */

public enum LogLevel {

    OFF( Integer.MAX_VALUE),

    VERBOSE(1),

    DEBUG(2),

    INFO(3),

    WARN(4),

    ERROR(5);

    public final int level;

    private LogLevel(final int level) {
        this.level = level;
    }

}
