package com.xianggao.smartbutler.entity;

/**
 * 项目名：  SmartButler
 * 包名：    com.xianggao.smartbutler.entity
 * 文件名：  SensorData
 * 创建者：  Shawn Gao
 * 创建时间：2017/2/5 - 4:04
 * 描述：    类描述
 */

public class SensorData {
    private int type;
    private double x;
    private double y;
    private double z;
    private long timeline;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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

    public long getTimeline() {
        return timeline;
    }

    public void setTimeline(long timeline) {
        this.timeline = timeline;
    }

}