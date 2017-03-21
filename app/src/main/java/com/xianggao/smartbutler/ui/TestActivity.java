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
    private SensorHelper gravity;
    private TextView main_txtAccelerometerX, main_txtAccelerometerY, main_txtAccelerometerZ;
    private TextView main_txtGravityX, main_txtGravityY, main_txtGravityZ;
    private TextView main_txtLinearX, main_txtLinearY, main_txtLinearZ;
    private ExecutorService executorService;
    private double[] newValues = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

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
        saveData(newValues);
    }

    private void showDataInView(double[] values) {
        double a = values[0] - values[3];
        double b = values[1] - values[4];
        double c = values[2] - values[5];
        main_txtAccelerometerX.setText("" + values[0]);
        main_txtAccelerometerY.setText("" + values[1]);
        main_txtAccelerometerZ.setText("" + values[2]);
        main_txtGravityX.setText("" + values[3]);
        main_txtGravityY.setText("" + values[4]);
        main_txtGravityZ.setText("" + values[5]);
        main_txtLinearX.setText("" + a);
        main_txtLinearY.setText("" + b);
        main_txtLinearZ.setText("" + c);
    }

    private void saveData(double[] values) {
        final double x = values[0];
        final double y = values[1];
        final double z = values[2];
        final double q = values[3];
        final double w = values[4];
        final double e = values[5];
        Thread thread = new Thread() {
            //通过线程池及时间频繁度来减少OOM的发生
            @Override
            public void run() {
                SQLHelper sqlHelper = SQLHelper.getInstance(getBaseContext());
                SQLiteDatabase database = sqlHelper.getWritableDatabase();
                database.execSQL("INSERT INTO sensor_data (x,y,z,q,w,e) VALUES (?,?,?,?,?,?)",
                        new Object[]{x, y, z, q, w, e});
            }
        };
        executorService.submit(thread);
    }

    public void startCollectData(View v) {
        accelerometer.registerListener(this);
        gravity.registerListener(this);
    }

    public void stopCollectData(View view) {
        accelerometer.unregisterListener();
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
                    sqlData.setX(cursor.getDouble(cursor.getColumnIndex("x")));
                    sqlData.setY(cursor.getDouble(cursor.getColumnIndex("y")));
                    sqlData.setZ(cursor.getDouble(cursor.getColumnIndex("z")));
                    sqlData.setQ(cursor.getDouble(cursor.getColumnIndex("q")));
                    sqlData.setW(cursor.getDouble(cursor.getColumnIndex("w")));
                    sqlData.setE(cursor.getDouble(cursor.getColumnIndex("e")));
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