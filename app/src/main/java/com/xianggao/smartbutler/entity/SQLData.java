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
    private float x;
    private float y;
    private float z;
    private float q;
    private float w;
    private float e;
    private float a;
    private float s;
    private float d;
    private long timeline;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getQ() {
        return q;
    }

    public void setQ(float q) {
        this.q = q;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }

    public float getE() {
        return e;
    }

    public void setE(float e) {
        this.e = e;
    }

    public float getA() {
        return a;
    }

    public void setA(float a) {
        this.a = a;
    }

    public float getS() {
        return s;
    }

    public void setS(float s) {
        this.s = s;
    }

    public float getD() {
        return d;
    }

    public void setD(float d) {
        this.d = d;
    }

    public long getTimeline() {
        return timeline;
    }

    public void setTimeline(long timeline) {
        this.timeline = timeline;
    }
}
