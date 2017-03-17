package com.xianggao.smartbutler.ui;


import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xianggao.smartbutler.R;
import com.xianggao.smartbutler.entity.SQLData;
import com.xianggao.smartbutler.utils.CSVHelper;
import com.xianggao.smartbutler.utils.RepeatHelper;
import com.xianggao.smartbutler.utils.SQLHelper;
import com.xianggao.smartbutler.utils.ScreenListener;
import com.xianggao.smartbutler.utils.SensorHelper;
import com.xianggao.smartbutler.utils.WakeHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestActivity extends AppCompatActivity implements ScreenListener.ScreenStateListener, SensorHelper.onSensorChangeListener {

    private WakeHelper mWakeHelper;
    private ScreenListener screenListener;
    private SensorHelper accelerometer;
    private SensorHelper gyroscope;
    private SensorHelper gravity;
    private TextView main_txtAccelerometerX, main_txtAccelerometerY, main_txtAccelerometerZ;
    private TextView main_txtGyroscopeX, main_txtGyroscopeY, main_txtGyroscopeZ;
    private TextView main_txtGravityX, main_txtGravityY, main_txtGravityZ;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initView();
    }

    //initial
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
        main_txtGyroscopeX = (TextView) findViewById(R.id.main_txtGyroscopeX);
        main_txtGyroscopeY = (TextView) findViewById(R.id.main_txtGyroscopeY);
        main_txtGyroscopeZ = (TextView) findViewById(R.id.main_txtGyroscopeZ);
        main_txtGravityX = (TextView) findViewById(R.id.main_txtGravityX);
        main_txtGravityY = (TextView) findViewById(R.id.main_txtGravityY);
        main_txtGravityZ = (TextView) findViewById(R.id.main_txtGravityZ);
        executorService = Executors.newCachedThreadPool();
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
        float[] newValues = new float[9];
        long maxTimeMillis = 20L;
        if (RepeatHelper.isFastDoubleAction(maxTimeMillis)) {
            return;//after 20ms
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
        saveData(newValues);
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

    private void saveData(float[] values) {
        final float x = values[0];
        final float y = values[1];
        final float z = values[2];
        final float q = values[3];
        final float w = values[4];
        final float e = values[5];
        final float a = values[6];
        final float s = values[7];
        final float d = values[8];
        final long timeline = System.currentTimeMillis();
        Thread thread = new Thread() {
            //通过线程池及时间频繁度来减少OOM的发生
            @Override
            public void run() {
                SQLHelper sqlHelper = SQLHelper.getInstance(getBaseContext());
                SQLiteDatabase database = sqlHelper.getWritableDatabase();
                database.execSQL("INSERT INTO sensor_data (x,y,z,q,w,e,a,s,d,timeline) VALUES (?,?,?,?,?,?,?,?,?,?)",
                        new Object[]{x, y, z, q, w, e, a, s, d, timeline});
            }
        };
        executorService.submit(thread);
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

    //On Feet
    public void asOnFeet(View view) {
        exportCSV(0);
    }

    //Still
    public void asStill(View view) {
        exportCSV(1);
    }

    //In Vehicle
    public void asInVehicle(View view) {
        exportCSV(2);
    }

    public void exportCSV(final int action) {
        final ProgressDialog dialog = ProgressDialog.show(this, null, "Exporting to CSV");
        new Thread() {
            @Override
            public void run() {
                SQLHelper helper = SQLHelper.getInstance(getBaseContext());
                SQLiteDatabase database = helper.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT * FROM sensor_data", null);
                cursor.moveToFirst();
                ArrayList<SQLData> dataList = new ArrayList<>();
                while (cursor.moveToNext()) {
                    SQLData sqlData = new SQLData();
                    sqlData.setX(cursor.getFloat(cursor.getColumnIndex("x")));
                    sqlData.setY(cursor.getFloat(cursor.getColumnIndex("y")));
                    sqlData.setZ(cursor.getFloat(cursor.getColumnIndex("z")));
                    sqlData.setQ(cursor.getFloat(cursor.getColumnIndex("q")));
                    sqlData.setW(cursor.getFloat(cursor.getColumnIndex("w")));
                    sqlData.setE(cursor.getFloat(cursor.getColumnIndex("e")));
                    sqlData.setA(cursor.getFloat(cursor.getColumnIndex("a")));
                    sqlData.setS(cursor.getFloat(cursor.getColumnIndex("s")));
                    sqlData.setD(cursor.getFloat(cursor.getColumnIndex("d")));
                    sqlData.setTimeline(cursor.getLong(cursor.getColumnIndex("timeline")));
                    dataList.add(sqlData);
                }
                cursor.close();
                String result;
                try {
                    String str = CSVHelper.createCSV(action, dataList);
                    switch (str) {
                        case "100":
                            result = "Export Success!";
                            database.execSQL("DELETE FROM sensor_data");//clear all data
                            break;
                        default:
                            result = "Export Fail: " + str;
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    result = "Export Fail: " + e.getMessage();
                }
                final String theFinal = result;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), theFinal, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    public void deleteData(View view) {
        final ProgressDialog dialog = ProgressDialog.show(this, null, "Deleting......");
        new Thread() {
            @Override
            public void run() {
                String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorData/";
                File file = new File(dirPath);
                SQLHelper helper = SQLHelper.getInstance(getBaseContext());
                SQLiteDatabase database = helper.getReadableDatabase();
                database.execSQL("DELETE FROM sensor_data");//clear all data
                CSVHelper.deleteCSV(file);//delete all CSV files
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Data Empty", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }
}