package com.xianggao.smartbutler.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

/**
 * 项目名：  MySensorData
 * 包名：    com.shawn.mysensordata.helper
 * 文件名：  ScreenListener
 * 创建者：  Shawn Gao
 * 创建时间：2017/2/5 - 3:01
 * 描述：    Screen status monitor
 * http://blog.csdn.net/mengweiqi33/article/details/18094221
 */

public class ScreenListener {
    private Context mContext;
    private ScreenBroadcastReceiver mScreenReceiver;
    private ScreenStateListener mScreenStateListener;

    public ScreenListener(Context context) {
        mContext = context;
        mScreenReceiver = new ScreenBroadcastReceiver();
    }

    /**
     * monitor screen
     *
     * @param listener
     */
    public void start(ScreenStateListener listener) {
        mScreenStateListener = listener;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceiver, filter);
        initScreenState();
    }

    /**
     * initial screen status
     */
    private void initScreenState() {
        if (mScreenStateListener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        PowerManager manager = (PowerManager) mContext
                .getSystemService(Context.POWER_SERVICE);
        if (manager.isScreenOn()) {
            mScreenStateListener.onScreenOn();
        } else {
            mScreenStateListener.onScreenOff();
        }
    }

    /**
     * stop monitoring
     */
    public void stop() {
        mContext.unregisterReceiver(mScreenReceiver);
    }

    /**
     * Recipient
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // on
                mScreenStateListener.onScreenOn();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // off
                mScreenStateListener.onScreenOff();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // unlock
                mScreenStateListener.onUserPresent();
            }
        }

    }

    /**
     * call back interface
     */
    public interface ScreenStateListener {

        /**
         * screen on
         */
        void onScreenOn();

        /**
         * screen off
         */
        void onScreenOff();

        /**
         * screen unlock
         */
        void onUserPresent();

    }
}
