package com.cxh.androidmedia.utils;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

/**
 * Created by Cxh
 * Time : 2021/5/29  01:29
 * Desc :
 */
public class MediaStoreUtil {

    public static void saveImageToMediaStore(Context context, String imagePath, String fileName, String fileDesc) {
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), imagePath, fileName, fileDesc);
            refreshFile(context, imagePath, new String[]{"image/jpeg"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ContentValues localContentValues = new ContentValues();
     * localContentValues.put("title", paramFile.getName());
     * localContentValues.put("_display_name", paramFile.getName());
     * localContentValues.put("mime_type", "video/mp4");
     * localContentValues.put("datetaken", Long.valueOf(paramLong));
     * localContentValues.put("date_modified", Long.valueOf(paramLong));
     * localContentValues.put("date_added", Long.valueOf(paramLong));
     * localContentValues.put("_data", paramFile.getAbsolutePath());
     * localContentValues.put("_size", Long.valueOf(paramFile.length()));
     * return localContentValues;
     * ————————————————
     * 版权声明：本文为CSDN博主「淡淡的香烟」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/u012556114/article/details/105649287
     *
     * @param context
     * @param videoPath
     * @param desc
     */

    public static void saveVideoToMediaStore(Context context, String videoPath, String desc) {
        CCLog.i("saveVideoToMediaStore, videoPath: " + videoPath + " , desc: " + desc);

        ContentProviderClient contentProviderClient = null;
        Uri localUri = null;
        try {
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            contentProviderClient = context.getContentResolver().acquireContentProviderClient(uri);

            if (null != contentProviderClient) {
                File file = new File(videoPath);
                long timeStamp = System.currentTimeMillis();

                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Video.Media.TITLE, file.getName());
                contentValues.put(MediaStore.Video.Media.DATA, file.getAbsolutePath());
                contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, file.getName());
                contentValues.put(MediaStore.Video.Media.DESCRIPTION, desc);
                contentValues.put(MediaStore.Video.Media.SIZE, file.length());
                contentValues.put(MediaStore.Video.Media.DATE_ADDED, timeStamp);
                contentValues.put(MediaStore.Video.Media.DATE_MODIFIED, timeStamp);
                contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                if (Build.VERSION.SDK_INT >= 29) {
                    contentValues.put(MediaStore.Video.Media.DATE_TAKEN, timeStamp);
                }

                localUri = contentProviderClient.insert(uri, contentValues);

                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri));

                refreshFile(context, videoPath, new String[]{"video/mp4"});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != contentProviderClient) {
                contentProviderClient.close();
            }
        }
    }

    public static void refreshFile(Context context, String path, String[] mimeTypes) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{path}, mimeTypes,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            CCLog.i("onScanCompleted, path: " + path + " , uri: " + uri);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
