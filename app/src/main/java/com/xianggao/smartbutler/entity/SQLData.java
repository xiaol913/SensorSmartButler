package com.xianggao.smartbutler.entity;

/**
 * 项目名：  SensorSmartButler
 * 包名：    com.xianggao.smartbutler.entity
 * 文件名：  SQLData
 * 创建者：  Shawn Gao
 * 创建时间：2017/3/15 - 21:16
 * 描述：    To record Accelerometer, Gyroscope, and Gravity data.
 */

public class SQLData {
    private double x;
    private double y;
    private double z;
    private double q;
    private double w;
    private double e;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getQ() {
        return q;
    }

    public void setQ(double q) {
        this.q = q;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getE() {
        return e;
    }

    public void setE(double e) {
        this.e = e;
    }
}
