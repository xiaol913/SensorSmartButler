package com.xianggao.smartbutler.utils;

/**
 * 项目名：  MySensorData
 * 包名：    com.shawn.mysensordata.utils
 * 文件名：  RepeatHelper
 * 创建者：  Shawn Gao
 * 创建时间：2017/2/5 - 3:23
 * 描述：    In order to prevent short time, repeated action occurs
 */

public class RepeatHelper {
    private static final long DEFAULT_TIME_MILLIS = 800L;//ms
    private static long lastTimeMillis = 0L;

    public static boolean isFastDoubleAction(long maxTimeMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        long diff = currentTimeMillis - lastTimeMillis;
        if (diff < maxTimeMillis) {
            return true;
        } else {
            lastTimeMillis = currentTimeMillis;
            return false;
        }
    }

    public static boolean isFastDoubleAction() {
        return isFastDoubleAction(DEFAULT_TIME_MILLIS);
    }
}
