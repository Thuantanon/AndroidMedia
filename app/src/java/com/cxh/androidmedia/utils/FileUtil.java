package com.cxh.androidmedia.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cxh
 * Time : 2018-09-23  17:30
 * Desc :
 */
public class FileUtil {

    private static final String ROOT_NAME = "andmedia";
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ROOT_NAME;

    public static final String PATH_AUDIO = ROOT_PATH + File.separator + "audio";
    public static final String PATH_VIDEO = ROOT_PATH + File.separator + "video";
    public static final String PATH_IMAGE = ROOT_PATH + File.separator + "image";


    public static String getRandomPCMFile() {
        File file = new File(PATH_AUDIO);
        if (!file.exists()) {
            boolean b = file.mkdirs();
        }
        return PATH_AUDIO + File.separator + getTimeFormat() + ".pcm";
    }

    public static String getTimeFormat() {
        return new SimpleDateFormat("yyyy_MM_dd_hhmmss", Locale.CHINA).format(new Date());
    }

    public static void tryClose(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static String getFileSize(String file) {
        return getFileSize(new File(file));
    }


    public static String getFileSize(File file) {
        if (null == file || !file.exists()) {
            return "0KB";
        }
        int fileSizeM = (int) (file.length() / 1000);
        return fileSizeM + "KB";
    }

    public static boolean makeFile(String filePath) throws IOException {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        boolean createFile = true;
        if (!file.exists()) {
            if (null != file.getParentFile() && !file.getParentFile().exists()) {
                createFile = file.getParentFile().mkdirs();
            }
            createFile = file.createNewFile();
        }
        return createFile;
    }


    /**
     * 读取assets文本文件
     *
     * @param context
     * @param filePath
     * @return
     */
    public static String readRenderScriptFromAssets(Context context, String filePath) {
        InputStream in = null;
        try {
            in = context.getAssets().open(filePath);
            byte[] data = new byte[in.available()];
            int result = in.read(data);
            if (result > 0) {
                return new String(data, StandardCharsets.UTF_8.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tryClose(in);
        }
        return "";
    }
}
