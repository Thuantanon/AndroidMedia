package com.cxh.facedetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLBitmap {

    private int aPositionHandle;
    private int uTextureSamplerHandle;
    private int aTextureCoordHandle;
    private int programId;
    private int[] textures;
    private int[] frameBuffers;
    private FloatBuffer vertexBuffer;
    private final float[] vertexData = {
            1f, -1f,
            -1f, -1f,
            1f, 1f,
            -1f, 1f
    };
    private FloatBuffer textureVertexBuffer;
    private final float[] textureVertexData = {
            1f, 0f,//右下
            0f, 0f,//左下
            1f, 1f,//右上
            0f, 1f//左上
    };
    private Bitmap bitmap;
    public GLBitmap(Context context, int id){
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureVertexBuffer.position(0);
        bitmap = BitmapFactory.decodeResource(context.getResources(),id);

    }
    private String vertexShader = "attribute vec4 aPosition;\n" +
            "attribute vec2 aTexCoord;\n" +
            "varying vec2 vTexCoord;\n" +
            "void main() {\n" +
            "    vTexCoord=aTexCoord;\n" +
            "    gl_Position = aPosition;\n" +
            "}";
    private String fragmentShader = "varying highp vec2 vTexCoord;\n" +
            "uniform highp sampler2D sTexture;\n"+
            "void main() {\n" +
            "    gl_FragColor = texture2D(sTexture,vec2(vTexCoord.x,1.0 - vTexCoord.y));\n" +
            "}";
    public void initFrame(int width,int height){
        this.width = width;
        this.height = height;
        programId = ShaderUtils.createProgram(vertexShader, fragmentShader);
        aPositionHandle = GLES30.glGetAttribLocation(programId, "aPosition");
        uTextureSamplerHandle= GLES30.glGetUniformLocation(programId,"sTexture");
        aTextureCoordHandle= GLES30.glGetAttribLocation(programId,"aTexCoord");
        textures = new int[2];
        GLES30.glGenTextures(2,textures,0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textures[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D,0, GLES30.GL_RGBA,bitmap,0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textures[1]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,0, GLES30.GL_RGBA,width,height,0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0);
        frameBuffers = new int[1];
        GLES30.glGenFramebuffers(1,frameBuffers,0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,frameBuffers[0]);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textures[1], 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);
    }
    public void setPoints(float[] points){
        vertexBuffer.rewind();
        vertexBuffer.put(points);
        vertexBuffer.position(0);
    }
    private int width,height;
    public int drawFrame(){
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,frameBuffers[0]);
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glViewport(0, 0, width, height);
        GLES30.glUseProgram(programId);
        GLES30.glEnableVertexAttribArray(aPositionHandle);
        GLES30.glVertexAttribPointer(aPositionHandle, 2, GLES30.GL_FLOAT, false,
                8, vertexBuffer);

        GLES30.glEnableVertexAttribArray(aTextureCoordHandle);
        GLES30.glVertexAttribPointer(aTextureCoordHandle,2, GLES30.GL_FLOAT,false,8,textureVertexBuffer);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textures[0]);
        GLES30.glUniform1i(uTextureSamplerHandle,0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0);
        GLES30.glUseProgram(0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);
        return textures[1];
    }
    public void release(){
        GLES30.glDeleteTextures(2,textures,0);
        GLES30.glDeleteFramebuffers(1,frameBuffers,0);
        GLES30.glDeleteProgram(programId);
    }
}
