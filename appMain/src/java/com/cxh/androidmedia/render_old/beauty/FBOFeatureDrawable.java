package com.cxh.androidmedia.render_old.beauty;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.cxh.androidmedia.R;
import com.cxh.androidmedia.base.AMApp;
import com.cxh.androidmedia.render_old.BaseFboDrawable;
import com.cxh.androidmedia.render_old.bean.BitmapTexture;
import com.cxh.androidmedia.utils.BitsUtil;
import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.OpenGLUtils;


/**
 * Created by Cxh
 * Time : 2020-09-01  23:41
 * Desc :
 */
public class FBOFeatureDrawable extends BaseFboDrawable {

    private static final String VERTEX_SHADER_FBO = "" +
            "attribute vec4 a_VertexCoord; \n" +
            "attribute vec2 a_TextureCoord; \n" +
            "uniform mat4 u_Matrix; \n" +
            "varying vec2 v_TextureCoord; \n" +
            "void main(){ \n" +
            "gl_Position = a_VertexCoord * u_Matrix; \n" +
            "v_TextureCoord = a_TextureCoord; \n" +
            "}";

    private static final String FRAG_SHADER_FBO = "" +
            "precision mediump float; \n" +
            "uniform sampler2D mTextureUnit; \n" +
            "varying vec2 v_TextureCoord; \n" +
            "void main(){ \n" +
            "vec4 color = texture2D(mTextureUnit, v_TextureCoord); \n" +
            "gl_FragColor = color; \n" +
            "} ";

    private static final String VERTEX_SHADER = "" +
            "attribute vec4 a_VertexCoord; \n" +
            "attribute vec2 a_TextureCoord; \n" +
            "uniform mat4 u_Matrix; \n" +
            "varying vec2 v_TextureCoord; \n" +
            "void main(){ \n" +
            "gl_Position = a_VertexCoord; \n" +
            "v_TextureCoord = a_TextureCoord; \n" +
            "}";

    private static final String FRAG_SHADER = "" +
            "precision mediump float; \n" +
            "uniform sampler2D mTextureUnit; \n" +
            "varying vec2 v_TextureCoord; \n" +
            "void main(){ \n" +
            "gl_FragColor = texture2D(mTextureUnit, v_TextureCoord); \n" +
            "} ";

    private int[] mFrameBufferId;
    private BitmapTexture mInputTexture;
    private BitmapTexture mOutputTexture;
    //第一次绘制
    private int mProgramFirst;
    private int mVertexLocationFirst;
    private int mTextureLocationFirst;
    private int mMatrixLocationFirst;
    private int mTextureUnitInput;
    // 第二次
    private int mProgramSecond;
    private int mVertexLocationSecond;
    private int mTextureLocationSecond;
    private int mMatrixLocationSecond;
    private int mTextureUnit;

    private static float[] VERTEX_ARRAY = {
            -1f, 1f, 0,
            1f, 1f, 0,
            1f, -1f, 0,
            -1f, -1f, 0
    };

    private static float[] TEXTURE_ARRAY = {
            0, 0,
            1f, 0,
            1f, 1f,
            0, 1f
    };

    // FBO纹理与正常纹理是反的
    private static float[] FBO_TEXTURE_ARRAY = {
            0, 1f,
            1f, 1f,
            1f, 0,
            0, 0
    };

    private static short[] VERTEX_INDEX = {
            0, 1, 2,
            0, 2, 3
    };

