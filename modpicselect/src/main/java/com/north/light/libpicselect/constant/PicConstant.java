package com.north.light.libpicselect.constant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzt
 * time 2020/10/20
 * 描述：图片内存缓存类
 */
public class PicConstant implements Serializable {
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
        this.picList = picList;
    }
}
