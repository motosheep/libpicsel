package com.north.light.libpicselect.bean;

import com.north.light.libpicselect.utils.LibPicPicDirectoryUtils;

import java.io.Serializable;

/**
 * create by lzt
 * data 2019/12/8
 * 图片or视频的信息
 * change by lzt 20200823 增加数据类型的变量
 */
public class LibPicInfo implements Serializable {
    private String name;//文件名
    private String path;//文件路径
    private String directory;//目录
    private long directoryCount;//目录下文件个数
    private SelInfo isSelect = new SelInfo();
    private int date;//日期__修改日期
    private int source;//1图片 2视频

    public LibPicInfo() {
    }

    public LibPicInfo(String name, String path, int date, int source) {
        this.name = name;
        this.path = path;
        this.directory = LibPicPicDirectoryUtils.getDirectory(path);
        this.date = date;
        this.source = source;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
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
        return isSelect.isSel();
    }

    public long getSelTime(){
        return isSelect.getSelTime();
    }

    public void setSelect(boolean select) {
        isSelect.setSel(select);
        isSelect.setSelTime(System.currentTimeMillis());
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    /**
     * 选择时间
     */
    public class SelInfo implements Serializable {
        private boolean isSel = false;
        private long selTime = 0;

        public boolean isSel() {
            return isSel;
        }

        public void setSel(boolean sel) {
            isSel = sel;
        }

        public long getSelTime() {
            return selTime;
        }

        public void setSelTime(long selTime) {
            this.selTime = selTime;
        }
    }
}
