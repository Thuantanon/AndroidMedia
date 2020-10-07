package com.cxh.androidmedia.render_old.bean;

/**
 * Created by Cxh
 * Time : 2019-06-02  03:00
 * Desc :
 */
public class GLTriangle3 {

    public GLPoint3 A;
    public GLPoint3 B;
    public GLPoint3 C;

    public GLTriangle3(GLPoint3 a, GLPoint3 b, GLPoint3 c) {
        A = a;
        B = b;
        C = c;
    }

    @Override
    public String toString() {
        return "GLTriangle3{" +
                "A=" + A +
                ", B=" + B +
                ", C=" + C +
                '}';
    }
}
