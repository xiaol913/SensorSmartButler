package com.xianggao.smartbutler.ui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xianggao.smartbutler.R;
import com.xianggao.smartbutler.entity.SensorData;
//import com.xianggao.smartbutler.utils.ExcelHelper;
import com.xianggao.smartbutler.utils.RepeatHelper;
import com.xianggao.smartbutler.utils.SQLHelper;
import com.xianggao.smartbutler.utils.ScreenListener;
import com.xianggao.smartbutler.utils.SensorHelper;
import com.xianggao.smartbutler.utils.WakeHelper;

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
//    private TextView main_txtGyroscopeX, main_txtGyroscopeY, main_txtGyroscopeZ;
//    private TextView main_txtGravityX, main_txtGravityY, main_txtGravityZ;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

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
        gravity = new SensorHelper(this, Sensor.TYPE_MAGNETIC_FIELD);
        main_txtAccelerometerX = (TextView) findViewById(R.id.main_txtAccelerometerX);
        main_txtAccelerometerY = (TextView) findViewById(R.id.main_txtAccelerometerY);
        main_txtAccelerometerZ = (TextView) findViewById(R.id.main_txtAccelerometerZ);
//        main_txtGyroscopeX = (TextView) findViewById(R.id.main_txtGyroscopeX);
//        main_txtGyroscopeY = (TextView) findViewById(R.id.main_txtGyroscopeY);
//        main_txtGyroscopeZ = (TextView) findViewById(R.id.main_txtGyroscopeZ);
//        main_txtGravityX = (TextView) findViewById(R.id.main_txtGravityX);
//        main_txtGravityY = (TextView) findViewById(R.id.main_txtGravityY);
//        main_txtGravityZ = (TextView) findViewById(R.id.main_txtGravityZ);
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
            // 几毫秒之内连续按两次
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
        //在手机锁屏的时候，重新注册传感器可解决部分手机无法黑屏后收集数据的问题，
        //参考：http://bbs.csdn.net/topics/390410025
        accelerometer.unregisterListener();
        accelerometer.registerListener(this);
//        gyroscope.unregisterListener();
//        gyroscope.registerListener(this);
//        gravity.unregisterListener();
//        gravity.registerListener(this);
    }

    @Override
    public void onUserPresent() {

    }

    @Override
    public void onSensorChanged(Sensor sensor, float[] values) {
        int sensorType = sensor.getType();
        showDataInView(sensorType, values);
        saveData(sensorType, values);
    }

    private void showDataInView(int sensorType, float[] values) {
        float x = values[0];
        float y = values[1];
        float z = values[2];
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            main_txtAccelerometerX.setText("X:" + x);
            main_txtAccelerometerY.setText("Y:" + y);
            main_txtAccelerometerZ.setText("Z:" + z);
//        } else if (sensorType == Sensor.TYPE_GYROSCOPE) {
//            main_txtGyroscopeX.setText("X:" + x);
//            main_txtGyroscopeY.setText("Y:" + y);
//            main_txtGyroscopeZ.setText("Z:" + z);
//        } else if (sensorType == Sensor.TYPE_GRAVITY) {
//            main_txtGravityX.setText("X:" + x);
//            main_txtGravityY.setText("Y:" + y);
//            main_txtGravityZ.setText("Z:" + z);
        }
    }

    private void saveData(final int sensorType, float[] values) {
        final float x = values[0];
        final float y = values[1];
        final float z = values[2];
        final long timeline = System.currentTimeMillis();
        Thread thread = new Thread() {
            //通过线程池及时间频繁度来减少OOM的发生
            @Override
            public void run() {
                long maxTimeMillis = 300L;
                if (RepeatHelper.isFastDoubleAction(maxTimeMillis)) {
                    return;//几毫秒后再保存
                }
                SQLHelper sqlHelper = SQLHelper.getInstance(getBaseContext());
                SQLiteDatabase database = sqlHelper.getWritableDatabase();
                database.execSQL("INSERT INTO sensor_test (type,x,y,z,timeline) VALUES (?,?,?,?,?)",
                        new Object[]{sensorType, x, y, z, timeline});
            }
        };
        executorService.submit(thread);
    }

    public void startCollectData(View v) {
        //开始监听传感器
        accelerometer.registerListener(this);
//        gyroscope.registerListener(this);
//        gravity.registerListener(this);
    }

    public void stopCollectData(View view) {
        //取消传感器监听
        accelerometer.unregisterListener();
//        gyroscope.unregisterListener();
//        gravity.unregisterListener();
    }

//    public void asOnFeet(View view) {
//        exportExcel(view, "OnFeet");
//    }
//
////    Still
//    public void asStill(View view) {
//        exportExcel(view, "Still");
//    }
//
//    //In Vehicle
//    public void asInVehicle(View view) {
//        exportExcel(view, "InVehicle");
//    }
//
//    //导出文件
//    public void exportExcel(View view, final String title) {
//        final ProgressDialog dialog = ProgressDialog.show(this, null, "Exporting to Excel");
//        new Thread() {
//            @Override
//            public void run() {
//                SQLHelper dbHelper = SQLHelper.getInstance(getBaseContext());
//                SQLiteDatabase database = dbHelper.getReadableDatabase();
//                Cursor cursor = database.rawQuery("SELECT * FROM sensor_test", null);
//                cursor.moveToFirst();
//                ArrayList<SensorData> dataList = new ArrayList<>();
//                while (cursor.moveToNext()) {
//                    SensorData data = new SensorData();
//                    data.setType(cursor.getInt(cursor.getColumnIndex("type")));
//                    data.setX(cursor.getFloat(cursor.getColumnIndex("x")));
//                    data.setY(cursor.getFloat(cursor.getColumnIndex("y")));
//                    data.setZ(cursor.getFloat(cursor.getColumnIndex("z")));
//                    data.setTimeline(cursor.getLong(cursor.getColumnIndex("timeline")));
//                    dataList.add(data);
//                }
//                cursor.close();
//                String result;
//                String path = null;
//                try {
//                    path = ExcelHelper.createExcel(title, dataList);
//                    result = "导出到Excel成功！";
//                } catch (Exception e) {
//                    path = "";
//                    e.printStackTrace();
//                    result = "导出失败：" + e.getMessage();
//                }
//                final String finalResult = result;
//                final String finalPath = path;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog.dismiss();
//                        Toast.makeText(getApplicationContext(), finalResult, Toast.LENGTH_SHORT).show();
//                        //发送到手机QQ
//                        if (!TextUtils.isEmpty(finalPath)) {
//                            String packageName = "com.tencent.mobileqq";
//                            try {
//                                Intent intent = new Intent(Intent.ACTION_SEND);
//                                intent.setPackage(packageName);
//                                intent.setType("*/*");
//                                intent.putExtra(Intent.EXTRA_STREAM, finalPath);
//                                startActivity(intent);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//            }
//        }.start();
//    }
//
//    public void deleteData(View view) {
//        final ProgressDialog dialog = ProgressDialog.show(this, null, "Deleting……");
//        new Thread() {
//            @Override
//            public void run() {
//                SQLHelper sqlHelper = SQLHelper.getInstance(getBaseContext());
//                SQLiteDatabase database = sqlHelper.getWritableDatabase();
//                database.execSQL("DELETE FROM sensor_test");//删除数据库中的数据
//                ExcelHelper.deleteExcel();//删除Excel文件
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog.dismiss();
//                        Toast.makeText(getApplicationContext(), "Data Empty", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }.start();
//    }
}
