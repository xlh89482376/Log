package com.zhidao.logcommon.debug;

import com.zhidao.logcommon.utils.log.LoggerController;

/**
 * @author Xuanlh
 * @desc 各个模块的调试信息控制接口
 * @date 2021/2/7
 */

public class DebugConfig {

    private static boolean sDebug = true;

    /**
     * 是否为调试模式
     *
     * @return true - 调试模式 false - 非调试模式
     */
    public static boolean isDebug() {
        return sDebug;
    }

    /**
     * 设置调试模式
     *
     * @param sDebug true - 调试模式 false - 非调试模式
     */
    public static void setDebug(boolean sDebug) {
        DebugConfig.sDebug = sDebug;
    }

    /**
     * 研发环境
     */
    public static final int NET_MODE_DEV = 1;

    /**
     * 测试环境
     */
    public static final int NET_MODE_QA = 2;

    /**
     * 演示环境
     */
    public static final int NET_MODE_DEMO = 4;

    /**
     * 生产环境
     */
    public static final int NET_MODE_RELEASE = 3;

    private static int sNetMode = NET_MODE_RELEASE;

    /**
     * 语音使用同行者
     */
    public static final int AI_TYPE_TXZ = 1;
    /**
     * 语音使用思必驰
     */
    public static final int AI_TYPE_SPEECH = 2;

    private static int sAIType = AI_TYPE_TXZ;

    /**
     * 获取网络环境类型
     *
     * @return {@link #NET_MODE_DEV}
     * {@link #NET_MODE_QA}
     * {@link #NET_MODE_RELEASE}
     */
    public static int getNetMode() {
        return sNetMode;
    }

    /**
     * 设置网络环境类型
     *
     * @param netMode {@link #NET_MODE_DEV}
     *                {@link #NET_MODE_QA}
     *                {@link #NET_MODE_DEMO}
     *                {@link #NET_MODE_RELEASE}
     */
    public static void setNetMode(int netMode) {
        DebugConfig.sNetMode = netMode;
        LoggerController.w("===hostUrl"+getHostUrl());
    }

    /**
     * 是否拉起位置服务，launcher 需要拉起位置服务，独立 app 不需要
     */
    private static boolean sLaunchLocationService = true;

    public static boolean isLaunchLocationService() {
        return sLaunchLocationService;
    }

    public static void setLaunchLocationService(boolean launchLocationService) {
        DebugConfig.sLaunchLocationService = launchLocationService;
    }

    /**
     * 是否使用自定义导航
     */
    private static boolean sUseCustomNavi = false;

    public static boolean isUseCustomNavi() {
        return sUseCustomNavi;
    }

    public static void setUseCustomNavi(boolean sUseCustomNavi) {
        DebugConfig.sUseCustomNavi = sUseCustomNavi;
    }

    /**
     * 设置使用哪个语音助手
     *
     * @param aiType {@link #AI_TYPE_TXZ}   {@link #AI_TYPE_SPEECH}
     */
    public static void setAIType(int aiType) {
        LoggerController.d("DebugConfig", "setAiType: " + aiType);
        sAIType = aiType;
    }

    /**
     * 使用哪个语音助手  {@link #AI_TYPE_TXZ}   {@link #AI_TYPE_SPEECH}
     */
    public static int getAIType() {
        return sAIType;
    }

    /**
     * 是否作为launcher运行
     */
    private static boolean sIsLauncher = false;

    public static boolean isLauncher() {
        return sIsLauncher;
    }

    public static void setLauncher(boolean isLauncher) {
        DebugConfig.sIsLauncher = isLauncher;
    }

    private static boolean sRequestOnlineCarData = true;

    public static boolean isRequestOnlineCarData() {
        return sRequestOnlineCarData;
    }

    public static void setRequestOnlineCarData(boolean sRequestOnlineCarData) {
        DebugConfig.sRequestOnlineCarData = sRequestOnlineCarData;
    }

    /**
     * 获取主机地址
     *
     * @return
     */
    public static String getHostUrl() {
        switch (DebugConfig.getNetMode()) {
            case DebugConfig.NET_MODE_DEV:
                return HttpConstant.HOST_DEV;
            case DebugConfig.NET_MODE_QA:
                return HttpConstant.HOST_TEST;
            case DebugConfig.NET_MODE_DEMO:
                return HttpConstant.HOST_DEMO;
            default:
                return HttpConstant.HOST_PRODUCT;
        }
    }
}
