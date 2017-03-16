package com.xianggao.smartbutler.utils;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.view.WindowManager;

/**
 * 项目名：  MySensorData
 * 包名：    com.shawn.mysensordata.helper
 * 文件名：  WakeHelper
 * 创建者：  Shawn Gao
 * 创建时间：2017/2/5 - 2:51
 * 描述：    Keep the CPU running or the screen is on
 */

public class WakeHelper {
    private static final String TAG = "wake_lock";
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private Type type;

    public WakeHelper(Context mContext, Type type) {
        this.type = type;
        //Get power management services
        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
    }

    public void acquire() {
        int level;
        if (type.equals(Type.KEEP_SCREEN_ON)) {
            level = PowerManager.SCREEN_BRIGHT_WAKE_LOCK;
        } else {
            level = PowerManager.PARTIAL_WAKE_LOCK;
        }
        mWakeLock = mPowerManager.newWakeLock(level | PowerManager.ON_AFTER_RELEASE, TAG);
        mWakeLock.acquire();
    }

    public void release() {
        if (null != mWakeLock) {
            mWakeLock.release();
        }
    }

    public static void keepScreenBright(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public enum Type {
        KEEP_CPU_RUN, KEEP_SCREEN_ON
    }
}
