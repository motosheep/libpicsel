package com.north.light.libpicselect.constant;

import android.os.Environment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzt
 * time 2020/10/20
 * 描述：图片内存缓存类
 */
public class PicConstant implements Serializable {
    private String mCameraPath = Environment.getExternalStorageDirectory() + "/kkgj/camera/";
    private String mCropPath = Environment.getExternalStorageDirectory() + "/kkgj/crop/";
    private String mCopyPath = Environment.getExternalStorageDirectory() + "/kkgj/copy/";


    private static class SingleHolder implements Serializable {
        static final PicConstant instance = new PicConstant();
    }

    public static PicConstant getInstance() {
        return SingleHolder.instance;
    }

    //传递需要浏览的图片
    private List<String> picList = new ArrayList<>();

    public List<String> getPicList() {
        return picList;
    }

    public void setPicList(List<String> picList) {
        this.picList.clear();
        if (picList == null || picList.size() == 0) {
            return;
        }
        for (String pic : picList) {
            this.picList.add(pic);
        }
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
}
