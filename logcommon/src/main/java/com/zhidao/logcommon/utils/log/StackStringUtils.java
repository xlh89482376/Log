package com.zhidao.logcommon.utils.log;

/**
 * @author Xuanlh
 * @desc
 * @date 2021/2/7
 */

public class StackStringUtils {

    public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    /**
     * 生成TAG
     *
     * @param caller
     * @return
     */
    public static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        return tag;
    }

    /**
     * 获取自动生成的tag 类名称 方法名 多少行
     *
     * @return
     */
    public static String getAutoGenerateTag() {
        return generateTag(getCallerStackTraceElement());
    }
}
