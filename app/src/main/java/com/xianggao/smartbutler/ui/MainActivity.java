package com.xianggao.smartbutler.ui;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xianggao.smartbutler.R;
import com.xianggao.smartbutler.entity.CountData;
import com.xianggao.smartbutler.utils.ModelHelper;
import com.xianggao.smartbutler.utils.RepeatHelper;
import com.xianggao.smartbutler.utils.SQLHelper;
import com.xianggao.smartbutler.utils.ScreenListener;
import com.xianggao.smartbutler.utils.SensorHelper;
import com.xianggao.smartbutler.utils.WakeHelper;

import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements ScreenListener.ScreenStateListener, SensorHelper.onSensorChangeListener {

    private ScreenListener screenListener;
    private SensorHelper accelerometer;
    private SensorHelper gravity;
    private TextView main_txtAccelerometerX, main_txtAccelerometerY, main_txtAccelerometerZ, main_txtAction;
    private TextView main_txtGravityX, main_txtGravityY, main_txtGravityZ;
    private TextView main_txtLinearX, main_txtLinearY, main_txtLinearZ;
    private WakeHelper mWakeHelper;
    private ExecutorService executorService;
    private ModelHelper modelHelper;
    private double[] newValues = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private double[] allValues;
    private CountData countData;
    private TimerTask tt;

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
        gravity = new SensorHelper(this, Sensor.TYPE_GRAVITY);
        main_txtAccelerometerX = (TextView) findViewById(R.id.main_txtAccelerometerX);
        main_txtAccelerometerY = (TextView) findViewById(R.id.main_txtAccelerometerY);
        main_txtAccelerometerZ = (TextView) findViewById(R.id.main_txtAccelerometerZ);
        main_txtGravityX = (TextView) findViewById(R.id.main_txtGravityX);
        main_txtGravityY = (TextView) findViewById(R.id.main_txtGravityY);
        main_txtGravityZ = (TextView) findViewById(R.id.main_txtGravityZ);
        main_txtLinearX = (TextView) findViewById(R.id.main_txtLinearX);
        main_txtLinearY = (TextView) findViewById(R.id.main_txtLinearY);
        main_txtLinearZ = (TextView) findViewById(R.id.main_txtLinearZ);
        main_txtAction = (TextView) findViewById(R.id.main_txtAction);
        executorService = Executors.newCachedThreadPool();
        modelHelper = new ModelHelper(this);
        allValues = new double[9];
        countData = new CountData();
        tt = new TimerTask() {
            @Override
            public void run() {

            }
        };
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
        gravity.unregisterListener();
        gravity.registerListener(this);
    }

    @Override
    public void onUserPresent() {

    }

    @Override
    public void onSensorChanged(Sensor sensor, float[] values) {
        int sensorType = sensor.getType();
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            newValues[0] = values[0];
            newValues[1] = values[1];
            newValues[2] = values[2];
        }
        if (sensorType == Sensor.TYPE_GRAVITY) {
            newValues[3] = values[0];
            newValues[4] = values[1];
            newValues[5] = values[2];
        }
        long maxTimeMillis = 50L;
        if (RepeatHelper.isFastDoubleAction(maxTimeMillis)) {
            return;//after 50ms
        }
        showDataInView(newValues);
    }

    private void showDataInView(double[] values) {
        double a = values[0] - values[3];
        double b = values[1] - values[4];
        double c = values[2] - values[5];
        allValues[0] = values[0];
        allValues[1] = values[1];
        allValues[2] = values[2];
        allValues[3] = values[3];
        allValues[4] = values[4];
        allValues[5] = values[5];
        allValues[6] = a;
        allValues[7] = b;
        allValues[8] = c;
        main_txtAccelerometerX.setText("" + values[0]);
        main_txtAccelerometerY.setText("" + values[1]);
        main_txtAccelerometerZ.setText("" + values[2]);
        main_txtGravityX.setText("" + values[3]);
        main_txtGravityY.setText("" + values[4]);
        main_txtGravityZ.setText("" + values[5]);
        main_txtLinearX.setText("" + values[6]);
        main_txtLinearY.setText("" + values[7]);
        main_txtLinearZ.setText("" + values[8]);
    }

    private void showAction(final double[] values) {
        final String action = modelHelper.predictAction(values, countData);
        countData.setCountF(0);
        countData.setCountS(0);
        countData.setCountV(0);
        main_txtAction.setText("" + action);
    }

    public void startCollectData(View v) {
        accelerometer.registerListener(this);
        gravity.registerListener(this);
    }

    public void stopCollectData(View view) {
        accelerometer.unregisterListener();
        gravity.unregisterListener();
    }

    public void RecordData(View view) {
        startActivity(new Intent(this, TestActivity.class));
        finish();
    }
}
