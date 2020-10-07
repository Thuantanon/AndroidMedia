package com.cxh.facedetection;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;


public class GLFramebuffer {

    private float[] mSTMatrix = new float[16];

    private int[] textures;

    private SurfaceTexture surfaceTexture;
    public void initFramebuffer(){

        textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MIN_FILTER,
                GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER,
                GLES30.GL_LINEAR);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }

    public SurfaceTexture getSurfaceTexture(){
        surfaceTexture = new SurfaceTexture(textures[0]);
        return surfaceTexture;
    }

    public void release(){
        GLES30.glDeleteTextures(1,textures,0);
        if(surfaceTexture != null ){
            surfaceTexture.release();
            surfaceTexture = null;
        }
    }

    public int drawFrameBuffer(){
        if(surfaceTexture != null){
            surfaceTexture.updateTexImage();
            surfaceTexture.getTransformMatrix(mSTMatrix);
        }
        return textures[0];
    }

    public float[] getMatrix() {
        return mSTMatrix;
    }

}
