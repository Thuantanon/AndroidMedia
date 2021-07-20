package com.cxh.androidmedia.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.cxh.androidmedia.base.AMApp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
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
    // 子目录
    // 截屏
    public static final String PATH_IMAGE_SHOT = PATH_IMAGE + File.separator + "screenshot";
    // 拍照
    public static final String PATH_IMAGE_PHOTO = PATH_IMAGE + File.separator + "photo";
    // 视频缓存目录
    public static final String PATH_VIDEO_CACHE = PATH_VIDEO + File.separator + "cache";
    // Face
    public static final String PATH_FACE = "FaceModel";
    // 人脸模型
    public static String PATH_FACE_MODELS = "";

    static {
        checkMediaDir(ROOT_PATH);
        checkMediaDir(PATH_AUDIO);
        checkMediaDir(PATH_VIDEO);
        checkMediaDir(PATH_IMAGE_SHOT);
        checkMediaDir(PATH_IMAGE_PHOTO);
        checkMediaDir(PATH_VIDEO_CACHE);

        PATH_FACE_MODELS = ROOT_PATH + File.separator + PATH_FACE;
    }

    public static String getRootPath() {
        String rootDir = null;
        // Android Q新模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy()) {
            File rootFile = AMApp.get().getExternalFilesDir("");
            // 去不到就用内存
            if (null != rootFile) {
                rootDir = rootFile.getAbsolutePath();
            } else {
                rootDir = AMApp.get().getFilesDir().getAbsolutePath();
            }
        } else {
            rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AndroidMedia";
        }
        return rootDir;
    }

    public static void checkMediaDir(String path) {
        File filePath = new File(path);
        if (!filePath.exists() || !filePath.isDirectory()) {
            boolean result = filePath.mkdirs();
            CCLog.i(String.format("init dir %s result = " + result, filePath.getAbsoluteFile()));
        }
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
    public static String readShaderFromAssets(Context context, String filePath) {
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

    /**
     * 读取assets文本文件
     *
     * @param filePath
     * @return
     */
    public static String readShaderFromAssets(String filePath) {
        return readShaderFromAssets(AMApp.get(), filePath);
    }


    public static boolean saveNV21ToStorage(byte[] data, int width, int height, String path, boolean face) {
        Bitmap sorceBitmap = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
            yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, byteArrayOutputStream);
            sorceBitmap = BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size());
            // 旋转
            Matrix matrix = new Matrix();
            if (face) {
                matrix.setScale(1, -1);
                Bitmap cacheBitmap = Bitmap.createBitmap(sorceBitmap, 0, 0, width, height, matrix, false);
                sorceBitmap.recycle();
                sorceBitmap = cacheBitmap;
                matrix.reset();
                matrix.setRotate(270);
            } else {
                matrix.setRotate(90);
            }
            Bitmap bitmap = Bitmap.createBitmap(sorceBitmap, 0, 0, width, height, matrix, false);
            saveBitmapToStorage(bitmap, path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            CCLog.i(e.toString());
        } finally {
            if (null != sorceBitmap) {
                sorceBitmap.recycle();
            }
            tryClose(byteArrayOutputStream);
        }
        return false;
    }

    public static void saveByteArrayToStorage(byte[] data, String path) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(data);
            fileOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fileOutputStream) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean saveBitmapToStorage(Bitmap bitmap, String path) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            CCLog.i("图像已写入到：" + path);
            return true;
        } catch (Exception e) {
            CCLog.i("写入失败：" + e.toString());
        } finally {
            tryClose(outputStream);
        }
        return false;
    }

    public static boolean saveImagePicture(@NonNull Image image, String path) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tryClose(fileOutputStream);
        }
        return false;
    }

    public static String copyFilesFromAssets(Context context, String oldPath, String newPath) {
        try {
            String[] fileNames = context.getAssets().list(oldPath);
            if (null != fileNames && fileNames.length > 0) {
                // directory
                File file = new File(newPath);
                if (!file.exists()) {
                    file.mkdirs();
                }

                for (String fileName : fileNames) {
                    copyFilesFromAssets(context, oldPath + "/" + fileName,
                            newPath + "/" + fileName);
                }
            } else {
                // file
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }

            return newPath;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
