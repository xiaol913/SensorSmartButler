package com.xianggao.smartbutler.utils;


import android.hardware.Sensor;
import android.os.Environment;

import com.xianggao.smartbutler.entity.SensorData;

import org.apache.poi.hssf.record.formula.functions.Date;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 项目名：  MySensorData
 * 包名：    com.shawn.mysensordata.utils
 * 文件名：  ExcelHelper
 * 创建者：  Shawn Gao
 * 创建时间：2017/2/5 - 3:55
 * 描述：    使用POI创建Excel示例
 */

public class ExcelHelper {
    /**
     * 表格数据和SQLite中的要对应
     */
    public static String createExcel(ArrayList<SensorData> dataList) throws Exception {
        ArrayList<SensorData> accelerometerList = new ArrayList<>();//加速度传感器
        ArrayList<SensorData> gyroscopeList = new ArrayList<>();//陀螺仪传感器
        ArrayList<SensorData> magneticList = new ArrayList<>();//磁场传感器
        for (SensorData data : dataList) {
            switch (data.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accelerometerList.add(data);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    gyroscopeList.add(data);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magneticList.add(data);
                    break;
            }
        }
        //创建文档
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
        //加速度
        HSSFSheet accelerometerSheet = workbook.createSheet("Accelerometer");
        int accelerometerSize = accelerometerList.size();
        if (accelerometerSize > 0) {
            for (short i = 0; i < accelerometerSize; i++) {
                SensorData accelerometerData = accelerometerList.get(i);
                Object[] values = {accelerometerData.getType(), accelerometerData.getX(), accelerometerData.getY(), accelerometerData.getZ(), accelerometerData.getTimeline()};
                insertRow(accelerometerSheet, i, values, cellStyle);
            }
        }
        //陀螺仪
        HSSFSheet gyroscopeSheet = workbook.createSheet("Gyroscope");
        int gyroscopeSize = gyroscopeList.size();
        if (gyroscopeSize > 0) {
            for (short i = 0; i < gyroscopeSize; i++) {
                SensorData gyroscopeData = gyroscopeList.get(i);
                Object[] values = {gyroscopeData.getType(), gyroscopeData.getX(), gyroscopeData.getY(), gyroscopeData.getZ(), gyroscopeData.getTimeline()};
                insertRow(gyroscopeSheet, i, values, cellStyle);
            }
        }
        //磁力
        HSSFSheet magneticSheet = workbook.createSheet("Magnetic");
        int magneticSize = magneticList.size();
        if (magneticSize > 0) {
            for (short i = 0; i < magneticSize; i++) {
                SensorData magneticData = magneticList.get(i);
                Object[] values = {magneticData.getType(), magneticData.getX(), magneticData.getY(), magneticData.getZ(), magneticData.getTimeline()};
                insertRow(magneticSheet, i, values, cellStyle);
            }
        }
        // 保存文档
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp";
        new File(dirPath).mkdirs();
        File file = new File(dirPath, "sensor_test.xls");
        FileOutputStream fos;
        if (!file.exists()) {
            file.createNewFile();
        }
        fos = new FileOutputStream(file);
        workbook.write(fos);
        fos.close();
        return file.getAbsolutePath();
    }

    public static void deleteExcel() {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp";
        File file = new File(dirPath, "sensor_test.xls");
        file.delete();
    }


    /**
     * 插入一行数据
     *
     * @param sheet        插入数据行的表单
     * @param rowIndex     插入的行的索引
     * @param columnValues 要插入一行中的数据，数组表示
     * @param cellStyle    该格中数据的显示样式
     */

    private static void insertRow(HSSFSheet sheet, short rowIndex,
                                  Object[] columnValues, HSSFCellStyle cellStyle) {
        HSSFRow row = sheet.createRow(rowIndex);
        int column = columnValues.length;
        for (short i = 0; i < column; i++) {
            createCell(row, i, columnValues[i], cellStyle);
        }
    }

    /**
     * 在一行中插入一个单元值
     *
     * @param row         要插入的数据的行
     * @param columnIndex 插入的列的索引
     * @param cellValue   该cell的值：如果是Calendar或者Date类型，就先对其格式化
     * @param cellStyle   该格中数据的显示样式
     */
    private static void createCell(HSSFRow row, short columnIndex, Object cellValue,
                                   HSSFCellStyle cellStyle) {
        HSSFCell cell = row.createCell(columnIndex);
        // 如果是Calender或者Date类型的数据，就格式化成字符串
        if (cellValue instanceof Date) {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String value = format.format(cellValue);
            HSSFRichTextString richTextString = new HSSFRichTextString(value);
            cell.setCellValue(richTextString);
        } else if (cellValue instanceof Calendar) {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String value = format.format(((Calendar) cellValue).getTime());
            HSSFRichTextString richTextString = new HSSFRichTextString(value);
            cell.setCellValue(richTextString);
        } else {
            HSSFRichTextString richTextString = new HSSFRichTextString(cellValue.toString());
            cell.setCellValue(richTextString);
        }
        cell.setCellStyle(cellStyle);
    }
}
