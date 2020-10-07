package com.cxh.facedetection;

import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLPoints {
    private FloatBuffer vertexBuffer;
    private int bufferLength = 106*2*4;
    private int programId = -1;
    private int aPositionHandle;

    private int[] vertexBuffers;


    private String fragmentShader =
            "void main() {\n" +
            "    gl_FragColor = vec4(1.0,0.0,0.0,1.0);\n" +
            "}";
    private String vertexShader = "attribute vec2 aPosition;\n" +
            "void main() {\n" +
            "    gl_Position = vec4(aPosition,0.0,1.0);\n" +
            "    gl_PointSize = 10.0;\n"+
            "}";
    public GLPoints(){
        vertexBuffer = ByteBuffer.allocateDirect(bufferLength)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.position(0);
    }
    public void initPoints(){
        programId = ShaderUtils.createProgram(vertexShader, fragmentShader);
        aPositionHandle = GLES30.glGetAttribLocation(programId, "aPosition");

        vertexBuffers = new int[1];
        GLES30.glGenBuffers(1,vertexBuffers,0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vertexBuffers[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, bufferLength, vertexBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
    }
    public void setPoints(float[] points){
        vertexBuffer.rewind();
        vertexBuffer.put(points);
        vertexBuffer.position(0);
    }


    public void drawPoints(){
        GLES30.glUseProgram(programId);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vertexBuffers[0]);
        GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER,0,bufferLength,vertexBuffer);
        GLES30.glEnableVertexAttribArray(aPositionHandle);
        GLES30.glVertexAttribPointer(aPositionHandle, 2, GLES30.GL_FLOAT, false,
                0, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 106);
    }

    public void release(){
        GLES30.glDeleteProgram(programId);
        GLES30.glDeleteBuffers(1,vertexBuffers,0);
    }
}
