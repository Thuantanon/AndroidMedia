package com.cxh.androidmedia.render_old;

import android.opengl.GLES30;

/**
 * Created by Cxh
 * Time : 2019-03-07  16:28
 * Desc :
 */
public abstract class BaseDrawable {

    protected int loadShader(int type, String shaderCode) {
        // 根据type创建顶点着色器或片元着色器
        int shader = GLES30.glCreateShader(type);
        // 将资源加入到着色器中并编译
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }

    protected void printLog(int glProgramId) {
        // 获取错误信息
        int[] status = new int[1];
        GLES30.glGetProgramiv(glProgramId, GLES30.GL_VALIDATE_STATUS, status, 0);
    }

    public abstract void draw(float[] matrix, int width, int height);

    public void release() {

    }
}
