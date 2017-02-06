package com.xianggao.smartbutler.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 项目名：  MySensorData
 * 包名：    com.shawn.mysensordata.utils
 * 文件名：  SQLHelper
 * 创建者：  Shawn Gao
 * 创建时间：2017/2/5 - 3:40
 * 描述：    数据库工具类
 */

public class SQLHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String SQL_NAME = "sensor.db";
    private static SQLHelper instance;

    public static SQLHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SQLHelper(context);
        }
        return instance;
    }

    private SQLHelper(Context context) {
        super(context, SQL_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sql) {
        String create_sql = "CREATE TABLE IF NOT EXISTS sensor_test ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "type INTEGER NOT NULL,"
                + "x FLOAT NOT NULL,"
                + "y FLOAT NOT NULL,"
                + "z FLOAT NOT NULL,"
                + "timeline INTEGER NOT NULL)";

        sql.execSQL(create_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sql, int oldVersion, int newVersion) {

    }
}
