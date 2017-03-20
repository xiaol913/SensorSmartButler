package com.xianggao.smartbutler.ui;


import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xianggao.smartbutler.R;
import com.xianggao.smartbutler.utils.ModelHelper;
import com.xianggao.smartbutler.utils.RepeatHelper;
import com.xianggao.smartbutler.utils.ScreenListener;
import com.xianggao.smartbutler.utils.SensorHelper;
import com.xianggao.smartbutler.utils.WakeHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements ScreenListener.ScreenStateListener, SensorHelper.onSensorChangeListener {

    private ScreenListener screenListener;
    private SensorHelper accelerometer;
    private SensorHelper gyroscope;
    private SensorHelper gravity;
    private TextView main_txtAccelerometerX, main_txtAccelerometerY, main_txtAccelerometerZ, main_txtAction;
    private TextView main_txtGyroscopeX, main_txtGyroscopeY, main_txtGyroscopeZ;
    private TextView main_txtGravityX, main_txtGravityY, main_txtGravityZ;
    private WakeHelper mWakeHelper;
    private ExecutorService executorService;
    private ModelHelper modelHelper;

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
        screenListener = new ScreenListener(this);
        screenListener.start(this);
        accelerometer = new SensorHelper(this, Sensor.TYPE_ACCELEROMETER);
        gyroscope = new SensorHelper(this, Sensor.TYPE_GYROSCOPE);
        gravity = new SensorHelper(this, Sensor.TYPE_GRAVITY);
        main_txtAccelerometerX = (TextView) findViewById(R.id.main_txtAccelerometerX);
        main_txtAccelerometerY = (TextView) findViewById(R.id.main_txtAccelerometerY);
        main_txtAccelerometerZ = (TextView) findViewById(R.id.main_txtAccelerometerZ);
        main_txtAction = (TextView) findViewById(R.id.main_txtAction);
        main_txtGyroscopeX = (TextView) findViewById(R.id.main_txtGyroscopeX);
        main_txtGyroscopeY = (TextView) findViewById(R.id.main_txtGyroscopeY);
        main_txtGyroscopeZ = (TextView) findViewById(R.id.main_txtGyroscopeZ);
        main_txtGravityX = (TextView) findViewById(R.id.main_txtGravityX);
        main_txtGravityY = (TextView) findViewById(R.id.main_txtGravityY);
        main_txtGravityZ = (TextView) findViewById(R.id.main_txtGravityZ);
        executorService = Executors.newCachedThreadPool();
        modelHelper = new ModelHelper(this);
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
        gyroscope.unregisterListener();
        gyroscope.registerListener(this);
        gravity.unregisterListener();
        gravity.registerListener(this);
    }

    @Override
    public void onUserPresent() {

    }

    @Override
    public void onSensorChanged(Sensor sensor, float[] values) {
        int sensorType = sensor.getType();
        showDataInView(sensorType, values);
        double[] newValues = new double[9];
        long maxTimeMillis = 2000L;
        if (RepeatHelper.isFastDoubleAction(maxTimeMillis)) {
            return;//after 2000ms
        }
        if (TextUtils.isEmpty(main_txtAccelerometerX.getText().toString()))
            newValues[0] = 0;
        else
            newValues[0] = Float.parseFloat(main_txtAccelerometerX.getText().toString());
        if (TextUtils.isEmpty(main_txtAccelerometerY.getText().toString()))
            newValues[1] = 0;
        else
            newValues[1] = Float.parseFloat(main_txtAccelerometerY.getText().toString());
        if (TextUtils.isEmpty(main_txtAccelerometerZ.getText().toString()))
            newValues[2] = 0;
        else
            newValues[2] = Float.parseFloat(main_txtAccelerometerZ.getText().toString());
        if (TextUtils.isEmpty(main_txtGyroscopeX.getText().toString()))
            newValues[3] = 0;
        else
            newValues[3] = Float.parseFloat(main_txtGyroscopeX.getText().toString());
        if (TextUtils.isEmpty(main_txtGyroscopeY.getText().toString()))
            newValues[4] = 0;
        else
            newValues[4] = Float.parseFloat(main_txtGyroscopeY.getText().toString());
        if (TextUtils.isEmpty(main_txtGyroscopeZ.getText().toString()))
            newValues[5] = 0;
        else
            newValues[5] = Float.parseFloat(main_txtGyroscopeZ.getText().toString());
        if (TextUtils.isEmpty(main_txtGravityX.getText().toString()))
            newValues[6] = 0;
        else
            newValues[6] = Float.parseFloat(main_txtGravityX.getText().toString());
        if (TextUtils.isEmpty(main_txtGravityY.getText().toString()))
            newValues[7] = 0;
        else
            newValues[7] = Float.parseFloat(main_txtGravityY.getText().toString());
        if (TextUtils.isEmpty(main_txtGravityZ.getText().toString()))
            newValues[8] = 0;
        else
            newValues[8] = Float.parseFloat(main_txtGravityZ.getText().toString());
        showAction(newValues);
    }

    private void showDataInView(int sensorType, float[] values) {
        float x = values[0];
        float y = values[1];
        float z = values[2];
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            main_txtAccelerometerX.setText("" + x);
            main_txtAccelerometerY.setText("" + y);
            main_txtAccelerometerZ.setText("" + z);
        } else if (sensorType == Sensor.TYPE_GYROSCOPE) {
            main_txtGyroscopeX.setText("" + x);
            main_txtGyroscopeY.setText("" + y);
            main_txtGyroscopeZ.setText("" + z);
        } else if (sensorType == Sensor.TYPE_GRAVITY) {
            main_txtGravityX.setText("" + x);
            main_txtGravityY.setText("" + y);
            main_txtGravityZ.setText("" + z);
        }
    }

    private void showAction(double[] values) {
        String action = null;
        action = modelHelper.predictAction(values);
        switch (action) {
            case "2":
                action = "InVehicle";
                break;
            case "1":
                action = "Still";
                break;
            case "0":
                action = "OnFeet";
                break;
            default:
                action = "Unknown";
                break;
        }
        main_txtAction.setText("" + action);
//        Thread thread = new Thread() {
//            //通过线程池及时间频繁度来减少OOM的发生
//            @Override
//            public void run() {
//            }
//        };
//        executorService.submit(thread);
    }

    public void startCollectData(View v) {
        accelerometer.registerListener(this);
        gyroscope.registerListener(this);
        gravity.registerListener(this);
    }

    public void stopCollectData(View view) {
        accelerometer.unregisterListener();
        gyroscope.unregisterListener();
        gravity.unregisterListener();
    }

    public void RecordData(View view) {
        startActivity(new Intent(this, TestActivity.class));
        finish();
    }
}
