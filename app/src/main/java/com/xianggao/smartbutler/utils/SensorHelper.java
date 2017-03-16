package com.xianggao.smartbutler.utils;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 项目名：  MySensorData
 * 包名：    com.shawn.mysensordata.helper
 * 文件名：  SensorHelper
 * 创建者：  Shawn Gao
 * 创建时间：2017/2/5 - 3:08
 * 描述：    Sensor tools class
 */

public class SensorHelper implements SensorEventListener {
    private SensorManager sensorManager;
    private onSensorChangeListener sensorChangeListener;
    private int sensorType;

    public SensorHelper(Context context, int sensorType) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.sensorType = sensorType;
    }

    //register sensor
    public void registerListener(onSensorChangeListener listener) {
        sensorChangeListener = listener;
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(sensorType),
                SensorManager.SENSOR_DELAY_UI);
    }

    //unregister sensor
    public void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        if (null != sensorChangeListener) {
            sensorChangeListener.onSensorChanged(sensorEvent.sensor, values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //Listener interface
    public interface onSensorChangeListener {
        void onSensorChanged(Sensor sensor, float[] values);
    }
}