    public FBOFeatureDrawable(int width, int height) {

        // 初始化shader
        mProgramFirst = OpenGLUtils.loadProgram(VERTEX_SHADER_FBO, FRAG_SHADER_FBO);
        mVertexLocationFirst = GLES30.glGetAttribLocation(mProgramFirst, "a_VertexCoord");
        mTextureLocationFirst = GLES30.glGetAttribLocation(mProgramFirst, "a_TextureCoord");
        mMatrixLocationFirst = GLES30.glGetUniformLocation(mProgramFirst, "u_Matrix");
        mTextureUnitInput = GLES30.glGetUniformLocation(mProgramFirst, "mTextureUnit");

        mProgramSecond = OpenGLUtils.loadProgram(VERTEX_SHADER, FRAG_SHADER);
        mVertexLocationSecond = GLES30.glGetAttribLocation(mProgramSecond, "a_VertexCoord");
        mTextureLocationSecond = GLES30.glGetAttribLocation(mProgramSecond, "a_TextureCoord");
        mMatrixLocationSecond = GLES30.glGetUniformLocation(mProgramSecond, "u_Matrix");
        mTextureUnit = GLES30.glGetUniformLocation(mProgramSecond, "mTextureUnit");

        // 初始化FBO、纹理
        mInputTexture = OpenGLUtils.loadTexture(AMApp.get(), R.drawable.beauty1);
        // FBO中要求绑定的对象与缓冲区宽高相同
        mOutputTexture = OpenGLUtils.createEmptyTexture(width, height);

        mFrameBufferId = new int[1];
        GLES30.glGenFramebuffers(1, mFrameBufferId, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferId[0]);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, mOutputTexture.mTextureId, 0);
        if (GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            CCLog.i("glFramebufferTexture2D error...");
        }
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        CCLog.i("inputTexture : " + mInputTexture.mTextureId + " , outputTexture : " + mOutputTexture.mTextureId + " , fbo : " + mFrameBufferId[0]);
    }

    @Override
    public int drawFBO(int textureId, int width, int height) {
        // 绘制FBO
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferId[0]);
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glViewport(0, 0, width, height);
        GLES30.glUseProgram(mProgramFirst);

        float[] matrix = getImageAspectMatrix(width, height, mInputTexture.mBitmapWidth, mInputTexture.mBitmapHeight);
        GLES30.glUniformMatrix4fv(mMatrixLocationFirst, 1, false, matrix, 0);

        GLES30.glVertexAttribPointer(mVertexLocationFirst, 3, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(VERTEX_ARRAY));
        GLES30.glEnableVertexAttribArray(mVertexLocationFirst);

        GLES30.glVertexAttribPointer(mTextureLocationFirst, 2, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(FBO_TEXTURE_ARRAY));
        GLES30.glEnableVertexAttribArray(mTextureLocationFirst);

        // 激活纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        // 绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        GLES30.glUniform1i(mTextureUnitInput, 0);
        // 绘制三角形
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, VERTEX_ARRAY.length, GLES30.GL_UNSIGNED_SHORT, BitsUtil.arraysToBuffer(VERTEX_INDEX));

        GLES30.glEnableVertexAttribArray(mVertexLocationFirst);
        GLES30.glEnableVertexAttribArray(mTextureLocationFirst);
        GLES30.glUseProgram(0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        return mOutputTexture.mTextureId;
    }

    @Override
    public void draw(float[] matrix, int width, int height) {

        int textureID = drawFBO(mInputTexture.mTextureId, width, height);

        drawOnScreen(matrix, textureID, width, height);
    }

    @Override
    public void release() {
        super.release();
        // 不需要手动释放，这里退出GL环境就销毁了
//        GLES30.glDeleteFramebuffers(1, mFrameBufferId, 0);
//        GLES30.glDeleteProgram(mProgramFirst);
//        GLES30.glDeleteProgram(mProgramSecond);
    }

    private void drawOnScreen(float[] matrix, int textureID, int width, int height) {

        GLES30.glUseProgram(mProgramSecond);
        GLES30.glViewport(0, 0, width, height);

        GLES30.glVertexAttribPointer(mVertexLocationSecond, 3, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(VERTEX_ARRAY));
        GLES30.glEnableVertexAttribArray(mVertexLocationSecond);

        GLES30.glVertexAttribPointer(mTextureLocationSecond, 2, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(TEXTURE_ARRAY));
        GLES30.glEnableVertexAttribArray(mTextureLocationSecond);

        // 激活纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        // 绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID);
        GLES30.glUniform1i(mTextureUnit, 0);
        // 绘制三角形
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, VERTEX_ARRAY.length, GLES30.GL_UNSIGNED_SHORT, BitsUtil.arraysToBuffer(VERTEX_INDEX));

        GLES30.glDisableVertexAttribArray(mVertexLocationSecond);
        GLES30.glDisableVertexAttribArray(mTextureLocationSecond);
        GLES30.glUseProgram(0);
    }

    // 这里测试，只考虑w > h的情况
    private float[] getImageAspectMatrix(float width, float height, float bmpWidth, float bmpHeight) {
        float[] matrix = new float[16];
        float scale = (width / height) * (bmpHeight / bmpWidth);
        Matrix.setIdentityM(matrix, 0);
        Matrix.scaleM(matrix, 0, 1f, scale, 1f);
        return matrix;
    }
}
