package com.cxh.androidmedia.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import androidx.annotation.DrawableRes;

import com.cxh.androidmedia.render_old.bean.BitmapTexture;

/**
 * Created by Cxh
 * Time : 2018-04-22  22:51
 * Desc :
 */
public class OpenGLUtils {

    public static final int NO_TEXTURE = -1;

    public static int loadShader(int type, String shaderCode) {
        // 根据type创建顶点着色器或片元着色器
        int shader = GLES30.glCreateShader(type);
        // 将资源加入到着色器中并编译
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        // 获取log
        int[] status = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, status, 0);
        if (0 == status[0]) {
            String logInfo = GLES30.glGetShaderInfoLog(shader);
            CCLog.i("shader error : " + logInfo);
            //创建失败
            GLES30.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    public static int loadProgram(String vertexShader, String fragShader) {
        int programId = GLES30.glCreateProgram();
        int vsh = loadShader(GLES30.GL_VERTEX_SHADER, vertexShader);
        int fsh = loadShader(GLES30.GL_FRAGMENT_SHADER, fragShader);

        // 链接shader
        GLES30.glAttachShader(programId, vsh);
        GLES30.glAttachShader(programId, fsh);
        GLES30.glLinkProgram(programId);
        GLES30.glValidateProgram(programId);
        // 删除shader代码
        GLES30.glDeleteShader(vsh);
        GLES30.glDeleteShader(fsh);
        // 获取log
        int[] status = new int[1];
        GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, status, 0);
        if (0 == status[0]) {
            String logInfo = GLES30.glGetProgramInfoLog(programId);
            CCLog.i("program error : " + programId + " : " + logInfo);
            GLES30.glDeleteProgram(programId);
            return 0;
        }
        return programId;
    }


    public static BitmapTexture loadTexture(Context context, @DrawableRes int drawable) {
        return loadTexture(BitmapFactory.decodeResource(context.getResources(), drawable));
    }

