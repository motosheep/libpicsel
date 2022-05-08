package com.north.light.libpicselect.bean;

import com.north.light.libpicselect.constant.LibPicIntentCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:li
 * date:2022/4/3
 * desc:图片选择中间件信息类
 */
public class LibPicSelMidInfo implements Serializable {
    private Map<String, Object> mWrapperMap = new HashMap<>();
    /**
     * 请求TYPE
     */
    private int REQ_TYPE = LibPicIntentCode.DEFAULT_REQ;

    /**
     * intent 请求flag
     */
    private List<Integer> REQ_FLAG = new ArrayList<>();
    /**
     * intent 请求action
     */
    private List<String> REQ_ACTION = new ArrayList<>();

    /**
     * 初始化wrapper
     */
    public void putWrapper(String key, Object value) {
        mWrapperMap.put(key, value);
    }

    /**
     * 获取wrapper
     */
    public Map<String, Object> getWrapper() {
        return mWrapperMap;
    }

    /**
     * 系统剪裁图片的源路径--------------------------------------------------------------------
     * */
    private String mCropPicSourcePath;
    private String mCropPicTargetPath;
    /**
     * 系统拍摄图片的源路径--------------------------------------------------------------------
     * */
    private String mTakePicTargetPath;

    /**
     * 自定义剪裁图片的源路径--------------------------------------------------------------------
     * */
    private String mCusCropPicSourcePath;
    private String mCusCropPicTargetPath;

    public int getREQ_TYPE() {
        return REQ_TYPE;
    }

    public void setREQ_TYPE(int REQ_TYPE) {
        this.REQ_TYPE = REQ_TYPE;
    }

    public void addFlag(int flag) {
        REQ_FLAG.add(flag);
    }

    public List<Integer> getREQ_FLAG() {
        return REQ_FLAG;
    }

    public void addAction(String action) {
        REQ_ACTION.add(action);
    }

    public List<String> getREQ_ACTION() {
        return REQ_ACTION;
    }

    public String getCropPicSourcePath() {
        return mCropPicSourcePath;
    }

    public void setCropPicSourcePath(String mCropPicPath) {
        this.mCropPicSourcePath = mCropPicPath;
    }

    public String getCropPicTargetPath() {
        return mCropPicTargetPath;
    }

    public void setCropPicTargetPath(String mCropPicTargetPath) {
        this.mCropPicTargetPath = mCropPicTargetPath;
    }

    public String getTakePicTargetPath() {
        return mTakePicTargetPath;
    }

    public void setTakePicTargetPath(String mTakePicTargetPath) {
        this.mTakePicTargetPath = mTakePicTargetPath;
    }

    public String getCusCropPicSourcePath() {
        return mCusCropPicSourcePath;
    }

    public void setCusCropPicSourcePath(String mCusCropPicSourcePath) {
        this.mCusCropPicSourcePath = mCusCropPicSourcePath;
    }

    public String getCusCropPicTargetPath() {
        return mCusCropPicTargetPath;
    }

    public void setCusCropPicTargetPath(String mCusCropPicTargetPath) {
        this.mCusCropPicTargetPath = mCusCropPicTargetPath;
    }
}
