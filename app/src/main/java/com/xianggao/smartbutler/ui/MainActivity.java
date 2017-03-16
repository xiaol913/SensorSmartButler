package com.xianggao.smartbutler.ui;


import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xianggao.smartbutler.R;
import com.xianggao.smartbutler.utils.ModelHelper;
import com.xianggao.smartbutler.utils.RepeatHelper;
import com.xianggao.smartbutler.utils.ScreenListener;
import com.xianggao.smartbutler.utils.SensorHelper;
import com.xianggao.smartbutler.utils.WakeHelper;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements ScreenListener.ScreenStateListener, SensorHelper.onSensorChangeListener {

//    private ScreenListener screenListener;
    private SensorHelper accelerometer;
    private TextView main_txtAccelerometerX, main_txtAccelerometerY, main_txtAccelerometerZ, main_txtAction;
    private WakeHelper mWakeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    //初始化
    private void initView() {
        mWakeHelper = new WakeHelper(this, WakeHelper.Type.KEEP_CPU_RUN);
        mWakeHelper.acquire();
//        screenListener = new ScreenListener(this);
//        screenListener.start(this);
        accelerometer = new SensorHelper(this, Sensor.TYPE_ACCELEROMETER);
        main_txtAccelerometerX = (TextView) findViewById(R.id.main_txtAccelerometerX);
        main_txtAccelerometerY = (TextView) findViewById(R.id.main_txtAccelerometerY);
        main_txtAccelerometerZ = (TextView) findViewById(R.id.main_txtAccelerometerZ);
        main_txtAction = (TextView) findViewById(R.id.main_txtAction);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWakeHelper.release();
    }

    @Override
    public void onBackPressed() {
        if (RepeatHelper.isFastDoubleAction(2000L)) {
            // twice BACK in 2s to exit
            finish();
            System.exit(0);
        } else {
            Toast.makeText(getApplicationContext(), "Press BACK one more to exit!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onScreenOn() {

    }

    @Override
    public void onScreenOff() {
        //Registering listener again can fix some phones stop recording data when they screen off
        //http://bbs.csdn.net/topics/390410025
        accelerometer.unregisterListener();
        accelerometer.registerListener(this);
    }

    @Override
    public void onUserPresent() {

    }

    @Override
    public void onSensorChanged(Sensor sensor, float[] values) {
        showAction(values);
    }

    private void showAction(float[] values) {
        final double x = values[0];
        final double y = values[1];
        final double z = values[2];
        String action = null;
        long maxTimeMillis = 200L;
        if (RepeatHelper.isFastDoubleAction(maxTimeMillis)) {
            return;//after 200ms
        }
        try {
            action = ModelHelper.predictAction(this, x, y, z);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        main_txtAccelerometerX.setText("X:" + x);
        main_txtAccelerometerY.setText("Y:" + y);
        main_txtAccelerometerZ.setText("Z:" + z);
        switch (action){
            case "0":
                action = "InVehicle";
                break;
            case "1":
                action = "Still";
                break;
            case "2":
                action = "Walking";
                break;
            default:
                action = "Unknown";
                break;
        }
        main_txtAction.setText("Action:" + action);
    }

    public void startCollectData(View v) {
        accelerometer.registerListener(this);
    }

    public void stopCollectData(View view) {
        accelerometer.unregisterListener();
    }

    public void RecordData(View view){
        startActivity(new Intent(this,TestActivity.class));
        finish();
    }
}