    public static BitmapTexture loadTexture(Bitmap bitmap) {
        final int[] texture = new int[1];
        BitmapTexture bitmapTexture = new BitmapTexture();
        //创建一个纹理对象
        GLES30.glGenTextures(1, texture, 0);
        if (texture[0] <= 0) {
            CCLog.i("load texture failed...");
            return bitmapTexture;
        }

        bitmapTexture.mBitmapWidth = bitmap.getWidth();
        bitmapTexture.mBitmapHeight = bitmap.getHeight();
        // 绑定纹理到OpenGL
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture[0]);
        // 设置默认的纹理过滤参数，抗锯齿等
        // 对于缩小过滤GL_TEXTURE_MIN_FILTER
        // GL_NEAREST 表示根据纹理坐标，获取最近的一个纹理样本
        // GL_LINEAR 根据纹理坐标，采用2次采样方法，获取纹理样本
        // GL_NEAREST_MIPMAP_NEAREST 根据纹理坐标，从最近的mip贴图层中获取一个点样本
        // GL_NEAREST_MIPMAP_LINEAR 根据纹理坐标，获取2个最近的mip图层的采样点，并取2个样本之间的插值
        // GL_LINEAR_MIPMAP_NEAREST 根据纹理坐标，采用二次线性插值从最近的mip层中获取一个点样本
        // GL_LINEAR_MIPMAP_LINEAR 根据纹理坐标，获取最近2个mip图层中采用二次线性获取的采用点，并取2个采用点之间的插值
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        // 对于放大过滤GL_TEXTURE_MAG_FILTER,可以设置为GL_NEAREST或者是GL_LINEAR。
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        // GL_REPEAT（重复边界纹理）、GL_CLAMP_TO_EDGE、GL_CLAMP_TO_BORDER（黑色）、GL_TEXTURE_BORDER_COLOR（指定颜色）
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        // GL_REPEAT（重复边界纹理）、GL_CLAMP_TO_EDGE、GL_CLAMP_TO_BORDER
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        //加载bitmap到纹理中
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        // 生成MIP贴图
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);

        bitmap.recycle();
        //取消绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        bitmapTexture.mTextureId = texture[0];

        return bitmapTexture;
    }

    public static BitmapTexture createEmptyTexture(int width, int height) {
        final int[] texture = new int[1];
        BitmapTexture bitmapTexture = new BitmapTexture();
        bitmapTexture.mBitmapWidth = width;
        bitmapTexture.mBitmapHeight = height;
        //创建一个纹理对象
        GLES30.glGenTextures(1, texture, 0);
        if (texture[0] <= 0) {
            CCLog.i("load texture failed...");
            return bitmapTexture;
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture[0]);
        // 采样
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        // 边界
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        // 指定内存大小
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        bitmapTexture.mTextureId = texture[0];
        return bitmapTexture;
    }

    public static int createOESTexture() {
        final int[] textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        if (0 == textures[0]) {
            CCLog.i(" createOESTexture error : " + textures[0]);
        }
        return textures[0];
    }

    /**
     * 渲染缓冲区的参数
     * <p>
     * Buffer Usage	Description
     * GL_STREAM_DRAW	缓冲区内容将由应用程序设置一次，并且不经常用于绘图
     * GL_STREAM_READ	缓冲区内容将被设置一次，作为 OpenGL 的输出，并且不经常用于绘图
     * GL_STREAM_COPY	缓冲区内容将被设置一次，作为 OpenGL 的输出，并且不经常用于绘制或复制到其他图像
     * GL_STATIC_DRAW	缓冲区内容将由应用程序设置一次，并经常用于绘图或复制到其他图像
     * GL_STATIC_READ	缓冲区内容将被设置一次，作为 OpenGL 的输出，并被应用程序多次查询
     * GL_STATIC_COPY	缓冲区内容将被设置一次，作为 OpenGL 的输出，并经常用于绘制或复制到其他图像
     * GL_DYNAMIC_DRAW	缓冲区内容将由应用程序频繁更新，并经常用于绘制或复制到其他图像
     * GL_DYNAMIC_READ	缓冲区内容将作为 OpenGL 的输出频繁更新，并由应用程序多次查询
     * GL_DYNAMIC_COPY	缓冲区内容将作为 OpenGL 的输出频繁更新，并经常用于绘图或复制到其他图像
     */
    public static int[] initVbo(float[] array) {
        int[] vbo = new int[1];
        GLES30.glGenBuffers(1, vbo, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
        // 这里需要注意，size字段传的是第三个参数Buffer的大小，一个float四个字节
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, array.length * 4, BitsUtil.arraysToBuffer(array), GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    public static int[] initElementVbo(int[] array) {
        int[] vbo = new int[1];
        GLES30.glGenBuffers(1, vbo, 0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, vbo[0]);
        // 这里需要注意，size字段传的是第三个参数Buffer的大小，一个float四个字节
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, array.length * 4, BitsUtil.arraysToBuffer(array), GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
        return vbo;
    }

    public static int createFBO(int textureID) {
        int[] fboIds = new int[1];
        GLES30.glGenFramebuffers(1, fboIds, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboIds[0]);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textureID, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        return fboIds[0];
    }


    /**
     * OpenGL ES 3.0才支持
     *
     * @param attachment
     * @param width
     * @param height
     * @return
     */
    public static int[] initPbo(int attachment, int width, int height) {
        int[] pbo = new int[1];
        // RGBA
        int imageDataSize = width * height * 4;
        GLES30.glGenBuffers(1, pbo, 0);
        GLES30.glBindBuffer(attachment, pbo[0]);
        GLES30.glBufferData(attachment, imageDataSize, null, GLES30.GL_STREAM_DRAW);
        GLES30.glBindBuffer(attachment, 0);
        return pbo;
    }

    public static void setUnifrom1i(int programId, String name, int value) {
        int location = GLES30.glGetUniformLocation(programId, name);
        GLES30.glUniform1i(location, value);
    }

    public static void setUnifrom1f(int programId, String name, float value) {
        int location = GLES30.glGetUniformLocation(programId, name);
        GLES30.glUniform1f(location, value);
    }

    public static void setUnifrom2f(int programId, String name, float[] value) {
        int location = GLES30.glGetUniformLocation(programId, name);
        GLES30.glUniform2f(location, value[0], value[1]);
    }

    public static void setUnifrom2fv(int programId, String name, float[] value) {
        if (null == value || value.length != 2) {
            value = new float[]{0, 0};
        }
        int location = GLES30.glGetUniformLocation(programId, name);
        GLES30.glUniform2fv(location, 1, value, 0);
    }

    public static void setUniformMatrix4fv(int programId, String name, float[] matrix) {
        if (null != matrix && matrix.length == 16) {
            int location = GLES30.glGetUniformLocation(programId, name);
            GLES30.glUniformMatrix4fv(location, 1, false, matrix, 0);
        } else {
            CCLog.e("setUniformMatrix4fv, invalid matrix!");
        }
    }

    public static void checkGLError() {
        CCLog.i("checkGLError, code: " + GLES30.glGetError());
    }
}
