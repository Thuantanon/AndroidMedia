package com.cxh.androidmedia.render_old.beauty;

import android.opengl.GLES30;

import com.cxh.androidmedia.render_old.BaseFboDrawable;
import com.cxh.androidmedia.utils.OpenGLUtils;

/**
 * Created by Cxh
 * Time : 2020-06-11  10:56
 * Desc : 使用VBO绘制三角形
 */
public class VBOFeatureDrawable extends BaseFboDrawable {

    /**
     * VBO（vertex buffer object）
     * 就是显存里用来缓存顶点的一块内存，在初始化时将一些顶点拷贝进显存，
     * 避免在绘制时频繁的拷贝大量数据，优化绘制性能
     */
    private static final String VERTEX_SHADER = "" +
            "attribute vec4 v_VertexCoord; " +
            "attribute vec4 v_VertexColor; " +
            "uniform mat4 v_Matrix; " +
            "varying vec4 vertexColor; " +
            "void main() { " +
            "gl_Position = v_VertexCoord * v_Matrix; " +
            "vertexColor = v_VertexColor; " +
            "}";

    private static final String FRAG_SHADER = "" +
            "precision mediump float; " +
            "varying vec4 vertexColor; " +
            "void main() { " +
            "gl_FragColor = vertexColor; " +
            "} ";

    private static float[] VERTEX_ARRAY = new float[]{
            -0.5f, 0.5f, 0,
            0.5f, 0.5f, 0,
            0.5f, -0.5f, 0,
            -0.5f, -0.5f, 0
    };

    private static float[] COLOR_ARRAY = new float[]{
            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f,
            1f, 1f, 0f, 1f
    };

    private static int[] DRAW_INDEX = new int[]{
            0, 1, 2,
            0, 2, 3
    };

    private int mGLProgram;
    private int mVertexLocation;
    private int mColorLocation;
    private int mMatrixLocation;

    private int[] mVboVertex;
    private int[] mVboColor;
    private int[] mVboIndex;

    public VBOFeatureDrawable() {
        mGLProgram = OpenGLUtils.loadProgram(VERTEX_SHADER, FRAG_SHADER);
        mVertexLocation = GLES30.glGetAttribLocation(mGLProgram, "v_VertexCoord");
        mColorLocation = GLES30.glGetAttribLocation(mGLProgram, "v_VertexColor");
        mMatrixLocation = GLES30.glGetUniformLocation(mGLProgram, "v_Matrix");

        mVboVertex = OpenGLUtils.initVbo(VERTEX_ARRAY);
        mVboColor = OpenGLUtils.initVbo(COLOR_ARRAY);
        mVboIndex = OpenGLUtils.initElementVbo(DRAW_INDEX);
    }

    @Override
    public int drawFBO(int textureId, int width, int height) {
        GLES30.glUseProgram(mGLProgram);

        float[] matrix = getAspectMatrix(width, height);
        GLES30.glUniformMatrix4fv(mMatrixLocation, 1, false, matrix, 0);

        // 一般情况
//        GLES30.glEnableVertexAttribArray(mVertexLocation);
//        GLES30.glVertexAttribPointer(mVertexLocation, 3, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(VERTEX_ARRAY));
        // 使用VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboVertex[0]);
        GLES30.glEnableVertexAttribArray(mVertexLocation);
        GLES30.glVertexAttribPointer(mVertexLocation, 3, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);

        // 一般情况
//        GLES30.glEnableVertexAttribArray(mColorLocation);
//        GLES30.glVertexAttribPointer(mColorLocation, 4, GLES30.GL_FLOAT, false, 0, BitsUtil.arraysToBuffer(COLOR_ARRAY));
        // 使用VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboColor[0]);
        GLES30.glEnableVertexAttribArray(mColorLocation);
        GLES30.glVertexAttribPointer(mColorLocation, 4, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);

        // 一般情况
//        GLES30.glDrawElements(GLES30.GL_TRIANGLES, DRAW_INDEX.length, GLES30.GL_UNSIGNED_INT, BitsUtil.arraysToBuffer(DRAW_INDEX));
        // 使用VBO
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mVboIndex[0]);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, DRAW_INDEX.length, GLES30.GL_UNSIGNED_INT, 0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);

        GLES30.glDisableVertexAttribArray(mVertexLocation);
        GLES30.glDisableVertexAttribArray(mColorLocation);
        GLES30.glUseProgram(0);
        return 0;
    }

    @Override
    public void release() {
        super.release();
        // OpenGL 环境没有释放就需要调用，若释放了这些内存会自动清理
        GLES30.glDeleteBuffers(1, mVboVertex, 0);
        GLES30.glDeleteBuffers(1, mVboColor, 0);
        GLES30.glDeleteBuffers(1, mVboIndex, 0);
        GLES30.glDeleteProgram(mGLProgram);
    }

}
