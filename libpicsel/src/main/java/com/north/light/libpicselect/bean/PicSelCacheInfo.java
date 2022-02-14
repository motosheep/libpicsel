package com.north.light.libpicselect.bean;

import java.io.Serializable;

/**
 * @Author: lzt
 * @Date: 2022/2/14 10:59
 * @Description:选择图片缓存信息
 */
public class PicSelCacheInfo implements Serializable {
    //路径
    private String path;
    private long clickTime;

    public PicSelCacheInfo(String path, long clickTime) {
        this.path = path;
        this.clickTime = clickTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getClickTime() {
        return clickTime;
    }

    public void setClickTime(long clickTime) {
        this.clickTime = clickTime;
    }
}
