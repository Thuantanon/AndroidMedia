package com.cxh.androidmedia.render_new.filter;

import android.opengl.GLES30;
import android.opengl.GLES30;

import com.cxh.androidmedia.utils.BitsUtil;
import com.cxh.androidmedia.utils.FileUtil;
import com.cxh.androidmedia.utils.OpenGLUtils;

/**
 * Created by Cxh
 * Time : 2020-09-17  23:24
 * Desc :
 */
public class TexturePreviewFilter extends BaseGLBeautyFilter {

    private float[] vertexArray = new float[]{
            -1, 1, 0,
            1, 1, 0,
            1, -1, 0,
            -1, -1, 0
    };

    private float[] texCoordsArray = new float[]{
            0, 1,
            1, 1,
            1, 0,
            0, 0
    };

    private static short[] drawIndex = {
            0, 1, 2,
            0, 2, 3
    };

    private int mProgramId;
    private int mPositionLocation;
    private int mCoordLocation;

    public TexturePreviewFilter(int width, int height) {
        super(width, height);

        String vertexShader = FileUtil.readShaderFromAssets("filter/preview/vertex_shader.glsl");
        String fragShader = FileUtil.readShaderFromAssets("filter/preview/fragment_shader.glsl");
        mProgramId = OpenGLUtils.loadProgram(vertexShader, fragShader);
        mPositionLocation = GLES30.glGetAttribLocation(mProgramId, "aTexPosition");
        mCoordLocation = GLES30.glGetAttribLocation(mProgramId, "aTexCoord");
    }

    @Override
    public int draw(int textureId, int width, int height) {

        GLES30.glViewport(0, 0, width, height);
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glUseProgram(mProgramId);

        GLES30.glEnableVertexAttribArray(mPositionLocation);
        GLES30.glVertexAttribPointer(mPositionLocation, 3, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(vertexArray));

        GLES30.glEnableVertexAttribArray(mCoordLocation);
        GLES30.glVertexAttribPointer(mCoordLocation, 2, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(texCoordsArray));

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        OpenGLUtils.setUnifrom1i(mProgramId, "uTextureUnit", 0);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawIndex.length, GLES30.GL_UNSIGNED_SHORT, BitsUtil.arraysToBuffer(drawIndex));

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glDisableVertexAttribArray(mCoordLocation);
        GLES30.glDisableVertexAttribArray(mPositionLocation);
        GLES30.glUseProgram(0);
        return 0;
    }

    @Override
    public void release() {
        super.release();
        GLES30.glDeleteProgram(mProgramId);
    }
}
