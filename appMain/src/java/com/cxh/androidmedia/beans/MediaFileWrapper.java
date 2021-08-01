package com.cxh.androidmedia.beans;

import java.io.File;

/**
 * Created by Cxh
 * Time : 2021/7/30  23:04
 * Desc :
 */
public class MediaFileWrapper {

    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_AUDIO = 2;

    private File mFile;
    private int mType;

    public MediaFileWrapper(File file, int type) {
        mFile = file;
        mType = type;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
