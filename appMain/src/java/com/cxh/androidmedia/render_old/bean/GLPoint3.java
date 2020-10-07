package com.cxh.androidmedia.render_old.bean;

/**
 * Created by Cxh
 * Time : 2019-06-02  02:55
 * Desc :
 */
public class GLPoint3 {

    public double x;
    public double y;
    public double z;

    /**
     *  实际坐标
     * @param x
     * @param y
     * @param z
     */
    public GLPoint3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     *  经纬度获取坐标
     *  北极点为Y正方向，南极为Y负方向
     *  东经0度为Z正方向，东经180为Z负方向
     *  东经90度为X正方向，东经270度为X负方向
     * @param angleLongitude
     * @param angleLatitude
     */
    public GLPoint3(float angleLongitude, float angleLatitude){
        // y坐标的角度是固定的
        y = cos(angleLatitude);
        // 分别计算出点与x、z轴的夹角的sin、cos值，即可求出x、z坐标
        // 纬度上的纬线围成的圆的半径是球半径的sinQ倍
        double sinQ = sin(angleLatitude) / 1f;
        x = sin(angleLongitude) * sinQ;
        z = cos(angleLongitude) * sinQ;
    }

    private double sin(float angle) {
        return Math.sin(angle * Math.PI / 180f);
    }

    private double cos(float angle) {
        return Math.cos(angle * Math.PI / 180f);
    }

    @Override
    public String toString() {
        return "GLPoint3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
