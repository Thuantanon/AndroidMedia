package com.cxh.androidmedia.render_new.filter;

import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.cxh.androidmedia.utils.BitsUtil;
import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.FileUtil;
import com.cxh.androidmedia.utils.OpenGLUtils;

/**
 * Created by Cxh
 * Time : 2020-09-17  23:08
 * Desc :
 */
public class TextureWhiteFilter extends BaseGLBeautyFilter {

    private float[] vertexArray = new float[]{
            -1, 1, 0,
            1, 1, 0,
            1, -1, 0,
            -1, -1, 0
    };

    private float[] texCoordsArray = new float[]{
            0, 0,
            1, 0,
            1, 1,
            0, 1
    };

    private static short[] drawIndex = {
            0, 1, 2,
            0, 2, 3
    };

    private int mProgramId;
    private int mPositionLocation;
    private int mCoordLocation;
    private int mMatrixLocation;

    private int mFBOId;
    private int mOutputTexture;
    private float[] mPreviewMatrix;

    public TextureWhiteFilter(int width, int height) {
        super(width, height);
        String vertexShader = FileUtil.readShaderFromAssets("filter/yuv/vertex_shader.glsl");
        String fragShader = FileUtil.readShaderFromAssets("filter/yuv/fragment_shader.glsl");
        mProgramId = OpenGLUtils.loadProgram(vertexShader, fragShader);
        mPositionLocation = GLES30.glGetAttribLocation(mProgramId, "aTexPosition");
        mCoordLocation = GLES30.glGetAttribLocation(mProgramId, "aTexCoord");
        mMatrixLocation = GLES30.glGetUniformLocation(mProgramId, "uMatrix");

        mOutputTexture = OpenGLUtils.createEmptyTexture(width, height).mTextureId;
        mFBOId = OpenGLUtils.createFBO(mOutputTexture);

        // 预览图是横屏的
        mPreviewMatrix = new float[16];
        Matrix.setIdentityM(mPreviewMatrix, 0);
        Matrix.setRotateM(mPreviewMatrix, 0, 270, 0, 0, 1);
        Matrix.scaleM(mPreviewMatrix, 0, -1, 1, 1);
    }

    @Override
    public int draw(int textureId, int width, int height) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFBOId);
        GLES30.glViewport(0, 0, width, height);
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glUseProgram(mProgramId);
        // 旋转矩阵
        GLES30.glUniformMatrix4fv(mMatrixLocation, 1, false, mPreviewMatrix, 0);
        OpenGLUtils.setUnifrom1f(mProgramId, "mScale", mScale);

        GLES30.glEnableVertexAttribArray(mPositionLocation);
        GLES30.glVertexAttribPointer(mPositionLocation, 3, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(vertexArray));

        GLES30.glEnableVertexAttribArray(mCoordLocation);
        GLES30.glVertexAttribPointer(mCoordLocation, 2, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(texCoordsArray));

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        OpenGLUtils.setUnifrom1i(mProgramId, "uPreviewTex", 0);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawIndex.length, GLES30.GL_UNSIGNED_SHORT, BitsUtil.arraysToBuffer(drawIndex));

        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES30.glDisableVertexAttribArray(mCoordLocation);
        GLES30.glDisableVertexAttribArray(mPositionLocation);
        GLES30.glUseProgram(0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        return mOutputTexture;
    }

    @Override
    public void release() {
        super.release();
        GLES30.glDeleteProgram(mProgramId);
        GLES30.glDeleteFramebuffers(1, new int[]{mFBOId}, 0);
        GLES30.glDeleteTextures(1, new int[]{mOutputTexture}, 0);
    }

}
