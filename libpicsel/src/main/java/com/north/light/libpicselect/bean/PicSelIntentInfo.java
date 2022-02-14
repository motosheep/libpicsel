package com.north.light.libpicselect.bean;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * pic select adapter 选中的图片
 */
public class PicSelIntentInfo {
    private static final String TAG = PicSelIntentInfo.class.getName();
    //浏览集合
    private volatile List<PicInfo> mIntentList = new ArrayList<>();
    //传递需要浏览的图片
    private List<String> picList = new ArrayList<>();

    private static class SingleHolder {
        static PicSelIntentInfo mInstance = new PicSelIntentInfo();
    }

    public static PicSelIntentInfo getInstance() {
        return SingleHolder.mInstance;
    }

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

    /**
     * 查询是否选中
     */
    public boolean isSel(String path) {
        try {
            for (PicInfo cache : mIntentList) {
                if (cache.getPath().equals(path)) {
                    return cache.isSelect();
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 设置是否选中
     */
    public void setSelPic(String path, int position, boolean isSel, int limit, SelCountListener listener) {
        try {
            int selectCount = 0;
            for (PicInfo cache : mIntentList) {
                if (cache.isSelect()) {
                    selectCount++;
                }
            }
            if (selectCount >= limit && isSel) {
                if (listener != null) {
                    listener.limit();
                    return;
                }
            }
            if (this.mIntentList.get(position).getPath().equals(path)) {
                this.mIntentList.get(position).setSelect(isSel);
                if (listener != null) {
                    listener.selCount(isSel ? ++selectCount : --selectCount);
                }
            } else {
                this.mIntentList.get(position).setSelect(false);
            }
        } catch (Exception e) {
            Log.d(TAG, "sel pic error: " + e.getMessage());
        }
    }


    /**
     * 设置数据
     */
    public void setPicSelList(List<PicInfo> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.mIntentList = list;
    }

    /**
     * 获取选择集合
     */
    public int selCount() {
        int selectCount = 0;
        for (PicInfo cache : mIntentList) {
            if (cache.isSelect()) {
                selectCount++;
            }
        }
        return selectCount;
    }

    /**
     * 获取图片集合
     */
    public List<PicInfo> getPicSelList() {
        return mIntentList;
    }

    /**
     * 接口
     */
    public interface SelCountListener {
        void selCount(int count);

        void limit();
    }
}
