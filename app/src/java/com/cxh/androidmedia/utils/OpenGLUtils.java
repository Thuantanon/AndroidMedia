package com.cxh.androidmedia.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import androidx.annotation.DrawableRes;

import com.cxh.androidmedia.render.bean.BitmapTexture;

/**
 * Created by Cxh
 * Time : 2018-04-22  22:51
 * Desc :
 */
public class OpenGLUtils {

    public static BitmapTexture loadTexture(Context context, @DrawableRes int drawable){
        final int[] texture = new int[1];
        BitmapTexture bitmapTexture = new BitmapTexture();
        //创建一个纹理对象
        GLES30.glGenTextures(1, texture, 0);
        if(texture[0] <= 0){
            CCLog.i("load texture failed...");
            return bitmapTexture;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawable, options);
        if(null == bitmap){
            CCLog.i("decode bitmap failed...");
            return bitmapTexture;
        }

        bitmapTexture.mBitmapWidth = bitmap.getWidth();
        bitmapTexture.mBitmapHeight = bitmap.getHeight();
        //绑定纹理到OpenGL
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture[0]);
        //设置默认的纹理过滤参数，抗锯齿等
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
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
}
