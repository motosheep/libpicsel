package com.north.light.libpicselect.constant;

import android.os.Environment;

import java.io.Serializable;

/**
 * Created by lzt
 * time 2020/10/20
 * 描述：图片内存缓存类
 */
public class LibPicConstant implements Serializable {
    private String mCameraPath = Environment.getExternalStorageDirectory() + "/kkgj/camera/";
    private String mCropPath = Environment.getExternalStorageDirectory() + "/kkgj/crop/";
    private String mCopyPath = Environment.getExternalStorageDirectory() + "/kkgj/copy/";
    private String mRecordVideo = Environment.getExternalStorageDirectory() + "/kkgj/recordvideo/";


    private static class SingleHolder implements Serializable {
        static final LibPicConstant instance = new LibPicConstant();
    }

    public static LibPicConstant getInstance() {
        return SingleHolder.instance;
    }


    public String getRecordVideo() {
        return mRecordVideo;
    }

    public void setRecordVideo(String mRecordVideo) {
        this.mRecordVideo = mRecordVideo;
    }

    public String getCameraPath() {
        return mCameraPath;
    }

    public String getCopyPath() {
        return mCopyPath;
    }

    public String getCropPath() {
        return mCropPath;
    }

    public void setCameraPath(String path) {
        mCameraPath = path;
    }

    public void setCopyPath(String mCopyPath) {
        this.mCopyPath = mCopyPath;
    }

    public void setCropPath(String mCropPath) {
        this.mCropPath = mCropPath;
    }
}
