package com.cxh.androidmedia.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Cxh
 * Time : 2019-02-26  21:29
 * Desc :
 */
public class BitsUtil {

    public static int byteToInt(byte[] bytes) {
        if (null != bytes) {
            if (bytes.length == 4) {
                return ((bytes[3]) << 24) | ((bytes[2] & 0xff) << 16) | ((bytes[1] & 0xff) << 8) | (bytes[0] & 0xff);
            } else if (bytes.length == 2) {
                return (bytes[1] << 8) | (bytes[0] & 0xff);
            } else if (bytes.length == 1) {
                return bytes[0];
            }
        }
        return 0;
    }


    public static IntBuffer arraysToBuffer(int[] buffer) {
        IntBuffer intBuffer;
        // Int占4个字节
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(buffer.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(buffer);
        intBuffer.position(0);
        return intBuffer;
    }

    public static IntBuffer arraysToBuffer(byte[] buffer) {
        IntBuffer intBuffer;
        // Int占4个字节
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byteBuffer.order(ByteOrder.nativeOrder());
        intBuffer = byteBuffer.asIntBuffer();
        intBuffer.position(0);
        return intBuffer;
    }

    public static FloatBuffer arraysToBuffer(float[] buffer) {
        FloatBuffer intBuffer;
        // Float占4个字节
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(buffer.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        intBuffer = byteBuffer.asFloatBuffer();
        intBuffer.put(buffer);
        intBuffer.position(0);
        return intBuffer;
    }

    public static ShortBuffer arraysToBuffer(short[] buffer) {
        ShortBuffer intBuffer;
        // Int占4个字节
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(buffer.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        intBuffer = byteBuffer.asShortBuffer();
        intBuffer.put(buffer);
        intBuffer.position(0);
        return intBuffer;
    }
}
