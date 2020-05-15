package com.north.light.libpicselect.bean;

/**
 * create by lzt
 * data 2019/12/8
 * 目录相关信息
 */
public class DirecotryBean {
    private String name;//名字
    private String cover;//封面
    private long count;//数量

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
