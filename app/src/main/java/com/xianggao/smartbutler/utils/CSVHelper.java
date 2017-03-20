package com.xianggao.smartbutler.utils;

import android.os.Environment;

import com.xianggao.smartbutler.entity.SQLData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名：  SensorSmartButler
 * 包名：    com.xianggao.smartbutler.utils
 * 文件名：  CSVHelper
 * 创建者：  Shawn Gao
 * 创建时间：2017/3/15 - 20:39
 * 描述：    Save as .CSV files and Delete all files
 */

public class CSVHelper {
    public static String createCSV(int action, ArrayList<SQLData> dataList) throws IOException {
        String title = null;
        switch (action){
            case 0:
                title = "OnFeet";
                break;
            case 1:
                title = "Still";
                break;
            case 2:
                title = "InVehicle";
                break;
        }
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorData/" + title;
        new File(dirPath).mkdirs();
        String fileName = System.currentTimeMillis() + ".csv";
        File file = new File(dirPath, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw;
        BufferedWriter bfw;
        try {
            fw = new FileWriter(file);
            bfw = new BufferedWriter(fw);
            for (int i = 0; i < dataList.size(); i++) {
                SQLData sqlData = dataList.get(i);
                List list = new ArrayList();
                list.add(sqlData.getX());
                list.add(sqlData.getY());
                list.add(sqlData.getZ());
                list.add(sqlData.getQ());
                list.add(sqlData.getW());
                list.add(sqlData.getE());
                list.add(sqlData.getTimeline());
                list.add(action);
                for (int j = 0; j < list.size(); j++) {
                    if (j != list.size() - 1)
                        bfw.write(list.get(j) + ",");
                    else
                        bfw.write(list.get(j) + "");
                }
                bfw.newLine();
            }
            bfw.flush();
            bfw.close();
            return "100";
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public static void deleteCSV(File file) {
        File[] childFile = file.listFiles();
        if (childFile == null || childFile.length == 0) {
            file.delete();
            return;
        }
        for (File f : childFile) {
            deleteCSV(f);
        }
        file.delete();
    }
}