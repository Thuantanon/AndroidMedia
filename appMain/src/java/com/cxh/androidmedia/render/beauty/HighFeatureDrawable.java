package com.cxh.androidmedia.render.beauty;

import android.opengl.GLES30;

import com.cxh.androidmedia.base.AMApp;
import com.cxh.androidmedia.render.BaseDrawable;
import com.cxh.androidmedia.utils.FileUtil;

/**
 * Created by Cxh
 * Time : 2020-06-11  10:56
 * Desc :
 */
public class HighFeatureDrawable extends BaseDrawable {

    private int mGLProgram;
    private int mMatrixHandler;
    private int mVertexHandler;
    private int mTextureHandler;

    public HighFeatureDrawable() {

        mGLProgram = GLES30.glCreateProgram();

        String vertexCode = FileUtil.readRenderScriptFromAssets(AMApp.get(), "glsl/render2/render_vertex_image.glsl");
        String fragmentCode = FileUtil.readRenderScriptFromAssets(AMApp.get(), "glsl/render2/render_fragment_image.glsl");
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexCode);
        int fragShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentCode);

        GLES30.glAttachShader(mGLProgram, vertexShader);
        GLES30.glAttachShader(mGLProgram, fragShader);
        GLES30.glLinkProgram(mGLProgram);
        GLES30.glValidateProgram(mGLProgram);
        printLog(mGLProgram);

        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragShader);
    }

    @Override
    public void draw(float[] matrix, int width, int height) {



    }
}
