package com.cxh.androidmedia.render_new.filter;

import android.opengl.GLES30;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.AMApp;
import com.cxh.androidmedia.render_old.bean.BitmapTexture;
import com.cxh.androidmedia.utils.BitsUtil;
import com.cxh.androidmedia.utils.FileUtil;
import com.cxh.androidmedia.utils.OpenGLUtils;

/**
 * Created by Cxh
 * Time : 2020-09-19  12:49
 * Desc :
 */
public class TextureEyeShadowFilter extends BaseGLBeautyFilter {

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

    // 腮红纹理坐标
    private float[] texRuddyCoordsArray = new float[]{
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
    private int mRuddyCoordLocation;

    private int mFBOId;
    private int mOutputTexture;
    private BitmapTexture mRuddyTexture;
    private float[] mRuddyLeft;
    private float[] mRuddyRight;
    private float[] mCenter;
    private boolean mLeftFace;

    public TextureEyeShadowFilter(int width, int height) {
        this(width, height, false);
    }

    public TextureEyeShadowFilter(int width, int height, boolean leftFace) {
        super(width, height);
        mLeftFace = leftFace;

        String vertexShader = FileUtil.readShaderFromAssets("filter/eyeshadow/vertex_shader.glsl");
        String fragShader = FileUtil.readShaderFromAssets("filter/eyeshadow/fragment_shader.glsl");
        mProgramId = OpenGLUtils.loadProgram(vertexShader, fragShader);
        mPositionLocation = GLES30.glGetAttribLocation(mProgramId, "aTexPosition");
        mCoordLocation = GLES30.glGetAttribLocation(mProgramId, "aTexCoord");
        mRuddyCoordLocation = GLES30.glGetAttribLocation(mProgramId, "aTexCoordRuddy");

        mRuddyTexture = OpenGLUtils.loadTexture(AMApp.get(), R.drawable.eye_shadow);
        mOutputTexture = OpenGLUtils.createEmptyTexture(width, height).mTextureId;
        mFBOId = OpenGLUtils.createFBO(mOutputTexture);
    }

    @Override
    public int draw(int textureId, int width, int height) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFBOId);
        GLES30.glViewport(0, 0, width, height);
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glUseProgram(mProgramId);
        OpenGLUtils.setUnifrom1f(mProgramId, "mScale", mScale);
        OpenGLUtils.setUnifrom2fv(mProgramId, "mRuddyLeft", mRuddyLeft);
        OpenGLUtils.setUnifrom2fv(mProgramId, "mRuddyRight", mRuddyRight);
        OpenGLUtils.setUnifrom2fv(mProgramId, "mCenter", mCenter);
        OpenGLUtils.setUnifrom1f(mProgramId, "mRuddyWidth", mRuddyTexture.mBitmapWidth);
        OpenGLUtils.setUnifrom1f(mProgramId, "mRuddyHeight", mRuddyTexture.mBitmapHeight);
        OpenGLUtils.setUnifrom1f(mProgramId, "mScreenRatio", mWidth * 1f / mHeight);
        OpenGLUtils.setUnifrom1i(mProgramId, "mLeftFace", mLeftFace ? 1 : 0);

        GLES30.glEnableVertexAttribArray(mPositionLocation);
        GLES30.glVertexAttribPointer(mPositionLocation, 3, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(vertexArray));

        GLES30.glEnableVertexAttribArray(mCoordLocation);
        GLES30.glVertexAttribPointer(mCoordLocation, 2, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(texCoordsArray));

//        GLES30.glEnableVertexAttribArray(mRuddyCoordLocation);
//        GLES30.glVertexAttribPointer(mRuddyCoordLocation, 2, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(texRuddyCoordsArray));

        // 第一张纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        OpenGLUtils.setUnifrom1i(mProgramId, "uTextureUnit", 0);
        // 水印纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mRuddyTexture.mTextureId);
        OpenGLUtils.setUnifrom1i(mProgramId, "uRuddyTextureUnit", 1);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawIndex.length, GLES30.GL_UNSIGNED_SHORT, BitsUtil.arraysToBuffer(drawIndex));

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glDisableVertexAttribArray(mCoordLocation);
        GLES30.glDisableVertexAttribArray(mRuddyCoordLocation);
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
        GLES30.glDeleteTextures(1, new int[]{mRuddyTexture.mTextureId}, 0);
    }

    public void setEye(float[] left, float[] right, float[] center) {
        mRuddyLeft = left;
        mRuddyRight = right;
        mCenter = center;
    }
}
