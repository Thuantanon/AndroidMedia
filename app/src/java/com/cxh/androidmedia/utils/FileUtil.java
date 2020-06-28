package com.cxh.androidmedia.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.cxh.androidmedia.base.AMApp;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
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

    private static final String ROOT_PATH = getRootPath();

    public static final String PATH_AUDIO = ROOT_PATH + File.separator + "audio";
    public static final String PATH_VIDEO = ROOT_PATH + File.separator + "video";
    public static final String PATH_IMAGE = ROOT_PATH + File.separator + "image";

    static {

        File fileRoot = new File(ROOT_PATH);
        if (!fileRoot.exists() || !fileRoot.isDirectory()) {
            boolean result = fileRoot.mkdirs();
            CCLog.i(String.format("init dir %s result = " + result, fileRoot.getAbsoluteFile()));
        }

        File audioDir = new File(PATH_AUDIO);
        if (!audioDir.exists() || !audioDir.isDirectory()) {
            boolean result = audioDir.mkdirs();
            CCLog.i(String.format("init dir %s result = " + result, audioDir.getAbsoluteFile()));
        }

        File videoDir = new File(PATH_VIDEO);
        if (!videoDir.exists() || !videoDir.isDirectory()) {
            boolean result = videoDir.mkdirs();
            CCLog.i(String.format("init dir %s result = " + result, videoDir.getAbsoluteFile()));
        }

        File imageDir = new File(PATH_IMAGE);
        if (!imageDir.exists() || !imageDir.isDirectory()) {
            boolean result = imageDir.mkdirs();
            CCLog.i(String.format("init dir %s result = " + result, imageDir.getAbsoluteFile()));
        }
    }

    public static String getRootPath() {
        String rootDir = null;
        // Android Q新模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy()) {
            File rootFile = AMApp.get().getExternalFilesDir("");
            // 去不到就用内存
            if(null != rootFile){
                rootDir = rootFile.getAbsolutePath();
            }else {
                rootDir = AMApp.get().getFilesDir().getAbsolutePath();
            }
        } else {
            rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return rootDir;
    }

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

    public static void saveBitmapToStorage(int[] data, int width, int height, String path) {
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(data, width, height, Bitmap.Config.ARGB_8888);
            saveBitmapToStorage(bitmap, path);
        } finally {
            if (null != bitmap) {
                bitmap.recycle();
            }
        }
    }

    public static void saveBitmapToStorage(Bitmap bitmap, String path) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            CCLog.i("图像已写入到：" + path);
        } catch (Exception e) {
            CCLog.i("写入失败：" + e.toString());
        } finally {
            tryClose(outputStream);
        }
    }
}
