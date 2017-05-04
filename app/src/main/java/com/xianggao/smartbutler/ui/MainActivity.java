package com.xianggao.smartbutler.ui;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xianggao.smartbutler.R;
import com.xianggao.smartbutler.entity.CountData;
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
    private SensorHelper gravity;
    private TextView main_txtAccelerometerX, main_txtAccelerometerY, main_txtAccelerometerZ, main_txtAction, main_txtGPS;
    private TextView main_txtGravityX, main_txtGravityY, main_txtGravityZ;
    private TextView main_txtLinearX, main_txtLinearY, main_txtLinearZ;
    private WakeHelper mWakeHelper;
    private ExecutorService executorService;
    private ModelHelper modelHelper;
    private double[] newValues = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private double[] allValues;
    private CountData countData;
    private LocationManager locationManager;

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
        main_txtGPS = (TextView) findViewById(R.id.main_txtGPS);
        executorService = Executors.newCachedThreadPool();
        modelHelper = new ModelHelper(this);
        allValues = new double[9];
        countData = new CountData();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateView(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateView(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                updateView(locationManager.getLastKnownLocation(provider));
            }

            @Override
            public void onProviderDisabled(String provider) {
                updateView(null);
            }
        });
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
        long maxTimeMillis = 3000L;
        if (RepeatHelper.isFastDoubleAction(maxTimeMillis)) {
            return;//after 3000ms
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
        showAction(allValues);
    }

    private void showAction(final double[] values) {
        final String action = modelHelper.predictAction(values, countData);
        countData.setCountF(0);
        countData.setCountS(0);
        countData.setCountV(0);
        main_txtAccelerometerX.setText("" + values[0]);
        main_txtAccelerometerY.setText("" + values[1]);
        main_txtAccelerometerZ.setText("" + values[2]);
        main_txtGravityX.setText("" + values[3]);
        main_txtGravityY.setText("" + values[4]);
        main_txtGravityZ.setText("" + values[5]);
        main_txtLinearX.setText("" + values[6]);
        main_txtLinearY.setText("" + values[7]);
        main_txtLinearZ.setText("" + values[8]);
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

    private void updateView(Location location) {
        if (location != null) {
            StringBuffer sb = new StringBuffer();
            sb.append("Longitude:");
            sb.append(location.getLongitude());
            sb.append("\nLatitude:");
            sb.append(location.getLatitude());
            sb.append("\nSpeed:");
            sb.append(location.getSpeed());
            main_txtGPS.setText(sb.toString());
        } else {
            // 如果传入的Location对象为空则清空EditText
            main_txtGPS.setText("");
        }
    }
}
