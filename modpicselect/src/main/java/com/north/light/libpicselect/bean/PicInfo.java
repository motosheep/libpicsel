package com.north.light.libpicselect.bean;

import com.north.light.libpicselect.utils.PicDirectoryUtils;

import java.io.Serializable;

/**
 * create by lzt
 * data 2019/12/8
 * 图片的信息
 */
public class PicInfo implements Serializable {
    private String name;//文件名
    private String path;//文件路径
    private String directory;//目录
    private long directoryCount;//目录下文件个数
    private boolean isSelect = false;
    private int date;//日期__修改日期

    public PicInfo() {
    }

    public PicInfo(String name, String path, int date) {
        this.name = name;
        this.path = path;
        this.directory = PicDirectoryUtils.getDirectory(path);
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setDirectoryCount(long directoryCount) {
        this.directoryCount = directoryCount;
    }

    public long getDirectoryCount() {
        return directoryCount;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
